package com.gracehopper.laserchessapp.data.manager

import com.google.gson.Gson
import com.gracehopper.laserchessapp.data.model.game.GameEvent
import com.gracehopper.laserchessapp.data.model.game.GameMessageType
import com.gracehopper.laserchessapp.data.model.game.WSServerMessage
import com.gracehopper.laserchessapp.data.remote.websocket.FriendlyGameWebSocket
import com.gracehopper.laserchessapp.data.remote.websocket.FriendlyGameWebSocketListener
import com.gracehopper.laserchessapp.utils.TokenManager

/**
 * Objeto singleton que gestiona el estado de una partida activa multijugador.
 */
object ActiveGameManager {

    /**
     * Estados posibles de la partida
     */
    enum class GameState {
        INACTIVE,
        CONNECTING,
        WAITING_ACCEPTANCE,
        STARTING_GAME,
        IN_GAME,
        CLOSED,
        ERROR
    }

    private var friendlyGameWebSocket: FriendlyGameWebSocket? = null
    private var isReconnecting = false

    var isFriendlyGame: Boolean = false
        private set

    var currentOpponentUsername: String? = null
        private set

    var currentBoard: Int? = null
        private set

    var currentStartingTime: Int? = null
        private set

    var currentTimeIncrement: Int? = null
        private set
    var reconnectingOpponentId: Long? = null
        private set

    var pendingStateLog: String? = null
        private set

    private var reconnectGotInitialState = false
    private var reconnectGotState = false
    private var awaitingReconnectMessages = false

    var intialBoardCSV: String? = null
        private set

    var imRedPlayer: Boolean = true
        private set

    var currentState: GameState = GameState.INACTIVE
        private set

    var lastError: String? = null
        private set

    private var onConnectedCallback: (() -> Unit)? = null
    private var onMessageReceivedCallback: ((GameEvent) -> Unit)? = null
    // Para errores del socket (de conexión, refresh...)
    private var onErrorCallback: ((String) -> Unit)? = null
    private var onClosedCallback: (() -> Unit)? = null

    /**
     * Establece los callbacks de la conexión.
     *
     * @param onConnected Callback al conectar correctamente
     * @param onMessageReceived Callback al recibir un mensaje del servidor
     * @param onError Callback en caso de error
     * @param onClosed Callback al cerrarse la conexión
     */
    fun setCallbacks(
        onConnected: (() -> Unit)? = null,
        onMessageReceived: ((GameEvent) -> Unit)? = null,
        onError: ((String) -> Unit)? = null,
        onClosed: (() -> Unit)? = null
    ) {

        onConnectedCallback = onConnected
        onMessageReceivedCallback = onMessageReceived
        onErrorCallback = onError
        onClosedCallback = onClosed

    }

    /**
     * Elimina todos los callbacks registrados.
     */
    fun clearCallbacks() {
        onConnectedCallback = null
        onMessageReceivedCallback = null
        onErrorCallback = null
        onClosedCallback = null
    }

    /**
     * Procesa un mensaje recibido del servidor.
     *
     * @param message Mensaje en formato JSON
     */
    fun handleServerMessage(message: String) {
        val gson = Gson()
        val serverMsg = gson.fromJson(message, WSServerMessage::class.java)

        when (serverMsg.type) {

            /**
             * Estado inicial de la partida:
             * - Se guarda el tablero inicial
             * - Se determina si el jugador es rojo
             */
            GameMessageType.INITIAL_STATE -> {
                intialBoardCSV = serverMsg.content

                val redPlayerId = serverMsg.extra?.toLongOrNull()
                val myId = TokenManager.getUserId()

                imRedPlayer = (redPlayerId == myId)

                if (awaitingReconnectMessages) {
                    // Reconexión: guardar y esperar a tener ambos mensajes
                    reconnectGotInitialState = true
                    dispatchReconnectIfReady()
                } else {
                    // Partida en curso normal (GameActivity ya escucha)
                    onMessageReceivedCallback?.invoke(
                        GameEvent.InitialState(
                            boardCsv = intialBoardCSV,
                            redPlayerId = redPlayerId
                        )
                    )
                }
            }

            /**
             * Movimiento de partida
             */
            GameMessageType.MOVE -> {
                onMessageReceivedCallback?.invoke(GameEvent.Move(
                    moveAndTime = serverMsg.content.orEmpty()))
            }

            /**
             * Estado de la partida
             */
            GameMessageType.STATE -> {
                val log = serverMsg.content.orEmpty()
                if (awaitingReconnectMessages) {
                    // Reconexión: guardar log y esperar a tener ambos mensajes
                    pendingStateLog = log
                    reconnectGotState = true
                    dispatchReconnectIfReady()
                } else {
                    onMessageReceivedCallback?.invoke(GameEvent.State(log = log))
                }
            }

            /**
             * Petición de pausa
             */
            GameMessageType.PAUSE_REQUEST -> {
                onMessageReceivedCallback?.invoke(GameEvent.PauseRequest)
            }

            /**
             * Rechazo de pausa
             */
            GameMessageType.PAUSE_REJECT -> {
                onMessageReceivedCallback?.invoke(GameEvent.PauseReject)
            }

            /**
             * Partida pausada
             */
            GameMessageType.PAUSED -> {
                onMessageReceivedCallback?.invoke(GameEvent.Paused)
            }

            /**
             * Fin de partida
             */
            GameMessageType.END -> {
                onMessageReceivedCallback?.invoke(GameEvent.End(
                    winner = serverMsg.content.orEmpty(),
                    victoryCause = serverMsg.extra.orEmpty()
                ))
            }

            /**
             * Error en el juego
             */
            GameMessageType.ERROR -> {
                onMessageReceivedCallback?.invoke(
                    GameEvent.Error(serverMsg.content ?: "Error desconocido")
                )
            }

            /**
             * End Of Connection → cerrar conexión
             */
            GameMessageType.EOC -> {
                if (serverMsg.content == "Challenge rejected") {
                    onMessageReceivedCallback?.invoke(GameEvent.ChallengeRejected)
                } else {
                    onMessageReceivedCallback?.invoke(
                        GameEvent.ConnectionClosed(serverMsg.content)
                    )
                }
                closeConnection()
            }

            /**
             * Rival desconectado
             */
            GameMessageType.DISCONNECTION -> {
                onMessageReceivedCallback?.invoke(GameEvent.OpponentDisconnected)
            }

            /**
             * Rival reconectado
             */
            GameMessageType.RECONNECTION -> {
                val myTimeMs = serverMsg.extra?.toLongOrNull()
                if (myTimeMs != null) {
                    // Es nuestra propia reconexión: content=ID rival, extra=tiempo restante ms
                    currentStartingTime = (myTimeMs / 1000).toInt()
                    reconnectingOpponentId = serverMsg.content?.toLongOrNull()
                    // A partir de aquí esperamos InitialState + State antes de navegar
                    awaitingReconnectMessages = true
                    onMessageReceivedCallback?.invoke(
                        GameEvent.Reconnected(
                            opponentId = serverMsg.content,
                            remainingTime = serverMsg.extra
                        )
                    )
                } else {
                    // Es el rival quien se ha reconectado
                    onMessageReceivedCallback?.invoke(GameEvent.OpponentReconnected)
                }
            }

            else -> {
                // ignorar
            }

        }
    }

    /**
     * Establece el tipo de partida.
     */
    fun setGameType(isFriendly: Boolean) {
        isFriendlyGame = isFriendly
    }

    /**
     * Crea un reto contra otro jugador.
     */
    fun createChallenge(
        challengedUsername: String,
        board: Int,
        startingTime: Int,
        timeIncrement: Int
    ) {

        resetConnectionOnly()

        setGameType(true)                   // La partida es amistosa
        currentOpponentUsername = challengedUsername
        currentBoard = board
        currentStartingTime = startingTime
        currentTimeIncrement = timeIncrement
        currentState = GameState.CONNECTING
        lastError = null

        val listener = buildListener(
            onOpenState = GameState.WAITING_ACCEPTANCE
        )

        friendlyGameWebSocket = FriendlyGameWebSocket(listener)
        friendlyGameWebSocket?.createChallenge(
            challengedUsername,
            board, startingTime, timeIncrement
        )

    }

    /**
     * Acepta un reto recibido.
     */
    fun acceptChallenge(
        challengerUsername: String,
        board: Int,
        startingTime: Int,
        timeIncrement: Int
    ) {

        resetConnectionOnly()

        setGameType(true)                   // La partida es amistosa
        currentOpponentUsername = challengerUsername
        currentBoard = board
        currentStartingTime = startingTime/1000
        currentTimeIncrement = timeIncrement
        currentState = GameState.CONNECTING
        lastError = null

        val listener = buildListener(
            onOpenState = GameState.STARTING_GAME
        )

        friendlyGameWebSocket = FriendlyGameWebSocket(listener)
        friendlyGameWebSocket?.acceptChallenge(challengerUsername)

    }

    /**
     * Rechaza un reto recibido.
     */
    fun rejectChallenge(challengerUsername: String) {

        resetConnectionOnly()

        currentOpponentUsername = challengerUsername
        currentState = GameState.CONNECTING
        lastError = null

        val listener = buildListener(
            onOpenState = GameState.CLOSED
        )

        friendlyGameWebSocket = FriendlyGameWebSocket(listener)
        friendlyGameWebSocket?.rejectChallenge(challengerUsername)

    }

    /**
     * Envía un mensaje de juego al servidor.
     */
    fun sendGameMessage(message: String) {
        friendlyGameWebSocket?.sendMessage(message)
    }

    /**
     * Marca la partida como iniciada.
     */
    fun markInGame() {
        currentState = GameState.IN_GAME
    }

    /**
     * Cierra la conexión actual.
     */
    fun closeConnection() {
        friendlyGameWebSocket?.close()
        friendlyGameWebSocket = null
        currentState = GameState.CLOSED
    }

    /**
     * Navega a GameActivity solo cuando han llegado AMBOS mensajes de reconexión
     * (InitialState y State), sin importar el orden en que lleguen.
     */
    private fun dispatchReconnectIfReady() {
        android.util.Log.d("RECONNECT", "dispatchReconnectIfReady: gotInitial=$reconnectGotInitialState gotState=$reconnectGotState pendingLog='$pendingStateLog' csv=${intialBoardCSV != null}")
        if (reconnectGotInitialState && reconnectGotState) {
            android.util.Log.d("RECONNECT", "Ambos recibidos → navegando a GameActivity")
            awaitingReconnectMessages = false
            currentState = GameState.IN_GAME
            onMessageReceivedCallback?.invoke(
                GameEvent.State(log = pendingStateLog.orEmpty())
            )
        }
    }

    fun reconnectGame() {

        // Si ya hay una reconexión en curso o una partida activa, no hacer nada.
        if (currentState == GameState.CONNECTING || currentState == GameState.IN_GAME) return

        resetConnectionOnly()

        isReconnecting = true
        reconnectGotInitialState = false
        reconnectGotState = false
        pendingStateLog = null
        awaitingReconnectMessages = false
        currentState = GameState.CONNECTING

        val token = TokenManager.getAccessToken() ?: return

        val listener = buildListener(
            onOpenState = GameState.CONNECTING
        )

        friendlyGameWebSocket = FriendlyGameWebSocket(listener)
        friendlyGameWebSocket?.reconnect(token)
    }

    /**
     * Resetea completamente el estado del manager.
     */
    fun resetAll() {
        friendlyGameWebSocket?.close()
        friendlyGameWebSocket = null

        currentOpponentUsername = null
        currentBoard = null
        currentStartingTime = null
        currentTimeIncrement = null
        reconnectingOpponentId = null
        pendingStateLog = null
        reconnectGotInitialState = false
        reconnectGotState = false
        awaitingReconnectMessages = false
        currentState = GameState.INACTIVE
        lastError = null

        clearCallbacks()
    }

    /**
     * Resetea únicamente la conexión (manteniendo datos de partida).
     */
    private fun resetConnectionOnly() {
        friendlyGameWebSocket?.close()
        friendlyGameWebSocket = null
    }

    /**
     * Construye el listener del WebSocket.
     *
     * @param onOpenState Estado al conectarse
     */
    private fun buildListener(onOpenState: GameState): FriendlyGameWebSocketListener {
        return FriendlyGameWebSocketListener(
            onConnected = {
                currentState = onOpenState

                isReconnecting = false

                onConnectedCallback?.invoke()
            },
            onMessageReceived = { message ->
                handleServerMessage(message)
            },
            onError = { error ->
                currentState = GameState.ERROR
                lastError = error
                onErrorCallback?.invoke(error)
            },
            onClosed = {
                // Si el cierre ocurre durante una reconexión
                if (isReconnecting) {
                    currentState = GameState.INACTIVE
                    isReconnecting = false
                } else {
                    currentState = GameState.CLOSED
                }
                onClosedCallback?.invoke()
            }
        )
    }
}
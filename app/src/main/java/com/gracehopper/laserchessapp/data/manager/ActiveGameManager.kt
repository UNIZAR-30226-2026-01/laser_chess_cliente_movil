package com.gracehopper.laserchessapp.data.manager

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.gracehopper.laserchessapp.data.model.game.GameEvent
import com.gracehopper.laserchessapp.data.model.game.GameMessageType
import com.gracehopper.laserchessapp.data.model.game.GamePlayerInfo
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

    var isMatchmakingGame: Boolean = false
        private set

    var isFriendlyGame: Boolean = false
        private set

    var currentOpponentInfo: GamePlayerInfo? = null
        private set

    var currentBoard: Int? = null
        private set

    var currentStartingTime: Int? = null
        private set

    var currentTimeIncrement: Int? = null
        private set

    var currentMatchId: Long? = null
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

    // Buffer de eventos que llegaron antes de que el Fragment registrara su callback
    private val pendingEvents = mutableListOf<GameEvent>()

    private const val PREF_NAME = "active_game_prefs"
    private const val KEY_IS_FRIENDLY = "is_friendly_game"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        isFriendlyGame = prefs.getBoolean(KEY_IS_FRIENDLY, false)  // restaurar al arrancar
    }

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

        // Despachar eventos que llegaron antes de tener listener
        if (onMessageReceived != null && pendingEvents.isNotEmpty()) {
            pendingEvents.forEach { onMessageReceived.invoke(it) }
            pendingEvents.clear()
        }

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

        if (serverMsg.type == null) {
            Log.w("WS", "Mensaje con type null: $message")
            return
        }

        when (serverMsg.type) {

            /**
             * Estado inicial de la partida:
             * - Se guarda el tablero inicial
             * - Se determina si el jugador es rojo
             */
            GameMessageType.INITIAL_STATE -> {
                intialBoardCSV = serverMsg.content

                val redPlayerId = serverMsg.extra?.toLongOrNull()
                if (redPlayerId != null && redPlayerId != -1L) {
                    val myId = TokenManager.getUserId()
                    imRedPlayer = (redPlayerId == myId)
                } else {
                    // Por defecto en bot/matchmaking si no hay ID, el primer jugador suele ser el humano
                    // o mantenemos el valor por defecto (true) seteado en resetAll
                }

                Log.d("PLAYER", "Soy rojo: $imRedPlayer (ID Red: $redPlayerId)")

                if (awaitingReconnectMessages) {
                    // Reconexión: guardar y esperar a tener ambos mensajes
                    reconnectGotInitialState = true
                    dispatchReconnectIfReady()
                } else if (reconnectGotState) {
                    // Retomar partida pausada: activar la espera del State
                    awaitingReconnectMessages = true
                    reconnectGotInitialState = true
                    dispatchReconnectIfReady()
                } else {
                    // Partida en curso normal
                    val event = GameEvent.InitialState(
                        boardCsv = intialBoardCSV,
                        redPlayerId = redPlayerId
                    )
                    val cb = onMessageReceivedCallback
                    if (cb != null) cb.invoke(event) else pendingEvents.add(event)
                }
            }

            /**
             * Movimiento de partida
             */
            GameMessageType.MOVE -> {
                onMessageReceivedCallback?.invoke(
                    GameEvent.Move(
                        moveAndTime = serverMsg.content.orEmpty()
                    )
                )
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
                } else if (currentState == GameState.IN_GAME) {
                    val cb = onMessageReceivedCallback
                    if (cb != null) {
                        cb.invoke(GameEvent.State(log = log))
                    } else {
                        pendingEvents.add(GameEvent.State(log = log))
                    }
                } else {
                    // State llegó antes del InitialState
                    pendingStateLog = log
                    reconnectGotState = true
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
                onMessageReceivedCallback?.invoke(
                    GameEvent.End(
                        winner = serverMsg.content.orEmpty(),
                        victoryCause = serverMsg.extra.orEmpty()
                    )
                )
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
             * End Of Connection: cerrar conexión
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

                val timers = serverMsg.extra?.split("%")

                if (timers?.size == 2) {

                    val myTimeMs = timers[0].toLongOrNull()
                    val opponentTimeMs = timers[1].toLongOrNull()

                    if (myTimeMs != null && opponentTimeMs != null) {

                        currentStartingTime = (myTimeMs / 1000).toInt()

                        reconnectingOpponentId = serverMsg.content?.toLongOrNull()

                        // Sincronizar timers inmediatamente
                        GameTimerManager.syncTimers(
                            myTime = myTimeMs,
                            opponentTime = opponentTimeMs
                        )

                        awaitingReconnectMessages = true

                        onMessageReceivedCallback?.invoke(
                            GameEvent.Reconnected(
                                opponentId = serverMsg.content,
                                remainingTime = serverMsg.extra
                            )
                        )

                    } else {

                        onMessageReceivedCallback?.invoke(
                            GameEvent.OpponentReconnected
                        )
                    }

                } else {

                    onMessageReceivedCallback?.invoke(
                        GameEvent.OpponentReconnected
                    )
                }
            }

            GameMessageType.MATCH_START -> {
                val opponentId = serverMsg.content?.toLongOrNull()
                val event = GameEvent.MatchStart(opponentId = opponentId)
                val cb = onMessageReceivedCallback
                if (cb != null) cb.invoke(event) else pendingEvents.add(event)
            }

            GameMessageType.REWARDS -> {

                val xpDiff = serverMsg.content?.toIntOrNull()
                val moneyDiff = serverMsg.extra?.toIntOrNull()

                if (xpDiff != null && moneyDiff != null) {

                    val event = GameEvent.Rewards(
                        xpDiff = xpDiff,
                        moneyDiff = moneyDiff
                    )

                    val cb = onMessageReceivedCallback

                    if (cb != null) {
                        cb.invoke(event)
                    } else {
                        pendingEvents.add(event)
                    }

                } else {

                    Log.w(
                        "WS",
                        "Mensaje REWARDS inválido: content=${serverMsg.content}, extra=${serverMsg.extra}"
                    )
                }
            }

            GameMessageType.ELO_UPDATE -> {

                val eloDiff = serverMsg.content?.toIntOrNull()

                if (eloDiff != null) {

                    val event = GameEvent.EloUpdate(
                        eloDiff = eloDiff
                    )

                    val cb = onMessageReceivedCallback

                    if (cb != null) {
                        cb.invoke(event)
                    } else {
                        pendingEvents.add(event)
                    }

                } else {

                    Log.w(
                        "WS",
                        "Mensaje ELO_UPDATE inválido: content=${serverMsg.content}"
                    )
                }
            }

            else -> {
                // ignorar
            }

        }
    }

    /**
     * Establece la información del rival.
     */
    fun setOpponentInfo(info: GamePlayerInfo?) {
        currentOpponentInfo = info
    }

    /**
     * Elimina la información del rival.
     */
    fun clearOpponentInfo() {
        currentOpponentInfo = null
    }

    fun getOpponentUsername() : String? {
        return currentOpponentInfo?.username
    }

    fun getOpponentPieceSkin(): Int {
        return currentOpponentInfo?.pieceSkin ?: 1
    }

    fun getOpponentBoardSkin(): Int {
        return currentOpponentInfo?.boardSkin ?: 4
    }

    /**
     * Establece el tipo de partida.
     */
    fun setGameType(isFriendly: Boolean) {
        isFriendlyGame = isFriendly
        prefs.edit { putBoolean(KEY_IS_FRIENDLY, isFriendly) }
    }

    /**
     *
     */
    fun createBotGame(
        board: Int,
        startingTime: Int,
        timeIncrement: Int,
        level: Int
    ) {

        //resetConnectionOnly()
        prepareForNewGame()

        setGameType(false)

        currentOpponentInfo = null
        currentBoard = board
        currentStartingTime = startingTime
        currentTimeIncrement = timeIncrement

        currentState = GameState.CONNECTING
        lastError = null

        val listener = buildListener(
            onOpenState = GameState.STARTING_GAME
        )

        friendlyGameWebSocket = FriendlyGameWebSocket(listener)

        friendlyGameWebSocket?.startBotGame(
            board,
            startingTime,
            timeIncrement,
            level
        )
    }

    /**
     * Crea un reto contra otro jugador.
     */
    fun createChallenge(
        opponentInfo: GamePlayerInfo,
        board: Int,
        startingTime: Int,
        timeIncrement: Int,
        matchId: Long? = null
    ) {

        //resetConnectionOnly()
        prepareForNewGame()

        setGameType(true)                   // La partida es amistosa
        currentOpponentInfo = opponentInfo

        currentBoard = board
        currentStartingTime = startingTime
        currentTimeIncrement = timeIncrement
        currentMatchId = matchId
        currentState = GameState.CONNECTING
        lastError = null

        reconnectGotInitialState = false
        reconnectGotState = false
        pendingStateLog = null

        val listener = buildListener(
            onOpenState = GameState.WAITING_ACCEPTANCE
        )

        friendlyGameWebSocket = FriendlyGameWebSocket(listener)
        friendlyGameWebSocket?.createChallenge(
            opponentInfo.username,
            board,
            startingTime,
            timeIncrement,
            matchId
        )

    }

    /**
     * Entra en la cola de matchmaking pública o ranked.
     *
     * @param board Tablero de juego
     * @param timeBase Tiempo base en segundos
     * @param timeIncrement Incremento de tiempo en segundos
     * @param ranked true si es ranked, false si es casual
     */
    fun joinMatchmaking(
        board: Int,
        timeBase: Int,
        timeIncrement: Int,
        ranked: Boolean
    ) {
        //resetConnectionOnly()
        prepareForNewGame()

        setGameType(false)
        isMatchmakingGame = true

        currentBoard = board
        currentStartingTime = timeBase
        currentTimeIncrement = timeIncrement
        currentOpponentInfo = null
        currentState = GameState.CONNECTING
        lastError = null

        val backendRanked = if (ranked) 0 else 1

        val listener = buildListener(onOpenState = GameState.STARTING_GAME)
        friendlyGameWebSocket = FriendlyGameWebSocket(listener)
        friendlyGameWebSocket?.joinMatchmaking(board, timeBase, timeIncrement, backendRanked)
    }

    /**
     * Acepta un reto recibido.
     */
    fun acceptChallenge(
        opponentInfo: GamePlayerInfo,
        board: Int,
        startingTime: Int,
        timeIncrement: Int
    ) {

        //resetConnectionOnly()
        prepareForNewGame()

        setGameType(true)                   // La partida es amistosa
        currentOpponentInfo = opponentInfo

        currentBoard = board
        currentStartingTime = startingTime / 1000
        currentTimeIncrement = timeIncrement
        currentState = GameState.CONNECTING
        lastError = null

        reconnectGotInitialState = false
        reconnectGotState = false
        pendingStateLog = null

        val listener = buildListener(
            onOpenState = GameState.STARTING_GAME
        )

        friendlyGameWebSocket = FriendlyGameWebSocket(listener)
        friendlyGameWebSocket?.acceptChallenge(opponentInfo.username)

    }

    /**
     * Rechaza un reto recibido.
     */
    fun rejectChallenge(challengerUsername: String) {

        resetConnectionOnly()

        currentOpponentInfo = null
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
        Log.d(
            "RECONNECT",
            "dispatchReconnectIfReady: gotInitial=$reconnectGotInitialState gotState=$reconnectGotState pendingLog='$pendingStateLog' csv=${intialBoardCSV != null}"
        )
        if (reconnectGotInitialState && reconnectGotState) {
            Log.d("RECONNECT", "Ambos recibidos → navegando a GameActivity")
            awaitingReconnectMessages = false
            currentState = GameState.IN_GAME
            onMessageReceivedCallback?.invoke(
                GameEvent.InitialState(
                    boardCsv = intialBoardCSV,
                    redPlayerId = if (imRedPlayer) TokenManager.getUserId() else null
                )
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

        val listener = buildListener(
            onOpenState = GameState.CONNECTING
        )

        friendlyGameWebSocket = FriendlyGameWebSocket(listener)
        friendlyGameWebSocket?.reconnect()
    }

    /**
     * Resetea completamente el estado del manager.
     */
    fun resetAll() {
        friendlyGameWebSocket?.close()
        friendlyGameWebSocket = null

        currentOpponentInfo = null
        currentBoard = null
        currentStartingTime = null
        currentTimeIncrement = null
        currentMatchId = null
        reconnectingOpponentId = null
        pendingStateLog = null
        reconnectGotInitialState = false
        reconnectGotState = false
        awaitingReconnectMessages = false
        isMatchmakingGame = false
        currentState = GameState.INACTIVE
        lastError = null
        pendingEvents.clear()
        
        intialBoardCSV = null
        imRedPlayer = true

        setGameType(false)

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
     * Limpia los datos de partidas previas pero MANTIENE los callbacks
     * actuales para no romper la comunicación con la UI que lanza la partida.
     */
    private fun prepareForNewGame() {
        // Cerramos cualquier conexión persistente anterior
        friendlyGameWebSocket?.close()
        friendlyGameWebSocket = null

        // Seteamos el estado inicial de juego
        imRedPlayer = true
        intialBoardCSV = null

        // Limpiamos datos de oponente y partida
        currentOpponentInfo = null
        currentMatchId = null
        reconnectingOpponentId = null
        pendingStateLog = null

        // Reseteamos flags de control
        reconnectGotInitialState = false
        reconnectGotState = false
        awaitingReconnectMessages = false
        isMatchmakingGame = false

        // Limpiamos errores y eventos pendientes
        lastError = null
        pendingEvents.clear()

        currentState = GameState.INACTIVE
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
                if (currentState != GameState.IN_GAME) {
                    onClosedCallback?.invoke()
                }
            }
        )
    }
}
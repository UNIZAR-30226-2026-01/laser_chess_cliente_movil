package com.gracehopper.laserchessapp.data.manager

import com.google.gson.Gson
import com.gracehopper.laserchessapp.data.remote.websocket.PrivateGameWebSocket
import com.gracehopper.laserchessapp.data.remote.websocket.PrivateGameWebSocketListener
import com.gracehopper.laserchessapp.data.remote.websocket.ServerSocketMessage
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

    private var privateGameWebSocket: PrivateGameWebSocket? = null

    var currentOpponentUsername: String? = null
        private set

    var currentBoard: Int? = null
        private set

    var currentStartingTime: Int? = null
        private set

    var currentTimeIncrement: Int? = null
        private set

    var intialBoardCSV: String? = null
        private set

    var imRedPlayer: Boolean = true
        private set

    var currentState: GameState = GameState.INACTIVE
        private set

    var lastError: String? = null
        private set

    private var onConnectedCallback: (() -> Unit)? = null
    private var onMessageReceivedCallback: ((String, String?) -> Unit)? = null
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
        onMessageReceived: ((String, String?) -> Unit)? = null,
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
        val serverMsg = gson.fromJson(message, ServerSocketMessage::class.java)

        when (serverMsg.Type) {

            /**
             * Estado inicial de la partida:
             * - Se guarda el tablero inicial
             * - Se determina si el jugador es rojo
             */
            "InitialState" -> {
                intialBoardCSV = serverMsg.Content

                val redPlayerId = serverMsg.Extra?.toLong()
                val myId = TokenManager.getUserId()

                imRedPlayer = (redPlayerId == myId)

                onMessageReceivedCallback?.invoke("INITIAL_STATE", null)
            }

            /**
             * Movimiento de partida
             */
            "Move" -> {
                onMessageReceivedCallback?.invoke(serverMsg.Content, serverMsg.Extra)
            }

            /**
             * Fin de partida
             */
            "End" -> {
                onMessageReceivedCallback?.invoke(serverMsg.Content, serverMsg.Extra)
            }

            /**
             * End Of Connection → cerrar conexión
             */
            "EOC" -> {
                closeConnection()
            }
        }
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

        currentOpponentUsername = challengedUsername
        currentBoard = board
        currentStartingTime = startingTime
        currentTimeIncrement = timeIncrement
        currentState = GameState.CONNECTING
        lastError = null

        val listener = buildListener(
            onOpenState = GameState.WAITING_ACCEPTANCE
        )

        privateGameWebSocket = PrivateGameWebSocket(listener)
        privateGameWebSocket?.createChallenge(
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

        currentOpponentUsername = challengerUsername
        currentBoard = board
        currentStartingTime = startingTime/1000
        currentTimeIncrement = timeIncrement
        currentState = GameState.CONNECTING
        lastError = null

        val listener = buildListener(
            onOpenState = GameState.STARTING_GAME
        )

        privateGameWebSocket = PrivateGameWebSocket(listener)
        privateGameWebSocket?.acceptChallenge(challengerUsername)

    }

    /**
     * Envía un mensaje de juego al servidor.
     */
    fun sendGameMessage(message: String) {
        privateGameWebSocket?.sendMessage(message)
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
        privateGameWebSocket?.close()
        privateGameWebSocket = null
        currentState = GameState.CLOSED
    }

    /**
     * Resetea completamente el estado del manager.
     */
    fun resetAll() {
        privateGameWebSocket?.close()
        privateGameWebSocket = null

        currentOpponentUsername = null
        currentBoard = null
        currentStartingTime = null
        currentTimeIncrement = null
        currentState = GameState.INACTIVE
        lastError = null

        clearCallbacks()
    }

    /**
     * Resetea únicamente la conexión (manteniendo datos de partida).
     */
    private fun resetConnectionOnly() {
        privateGameWebSocket?.close()
        privateGameWebSocket = null
    }

    /**
     * Construye el listener del WebSocket.
     *
     * @param onOpenState Estado al conectarse
     */
    private fun buildListener(onOpenState: GameState): PrivateGameWebSocketListener {
        return PrivateGameWebSocketListener(
            onConnected = {
                currentState = onOpenState
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
                currentState = GameState.CLOSED
                onClosedCallback?.invoke()
            }
        )
    }
}
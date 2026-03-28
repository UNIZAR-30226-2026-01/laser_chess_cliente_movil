package com.gracehopper.laserchessapp.data.manager

import com.gracehopper.laserchessapp.data.remote.websocket.PrivateMatchWebSocket
import com.gracehopper.laserchessapp.data.remote.websocket.PrivateMatchWebSocketListener

object ActiveMatchManager {

    enum class MatchState {
        INACTIVE,
        CONNECTING,
        WAITING_ACCEPTANCE,
        STARTING_GAME,
        IN_GAME,
        CLOSED,
        ERROR
    }

    private var privateMatchWebSocket: PrivateMatchWebSocket? = null

    var currentOpponentUsername: String? = null
        private set

    var currentBoard: Int? = null
        private set

    var currentStartingTime: Int? = null
        private set

    var currentTimeIncrement: Int? = null
        private set

    var currentState: MatchState = MatchState.INACTIVE
        private set

    var lastError: String? = null
        private set

    private var onConnectedCallback: (() -> Unit)? = null
    private var onMessageReceivedCallback: ((String) -> Unit)? = null
    private var onErrorCallback: ((String) -> Unit)? = null
    private var onClosedCallback: (() -> Unit)? = null

    fun setCallbacks(onConnected: (() -> Unit)? = null,
                    onMessageReceived: ((String) -> Unit)? = null,
                    onError: ((String) -> Unit)? = null,
                    onClosed: (() -> Unit)? = null) {

        onConnectedCallback = onConnected
        onMessageReceivedCallback = onMessageReceived
        onErrorCallback = onError
        onClosedCallback = onClosed

    }

    fun clearCallbacks() {
        onConnectedCallback = null
        onMessageReceivedCallback = null
        onErrorCallback = null
        onClosedCallback = null
    }

    fun createChallenge(challengedUsername: String,
                        board: Int,
                        startingTime: Int,
                        timeIncrement: Int) {

        resetConnectionOnly()

        currentOpponentUsername = challengedUsername
        currentBoard = board
        currentStartingTime = startingTime
        currentTimeIncrement = timeIncrement
        currentState = MatchState.CONNECTING
        lastError = null

        val listener = buildListener(
            onOpenState = MatchState.WAITING_ACCEPTANCE
        )

        privateMatchWebSocket = PrivateMatchWebSocket(listener)
        privateMatchWebSocket?.createChallenge(challengedUsername,
            board, startingTime, timeIncrement)

    }

    fun acceptChallenge(challengerUsername: String,
                        board: Int,
                        startingTime: Int,
                        timeIncrement: Int) {

        resetConnectionOnly()

        currentOpponentUsername = challengerUsername
        currentBoard = board
        currentStartingTime = startingTime
        currentTimeIncrement = timeIncrement
        currentState = MatchState.CONNECTING
        lastError = null

        val listener = buildListener(
            onOpenState = MatchState.STARTING_GAME
        )

        privateMatchWebSocket = PrivateMatchWebSocket(listener)
        privateMatchWebSocket?.acceptChallenge(challengerUsername)

    }

    fun sendGameMessage(message: String) {
        privateMatchWebSocket?.sendMessage(message)
    }

    fun markInGame() {
        currentState = MatchState.IN_GAME
    }

    fun closeConnection() {
        privateMatchWebSocket?.close()
        privateMatchWebSocket = null
        currentState = MatchState.CLOSED
    }

    fun resetAll() {
        privateMatchWebSocket?.close()
        privateMatchWebSocket = null

        currentOpponentUsername = null
        currentBoard = null
        currentStartingTime = null
        currentTimeIncrement = null
        currentState = MatchState.INACTIVE
        lastError = null

        clearCallbacks()
    }

    private fun resetConnectionOnly() {
        privateMatchWebSocket?.close()
        privateMatchWebSocket = null
    }

    private fun buildListener(onOpenState: MatchState) : PrivateMatchWebSocketListener {
        return PrivateMatchWebSocketListener(
            onConnected = {
                currentState = onOpenState
                onConnectedCallback?.invoke()
            },
            onMessageReceived = { message ->
                onMessageReceivedCallback?.invoke(message)
            },
            onError = { error ->
                currentState = MatchState.ERROR
                lastError = error
                onErrorCallback?.invoke(error)
            },
            onClosed = {
                currentState = MatchState.CLOSED
                onClosedCallback?.invoke()
            }
        )
    }
}
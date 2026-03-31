package com.gracehopper.laserchessapp.data.manager

import android.util.Log
import com.google.gson.Gson
import com.gracehopper.laserchessapp.data.remote.websocket.PrivateGameWebSocket
import com.gracehopper.laserchessapp.data.remote.websocket.PrivateGameWebSocketListener
import com.gracehopper.laserchessapp.data.remote.websocket.ServerSocketMessage
import com.gracehopper.laserchessapp.utils.TokenManager

object ActiveGameManager {

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

    fun setCallbacks(onConnected: (() -> Unit)? = null,
                    onMessageReceived: ((String, String?) -> Unit)? = null,
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

    fun handleServerMessage(message: String) {
        val gson = Gson()
        val serverMsg = gson.fromJson(message, ServerSocketMessage::class.java)

        when (serverMsg.Type) {
            "InitialState" -> {
                intialBoardCSV = serverMsg.Content

                val redPlayerId = serverMsg.Extra?.toLong()
                val myId = TokenManager.getUserId()

                imRedPlayer = (redPlayerId == myId)

                onMessageReceivedCallback?.invoke("INITIAL_STATE", null)
            }
            "Move" -> {
                onMessageReceivedCallback?.invoke(serverMsg.Content, serverMsg.Extra)
            }
        }
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
        currentState = GameState.CONNECTING
        lastError = null

        val listener = buildListener(
            onOpenState = GameState.WAITING_ACCEPTANCE
        )

        privateGameWebSocket = PrivateGameWebSocket(listener)
        privateGameWebSocket?.createChallenge(challengedUsername,
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
        currentState = GameState.CONNECTING
        lastError = null

        val listener = buildListener(
            onOpenState = GameState.STARTING_GAME
        )

        privateGameWebSocket = PrivateGameWebSocket(listener)
        privateGameWebSocket?.acceptChallenge(challengerUsername)

    }

    fun sendGameMessage(message: String) {
        privateGameWebSocket?.sendMessage(message)
    }

    fun markInGame() {
        currentState = GameState.IN_GAME
    }

    fun closeConnection() {
        privateGameWebSocket?.close()
        privateGameWebSocket = null
        currentState = GameState.CLOSED
    }

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

    private fun resetConnectionOnly() {
        privateGameWebSocket?.close()
        privateGameWebSocket = null
    }

    private fun buildListener(onOpenState: GameState) : PrivateGameWebSocketListener {
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
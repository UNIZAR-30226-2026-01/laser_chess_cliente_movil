package com.gracehopper.laserchessapp.data.remote.websocket

import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * Clase de bajo nivel que:
 * - abre socket
 * - envía mensajes
 * - lo cierra
 */
class FriendlyGameWebSocket(private val listener: WebSocketListener) {

    private var webSocket: WebSocket? = null
    private val CHALLENGE_URL = NetworkUtils.WS_BASE_URL + "challenge"
    private val BOT_URL = NetworkUtils.WS_BASE_URL + "bot"
    private val RECONNECT_URL = NetworkUtils.WS_BASE_URL + "reconnect"
    private val MATCHMAKING_URL = NetworkUtils.WS_BASE_URL + "matchmaking"


    /**
     * Crea una nueva partida contra un bot.
     */
    fun startBotGame(
        board: Int,
        startingTime: Int,
        timeIncrement: Int,
        level: Int
    ) {

        val url =
            BOT_URL +
                    "?board=$board" +
                    "&starting_time=$startingTime" +
                    "&time_increment=$timeIncrement" +
                    "&level=$level"

        val request = Request.Builder()
            .url(url)
            .build()

        val client = NetworkUtils.getWebSocketClient()
        webSocket = client.newWebSocket(request, listener)
    }

    /**
     * Entra en la cola de matchmaking (Ranked o no)
     * @param board Tablero de juego
     * @param timeBase Tiempo inicial del juego
     * @param timeIncrement Incremento de tiempo
     * @param ranked 1 si es Ranked, 0 si no
     */
    fun joinMatchmaking(
        board: Int,
        timeBase: Int,
        timeIncrement: Int,
        ranked: Int
    ) {
        val url = MATCHMAKING_URL +
                "?board=$board" +
                "&time_base=$timeBase" +
                "&time_increment=$timeIncrement" +
                "&ranked=$ranked"
        val request = Request.Builder().url(url).build()
        webSocket = NetworkUtils.getWebSocketClient().newWebSocket(request, listener)
    }


    /**
     * Crea una nueva solicitud de reto a un usuario específico.
     *
     * @param username Nombre de usuario al que se enviará la solicitud
     * @param board Tablero de juego
     * @param startingTime Tiempo inicial del juego
     * @param timeIncrement Incremento de tiempo
     */
    fun createChallenge(
        username: String, board: Int,
        startingTime: Int, timeIncrement: Int
    ) {

        val url =
            "$CHALLENGE_URL?username=$username&board=$board&starting_time=$startingTime&time_increment=$timeIncrement"

        val request = Request.Builder()
            .url(url)
            .build()

        val client = NetworkUtils.getWebSocketClient()
        webSocket = client.newWebSocket(request, listener)

    }

    private fun openChallengeReplySocket(action: String, username: String) {
        val url = "$CHALLENGE_URL/$action?username=$username"

        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = NetworkUtils.getWebSocketClient().newWebSocket(request, listener)
    }

    fun acceptChallenge(username: String) {
        openChallengeReplySocket("accept", username)
    }

    fun rejectChallenge(username: String) {
        openChallengeReplySocket("reject", username)
    }

    /**
     * Envía un mensaje a través del WebSocket.
     *
     * @param message Mensaje a enviar
     */
    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun reconnect() {


        val request = Request.Builder()
            .url(RECONNECT_URL)
            .build()

        val client = NetworkUtils.getWebSocketClient()
        webSocket = client.newWebSocket(request, listener)
    }

    /**
     * Cierra la conexión WebSocket.
     */
    fun close() {
        webSocket?.close(1000, "closed")
    }

}
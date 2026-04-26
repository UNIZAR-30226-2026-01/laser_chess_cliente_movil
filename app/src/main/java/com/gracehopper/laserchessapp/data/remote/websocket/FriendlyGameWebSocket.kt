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

    private val BASE_URL = "ws://10.0.2.2:8080/api/rt/"
        // TODO EMULADOR:           "ws://10.0.2.2:8080/api/rt/"
        // TODO PORTÁTIL AINHOA:    "ws://192.168.1.26:8080/api/rt/"
        // TODO PORTÁTIL JORGE:     "ws://192.168.0.17:8080/api/rt/"
    private val CHALLENGE_URL = BASE_URL + "challenge"
    private val BOT_URL = BASE_URL + "bot"


    /**
     *
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

        webSocket = NetworkUtils.getOkHttpClient().newWebSocket(request, listener)
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

    fun reconnect(token: String) {

        val url = "ws://192.168.0.17:8080/api/rt/reconnect?token=$token"

        val request = Request.Builder()
            .url(url)
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
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
class PrivateGameWebSocket(private val listener: WebSocketListener) {

    private var webSocket: WebSocket? = null
    private val BASE_URL = "ws://172.20.10.12:8080/api/rt/challenge"

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
            "$BASE_URL?username=$username&board=$board&starting_time=$startingTime&time_increment=$timeIncrement"

        val request = Request.Builder()
            .url(url)
            .build()

        val client = NetworkUtils.getWebSocketClient()
        webSocket = client.newWebSocket(request, listener)

    }

    /**
     * Acepta una solicitud de reto de un usuario específico.
     *
     * @param username Nombre de usuario al que se aceptará la solicitud
     */
    fun acceptChallenge(username: String) {

        // Construir la URL con el nombre de usuario
        val url = "$BASE_URL/accept?username=$username"

        val request = Request.Builder()
            .url(url)
            .build()

        val client = NetworkUtils.getOkHttpClient()
        webSocket = client.newWebSocket(request, listener)

    }

    /**
     * Envía un mensaje a través del WebSocket.
     *
     * @param message Mensaje a enviar
     */
    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    /**
     * Cierra la conexión WebSocket.
     */
    fun close() {
        webSocket?.close(1000, "closed")
    }

}
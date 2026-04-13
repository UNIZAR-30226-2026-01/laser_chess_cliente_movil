package com.gracehopper.laserchessapp.data.remote.websocket

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * Listener personalizado para el WebSocket de la aplicación.
 *
 * @param onConnected Callback a ejecutar cuando se establezca la conexión
 * @param onMessageReceived Callback a ejecutar cuando se reciba un mensaje
 * @param onError Callback a ejecutar en caso de error
 * @param onClosed Callback a ejecutar cuando se cierre la conexión
 */
class PrivateGameWebSocketListener(
    private val onConnected: () -> Unit,
    private val onMessageReceived: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onClosed: () -> Unit
) : WebSocketListener() {

    /**
     * Callback que se ejecuta cuando se establezca la conexión.
     *
     * @param webSocket WebSocket que se ha conectado
     * @param response Respuesta del servidor
     */
    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WS", "Conectado")
        onConnected()
    }

    /**
     * Callback que se ejecuta cuando se reciba un mensaje.
     *
     * @param webSocket WebSocket que ha recibido el mensaje
     * @param text Mensaje recibido
     */
    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WS", "Mensaje: $text")
        onMessageReceived(text)
    }

    /**
     * Callback que se ejecuta en caso de error.
     *
     * @param webSocket WebSocket que ha generado el error
     * @param t Excepción que ha generado el error
     * @param response Respuesta del servidor
     */
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WS", "Error: ${t.message}")
        onError(t.message ?: "Error desconocido")
    }

    /**
     * Callback que se ejecuta cuando se cierra la conexión.
     *
     * @param webSocket WebSocket que ha cerrado la conexión
     * @param code Código de cierre
     * @param reason Razón del cierre
     */
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WS", "Cerrado: $reason")
        onClosed()
    }

}
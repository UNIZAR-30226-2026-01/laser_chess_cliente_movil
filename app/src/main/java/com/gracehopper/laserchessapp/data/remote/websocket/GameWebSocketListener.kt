package com.gracehopper.laserchessapp.data.remote.websocket

import android.util.Log
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class GameWebSocketListener (
    private val onMessageReceived: (ServerSocketMessage) -> Unit
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WebSocket", "Conectado al servidor")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocket", "Mensaje recibido: $text")

        val message = Gson().fromJson(text, ServerSocketMessage::class.java)
        onMessageReceived(message)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocket", "Cerrando conexión")
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocket", "Error en la conexión: ${t.message}")
    }
}
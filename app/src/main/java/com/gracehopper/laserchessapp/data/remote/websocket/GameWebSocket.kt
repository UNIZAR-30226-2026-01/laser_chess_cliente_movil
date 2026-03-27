package com.gracehopper.laserchessapp.data.remote.websocket

import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import okhttp3.Request
import okhttp3.WebSocket

class GameWebSocket(private val listener: GameWebSocketListener) {

    private var webSocket: WebSocket? = null

    fun connect(url: String) {
        val client = NetworkUtils.getApiService()
        val okHttpClient = NetworkUtils.getOkHttpClient()

        val request = Request.Builder().url(url).build()
        webSocket = okHttpClient.newWebSocket(request, listener)
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun close() {
        webSocket?.close(1000, null)
    }
}
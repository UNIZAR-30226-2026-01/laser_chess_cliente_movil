package com.gracehopper.laserchessapp.data.manager

import android.util.Log
import androidx.compose.runtime.clearCompositionErrors
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

class SseManager(
    private val onChallengeReceived: ((String) -> Unit)? = null,
    private val onFriendRequestReceived: ((String) -> Unit)? = null,
    private val onError: ((Throwable?) -> Unit)? = null
) {

    private var eventSource: EventSource? = null

    fun connect() {

        if (eventSource != null) return

        val request = Request.Builder()
            .url(NetworkUtils.BASE_URL + "api/events")
            .build()

        val client = NetworkUtils.getOkHttpClient()
        val factory = EventSources.createFactory(client)

        eventSource = factory.newEventSource(request, object : EventSourceListener() {

            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.d("SSE", "Conectado")
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                Log.d("SSE", "Evento recibido: $type - $data")

                handleEvent(type, data)
            }

            override fun onClosed(eventSource: EventSource) {
                Log.d("SSE", "Conexión cerrada")
                this@SseManager.eventSource = null
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?
            ) {
                Log.e("SSE", "Error en SSE", t)
                this@SseManager.eventSource = null
                onError?.invoke(t)
            }

        })

    }

    fun disconnect() {
        eventSource?.cancel()
        eventSource = null
    }

    private fun handleEvent(type: String?, data: String) {

        when (type) {

            "Init" -> {
                Log.d("SSE", "SSE inicializado")
            }

            "Challenge" -> {
                onChallengeReceived?.invoke(data)
            }

            "FriendRequest" -> {
                onFriendRequestReceived?.invoke(data)
            }

        }

    }

}

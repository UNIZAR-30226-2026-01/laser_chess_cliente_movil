package com.gracehopper.laserchessapp.data.manager

import android.util.Log
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

class SseManager(
    private val onChallengeReceived: ((String) -> Unit)? = null,
    private val onFriendRequestReceived: ((String) -> Unit)? = null,
    private val onNewFriendshipReceived: ((String) -> Unit)? = null,
    private val onError: ((Throwable?) -> Unit)? = null
) {

    private var eventSource: EventSource? = null
    private var manuallyClosed = false

    fun connect() {

        Log.d("SSE", "connect() llamado. eventSourceNull=${eventSource == null}")

        if (eventSource != null) return

        val url = NetworkUtils.BASE_URL + "api/events"

        val request = Request.Builder()
            .url(url)
            .build()

        val client = NetworkUtils.getSseClient()
        val factory = EventSources.createFactory(client)

        eventSource = factory.newEventSource(request, object : EventSourceListener() {

            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.d("SSE", "Conectado HTTP=${response.code}")
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
                this@SseManager.eventSource = null

                if (manuallyClosed) {
                    Log.d("SSE", "SSE cerrado manualmente")
                    manuallyClosed = false
                    return
                }

                Log.e("SSE", "onFailure manuallyClosed=$manuallyClosed HTTP=${response?.code}", t)
                onError?.invoke(t)
            }

        })

    }

    fun reconnect() {
        disconnect()
        connect()
    }

    fun disconnect() {
        manuallyClosed = true
        eventSource?.cancel()
        eventSource = null
    }

    private fun handleEvent(type: String?, data: String) {

        Log.d("SSE", "RAW eventType=$type data=$data")
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

            "NewFriend" -> {
                onNewFriendshipReceived?.invoke(data)
            }

        }

    }

}

package com.gracehopper.laserchessapp.data.remote.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gracehopper.laserchessapp.utils.AppNotificationHelper

/**
 * Servicio de Firebase para recibir y manejar notificaciones push.
 */
class AppFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Se llama cuando se recibe un nuevo token de registro para este dispositivo.
     *
     * @param token Nuevo token de registro.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token FCM: $token")

        // TODO:
        // 1) guardarlo localmente ?
        // 2) enviarlo al backend (endpoint)
    }

    /**
     * Se llama cuando se recibe un mensaje push.
     *
     * @param message Mensaje push recibido.
     */
    override fun onMessageReceived(message: RemoteMessage) {

        super.onMessageReceived(message)

        Log.d("FCM", "Mensaje recibido: ${message.data}")

        when (message.data["type"]) {
            "challenge" -> {
                val challengerUsername = message.data["challengerUsername"]
                if (!challengerUsername.isNullOrBlank()) {
                    AppNotificationHelper.showChallengeNotification(
                        applicationContext,
                        challengerUsername
                    )
                }
            }

            "friend_request" -> {
                val requesterUsername = message.data["requesterUsername"]
                if (!requesterUsername.isNullOrBlank()) {
                    AppNotificationHelper.showFriendRequestNotification(
                        applicationContext,
                        requesterUsername
                    )
                }
            }
        }

    }

}
package com.gracehopper.laserchessapp.data.remote.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.DeviceRepository
import com.gracehopper.laserchessapp.utils.AppNotificationHelper
import com.gracehopper.laserchessapp.data.manager.TokenManager

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

        if (TokenManager.isLoggedIn()) {
            DeviceRepository(NetworkUtils.getApiService())
                .registerDevice(token)
        }
    }

    /**
     * Se llama cuando se recibe un mensaje push.
     *
     * @param message Mensaje push recibido.
     */
    override fun onMessageReceived(message: RemoteMessage) {

        super.onMessageReceived(message)

        val eventType = message.data["event_type"]
        val data = message.data["data"]

        Log.d("FCM", "Mensaje recibido eventType=$eventType data=$data")
        Log.d("FCM", "Data completa=${message.data}")

        when (eventType) {

            "Challenge" -> {
                if (!data.isNullOrBlank()) {
                    AppNotificationHelper.showChallengeNotification(
                        applicationContext,
                        data
                    )
                }
            }

            "FriendRequest" -> {
                if (!data.isNullOrBlank()) {
                    AppNotificationHelper.showFriendRequestNotification(
                        applicationContext,
                        data
                    )
                }
            }

            "NewFriend" -> {
                if (!data.isNullOrBlank()) {
                    AppNotificationHelper.showNewFriendshipNotification(
                        applicationContext,
                        data
                    )
                }
            }

            else -> {
                Log.w("FCM", "Tipo de evento no reconocido: $eventType")
            }
        }

    }

}
package com.gracehopper.laserchessapp.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.ui.main.MainActivity

object AppNotificationHelper {

    // Challenge
    const val CHANNEL_ID_CHALLENGE = "challenge_notifications"
    private const val CHANNEL_NAME_CHALLENGE = "Invitaciones de partida"
    private const val CHANNEL_DESCRIPTION_CHALLENGE =
        "Notificaciones de invitaciones de partida amistosa"

    // Friendship
    const val CHANNEL_ID_FRIENDSHIP = "friendship_notifications"
    private const val CHANNEL_NAME_FRIENDSHIP = "Solicitudes de amistad"
    private const val CHANNEL_DESCRIPTION_FRIENDSHIP =
        "Notificaciones de solicitudes de amistad"

    // Notification IDs
    private const val NOTIFICATION_ID_CHALLENGE = 1001
    private const val NOTIFICATION_ID_FRIENDSHIP = 1002

    // Request codes
    private const val REQUEST_CODE_CHALLENGE = 2001
    private const val REQUEST_CODE_FRIENDSHIP = 2002

    fun createChannels(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Canal de challenges (partida amistosa)
            val challengeChannel = NotificationChannel(
                CHANNEL_ID_CHALLENGE,
                CHANNEL_NAME_CHALLENGE,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION_CHALLENGE
            }

            // Canal de solicitudes de amistad
            val friendshipChannel = NotificationChannel(
                CHANNEL_ID_FRIENDSHIP,
                CHANNEL_NAME_FRIENDSHIP,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION_FRIENDSHIP
            }

            notificationManager.createNotificationChannel(challengeChannel)
            notificationManager.createNotificationChannel(friendshipChannel)

        }

    }

    private fun showNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        requestCode: Int,
        title: String,
        text: String,
        intent: Intent
    ) {

        // si la version de android es menor a TIRAMISU(13) no es necesario
        // si no lo es y no se ha concedido el permiso no es necesario mostrar la notificacion
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            && (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            return
        }

        val pendingIntent = PendingIntent.getActivity(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(notificationId, notification)

    }

    /**
     * Muestra una notificación de invitación de partida
     *
     * @param context Contexto de la aplicación
     * @param challengerUsername Nombre de usuario del jugador que lanza el reto
     */
    fun showChallengeNotification(
        context: Context,
        challengerUsername: String
    ) {

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_type", "challenge")
        }

        showNotification(
            context = context,
            channelId = CHANNEL_ID_CHALLENGE,
            notificationId = NOTIFICATION_ID_CHALLENGE,
            requestCode = REQUEST_CODE_CHALLENGE,
            title = "Nueva invitación de partida",
            text = "Has recibido una invitación de $challengerUsername",
            intent = openAppIntent
        )

    }

    fun showFriendRequestNotification(context: Context, username: String) {

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_type", "friend_request")
        }

        showNotification(
            context = context,
            channelId = CHANNEL_ID_FRIENDSHIP,
            notificationId = NOTIFICATION_ID_FRIENDSHIP,
            requestCode = REQUEST_CODE_FRIENDSHIP,
            title = "Nueva solicitud de amistad",
            text = "Has recibido una solicitud de amistad de $username",
            intent = intent
        )

    }

}
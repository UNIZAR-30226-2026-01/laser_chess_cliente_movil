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

object ChallengeNotificationHelper {

    const val CHANNEL_ID_CHALLENGE = "challenge_notifications"
    private const val CHANNEL_NAME_CHALLENGE = "Invitaciones de partida"
    private const val CHANNEL_DESCRIPTION_CHALLENGE
        = "Notificaciones de invitaciones de partida amistosa"

    private const val NOTIFICATION_ID_CHALLENGE = 1001

    fun createChannels(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val challengeChannel = NotificationChannel(
                CHANNEL_ID_CHALLENGE,
                CHANNEL_NAME_CHALLENGE,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION_CHALLENGE
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

            notificationManager.createNotificationChannel(challengeChannel)
        }

    }

    /**
     * Método que muestra una notificación de invitación de partida
     *
     * @param context Contexto de la aplicación
     * @param challengerUsername Nombre de usuario del jugador que lanza el reto
     */
    fun showChallengeNotification(context: Context,
                                  challengerUsername: String) {

        // si la version de android es menor a TIRAMISU(13) no es necesario
        // si no lo es y no se ha concedido el permiso no es necesario mostrar la notificacion
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            && (ContextCompat.checkSelfPermission(context,
            Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)) {
            return
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("open_notifications_dialog", true)
        }

        val pendingIntent = PendingIntent.getActivity(context, 2001, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_CHALLENGE)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Nueva invitación de partida")
            .setContentText("$challengerUsername te ha retado")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID_CHALLENGE, notification)

    }

}
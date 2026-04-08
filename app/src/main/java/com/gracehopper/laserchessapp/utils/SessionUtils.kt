package com.gracehopper.laserchessapp.utils

import android.content.Context
import android.content.Intent
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.ui.auth.LoginActivity

/**
 * Redirige al usuario a la pantalla de inicio de sesión.
 *
 * @param context Contexto de la aplicación.
 */
fun redirectToLogin(context: Context) {

    TokenManager.clear()
    CurrentUserManager.resetSessionExpiredFlag()

    val intent = Intent(context, LoginActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)

}
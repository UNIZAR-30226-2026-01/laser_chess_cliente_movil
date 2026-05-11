package com.gracehopper.laserchessapp

import android.app.Application
import android.content.Context
import android.util.Log
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.utils.TokenManager

/**
 * Clase global que se ejecuta antes que cualquier Activity.
 * Para inicializar NetworkUtils.
 */
class LaserChessApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        NetworkUtils.init(this)
        TokenManager.init(this)

        clearSessionIfUrlChanged()
    }

    private fun clearSessionIfUrlChanged() {

        val prefs = getSharedPreferences("app_environment", Context.MODE_PRIVATE)

        val lastEnvironment = prefs.getString("last_environment", null)
        val currentEnvironment = "${NetworkUtils.BASE_URL}|${NetworkUtils.WS_BASE_URL}"

        Log.d("APP_ENV", "lastEnvironment=$lastEnvironment")
        Log.d("APP_ENV", "currentEnvironment=$currentEnvironment")

        if (lastEnvironment != null && lastEnvironment != currentEnvironment) {
            Log.w("APP_ENV", "Environment cambiado. Limpiando sesión antigua.")
            NetworkUtils.clearSession()
        }

        prefs.edit()
            .putString("last_environment", currentEnvironment)
            .apply()
    }

}
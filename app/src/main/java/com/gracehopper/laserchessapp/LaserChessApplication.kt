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

        val lastBaseUrl = prefs.getString("last_base_url", null)
        val currentBaseUrl = NetworkUtils.BASE_URL

        Log.d("APP_ENV", "lastBaseUrl=$lastBaseUrl")
        Log.d("APP_ENV", "currentBaseUrl=$currentBaseUrl")

        if (lastBaseUrl != null && lastBaseUrl != currentBaseUrl) {
            Log.w("APP_ENV", "Base URL cambiada. Limpiando sesión antigua.")
            NetworkUtils.clearSession()
        }

        prefs.edit()
            .putString("last_base_url", currentBaseUrl)
            .apply()
    }

}
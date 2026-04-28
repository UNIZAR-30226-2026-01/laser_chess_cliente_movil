package com.gracehopper.laserchessapp

import android.app.Application
import com.gracehopper.laserchessapp.data.remote.NetworkUtils

/**
 * Clase global que se ejecuta antes que cualquier Activity.
 * Para inicializar NetworkUtils.
 */
class LaserChessApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        NetworkUtils.init(this)
    }

}
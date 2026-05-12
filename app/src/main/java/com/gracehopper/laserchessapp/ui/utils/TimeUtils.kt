package com.gracehopper.laserchessapp.ui.utils

import android.annotation.SuppressLint

object TimeUtils {

    /**
     * Convierte milisegundos a formato mm:ss
     */
    @SuppressLint("DefaultLocale")
    fun formatTime(millis: Long): String {

        val totalSeconds = millis / 1000

        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d", minutes, seconds)
    }
}
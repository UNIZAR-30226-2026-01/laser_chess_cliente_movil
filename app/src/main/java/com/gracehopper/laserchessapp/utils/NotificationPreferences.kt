package com.gracehopper.laserchessapp.utils

import android.content.Context
import androidx.core.content.edit

object NotificationPreferences {

    private const val PREF_NAME = "notification_preferences"
    private const val KEY_ENABLED = "notifications_enabled"

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit {
                putBoolean(KEY_ENABLED, enabled)
            }
    }

    fun isEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)
    }

}
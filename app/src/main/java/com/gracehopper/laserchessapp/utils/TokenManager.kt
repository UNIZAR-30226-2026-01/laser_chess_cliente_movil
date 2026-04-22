package com.gracehopper.laserchessapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

//Se necesita para manejar la nueva logica de los tokens
object TokenManager {
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_CREDENTIAL = "user_credential"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveAccessToken(token: String) {
        sharedPreferences.edit { putString(KEY_ACCESS_TOKEN, token) }
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun saveUserId(id: Long) {
        sharedPreferences.edit().putLong(KEY_USER_ID, id).apply()
    }

    fun getUserId(): Long {
        return sharedPreferences.getLong(KEY_USER_ID, -1)
    }

    fun saveUserCredential(credential: String) {
        sharedPreferences.edit().putString(KEY_USER_CREDENTIAL, credential).apply()
    }

    fun isLoggedIn(): Boolean {
        return getAccessToken() != null && getUserId() != -1L
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
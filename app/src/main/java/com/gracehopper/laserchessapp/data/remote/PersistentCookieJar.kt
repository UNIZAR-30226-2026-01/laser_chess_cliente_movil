package com.gracehopper.laserchessapp.data.remote

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar(context: Context) : CookieJar {

    private val prefs = context.getSharedPreferences("cookies", Context.MODE_PRIVATE)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val editor = prefs.edit()

        cookies.forEach { cookie ->
            val key = "${cookie.domain}|${cookie.path}|${cookie.name}"
            editor.putString(key, cookie.toString())
        }

        editor.apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = mutableListOf<Cookie>()
        val editor = prefs.edit()

        prefs.all.forEach { entry ->
            val rawCookie = entry.value as? String ?: return@forEach
            val cookie = Cookie.parse(url, rawCookie)

            if (cookie == null || cookie.expiresAt < System.currentTimeMillis()) {
                editor.remove(entry.key)
            } else if (cookie.matches(url)) {
                cookies.add(cookie)
            }
        }

        editor.apply()
        return cookies
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
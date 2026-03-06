package com.gracehopper.laserchessapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.JavaNetCookieJar
import java.net.CookieManager
import java.net.CookiePolicy

object NetworkUtils {
    // Para el emulador de Android creo que 10.0.2.2 pero habra q cambiarlo
    private const val BASE_URL = "http://10.0.2.2:8080/"
    private var apiService: ApiService? = null

    fun getApiService(): ApiService {
        if (apiService != null) return apiService!!

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // manejar cookies automáticamente
        val cookieManager = CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .cookieJar(JavaNetCookieJar(cookieManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        return apiService!!
    }
}
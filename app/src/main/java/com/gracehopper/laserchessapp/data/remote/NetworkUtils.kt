package com.gracehopper.laserchessapp.data.remote

import android.content.Context
import com.gracehopper.laserchessapp.utils.TokenManager
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.JavaNetCookieJar
import java.net.CookieManager
import java.net.CookiePolicy

object NetworkUtils {
    // Para el emulador de Android, 10.0.2.2 pero habra q cambiarlo
    const val BASE_URL = "http://10.0.2.2:8080/"
    // TODO EMULADOR: "http://10.0.2.2:8080/"
    // TODO PORTÁTIL AINHOA: "http://192.168.1.26:8080/"
    // TODO PORTÁTIL JORGE: "http://192.168.0.17:8080/"
    private var apiService: ApiService? = null
    private var okHttpClient: OkHttpClient? = null
    private var refreshClient: OkHttpClient? = null
    private var webSocketClient: OkHttpClient? = null
    private var sseClient: OkHttpClient? = null

    private val cookieManager: CookieManager by lazy {
        CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        }
    }

    private lateinit var persistentCookieJar: PersistentCookieJar

    private val cookieJar: PersistentCookieJar
        get() = persistentCookieJar

    private val tokenAuthenticator by lazy { TokenAuthenticator() }

    private val authInterceptor by lazy {
        Interceptor { chain ->
            val token = TokenManager.getAccessToken()
            val requestBuilder = chain.request().newBuilder()

            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }
    }

    fun init(context: Context) {
        persistentCookieJar = PersistentCookieJar(context.applicationContext)
    }

    fun getOkHttpClient(): OkHttpClient {

        if (okHttpClient != null) return okHttpClient!!

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .cookieJar(cookieJar)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return okHttpClient!!
    }

    fun getRefreshClient(): OkHttpClient {

        if (refreshClient != null) return refreshClient!!

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        refreshClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .cookieJar(cookieJar)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return refreshClient!!

    }

    fun getWebSocketClient(): OkHttpClient {

        if (webSocketClient != null) return webSocketClient!!

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }

        webSocketClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .cookieJar(cookieJar)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS)
            .writeTimeout(0, TimeUnit.SECONDS)
            .pingInterval(20, TimeUnit.SECONDS)
            .build()

        return webSocketClient!!

    }

    fun getSseClient(): OkHttpClient {

        if (sseClient != null) return sseClient!!

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }

        sseClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .cookieJar(cookieJar)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS)
            .writeTimeout(0, TimeUnit.SECONDS)
            .build()

        return sseClient!!

    }

    fun getApiService(): ApiService {

        if (apiService != null) return apiService!!

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        return apiService!!

    }

    fun clearCookies() {
        persistentCookieJar.clear()
    }

}
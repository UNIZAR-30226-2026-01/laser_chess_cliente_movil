package com.gracehopper.laserchessapp.data.remote

import com.gracehopper.laserchessapp.utils.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.JavaNetCookieJar
import java.net.CookieManager
import java.net.CookiePolicy

object NetworkUtils {
    // Para el emulador de Android, 10.0.2.2 pero habra q cambiarlo
    private const val BASE_URL = "http://10.0.2.2:8080/"
    private var apiService: ApiService? = null
    private var okHttpClient: OkHttpClient? = null
    private var refreshClient: OkHttpClient? = null

    private val cookieManager: CookieManager by lazy {
        CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        }
    }

    fun getOkHttpClient(): OkHttpClient {

        if (okHttpClient != null) return okHttpClient!!

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val token = TokenManager.getAccessToken()
                val requestBuilder = chain.request().newBuilder()

                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .authenticator(TokenAuthenticator())
            .cookieJar(JavaNetCookieJar(cookieManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS)
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
            .cookieJar(JavaNetCookieJar(cookieManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return refreshClient!!

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

}
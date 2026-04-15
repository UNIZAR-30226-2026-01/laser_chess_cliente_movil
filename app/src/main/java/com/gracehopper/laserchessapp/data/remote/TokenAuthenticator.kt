package com.gracehopper.laserchessapp.data.remote

import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.utils.TokenManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenAuthenticator : Authenticator {

    private val refreshApiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkUtils.BASE_URL)
            .client(NetworkUtils.getRefreshClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun authenticate(route: Route?, response: Response): Request? {

        val currentToken = TokenManager.getAccessToken()
        if (currentToken.isNullOrEmpty()) {
            return null
        }

        // para evitar bucles infinitos si ya hemos reintentado varias veces
        if (responseCount(response) >= 2) {
            clearSession()
            return null
        }

        // si otra request ya ha refrescado, reutilizarlo
        val requestToken = response.request.header("Authorization")
            ?.removePrefix("Bearer ")
            ?.trim()

        val latestToken = TokenManager.getAccessToken()
        if (!latestToken.isNullOrEmpty() &&
            requestToken != null &&
            requestToken != latestToken
        ) {

            return response.request.newBuilder()
                .header("Authorization", "Bearer $latestToken")
                .build()

        }

        val newAccessToken = refreshAccessToken() ?: run {
            clearSession()
            return null
        }

        TokenManager.saveAccessToken(newAccessToken)

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }

    private fun clearSession() {
        TokenManager.clear()
        CurrentUserManager.expireSession()
    }

    private fun refreshAccessToken(): String? {

        return try {

            val refreshResponse = refreshApiService.refreshToken().execute()

            if (!refreshResponse.isSuccessful) {
                null
            } else {
                refreshResponse.body()?.accessToken
            }
        } catch (e: Exception) {
            null
        }

    }

    private fun responseCount(response: Response): Int {

        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result

    }

}
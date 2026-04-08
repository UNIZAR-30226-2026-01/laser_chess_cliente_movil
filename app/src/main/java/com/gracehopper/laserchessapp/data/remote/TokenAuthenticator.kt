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

    override fun authenticate(route: Route?, response: Response): Request? {

        // para evitar bucles infinitos si ya hemos reintentado varias veces
        if (responseCount(response) >= 2) {
            TokenManager.clear()
            CurrentUserManager.clearMyProfile()
            return null
        }

        val newAccessToken = refreshAccessToken() ?: run {
            TokenManager.clear()
            CurrentUserManager.clearMyProfile()
            return null
        }

        TokenManager.saveAccessToken(newAccessToken)

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }

    private fun refreshAccessToken(): String? {

        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .client(NetworkUtils.getRefreshClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)
            val refreshResponse = apiService.refreshToken().execute()

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
package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.ranking.AllRatingsResponse
import com.gracehopper.laserchessapp.data.model.user.AccountResponse
import com.gracehopper.laserchessapp.data.model.user.UserProfile
import com.gracehopper.laserchessapp.data.model.user.UserRatings
import com.gracehopper.laserchessapp.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repositorio encargado de gestionar
 *
 * Esta clase actúa como un mediador entre la capa de datos remota y el resto
 * de la aplicación, proporcionando métodos para obtener la lista de amigos.
 *
 * @property apiService Instancia de la interfaz de Retrofit para realizar las peticiones a la API
 */
class UserRepository(private val apiService: ApiService) {

    fun getUserProfile(
        userId: Long,
        onSuccess: (UserProfile) -> Unit,
        onError: () -> Unit
    ) {
        apiService.getAccount(userId).enqueue(
            object : Callback<AccountResponse> {
                override fun onResponse(
                    call: Call<AccountResponse>,
                    response: Response<AccountResponse>
                ) {
                    val account = response.body()
                    if (!response.isSuccessful || account == null) {
                        onError()
                        return
                    }

                    getUserRatings(
                        userId = userId,
                        onSuccess = { ratings ->
                            val profile = UserProfile(
                                id = account.accountId.toString(),
                                username = account.username,
                                avatar = account.avatar,
                                level = account.level,
                                xp = account.xp,
                                boardSkin = account.boardSkin,
                                pieceSkin = account.pieceSkin,
                                winAnimation = account.winAnimation,
                                ratings = ratings
                            )
                            onSuccess(profile)
                        }, onError = {
                            onError()
                        }
                    )

                }

                private fun getUserRatings(
                    userId: Long,
                    onSuccess: (UserRatings) -> Unit,
                    onError: () -> Unit
                ) {
                    apiService.getRatings(userId).enqueue(
                        object : Callback<AllRatingsResponse> {
                            override fun onResponse(
                                call: Call<AllRatingsResponse>,
                                response: Response<AllRatingsResponse>
                            ) {
                                val ratings = response.body()
                                if (!response.isSuccessful || ratings == null) {
                                    onError()
                                    return
                                }

                                val userRatings = UserRatings(ratings.blitz,
                                    ratings.rapid, ratings.classic,
                                    ratings.extended)

                                onSuccess(userRatings)

                            }

                            override fun onFailure(call: Call<AllRatingsResponse>, t: Throwable) {
                                onError()
                            }
                        }
                    )

                }

                override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                    onError()
                }

            }
        )

    }





}
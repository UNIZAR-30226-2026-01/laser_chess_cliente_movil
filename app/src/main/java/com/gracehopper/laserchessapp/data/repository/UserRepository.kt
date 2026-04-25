package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.model.ranking.AllRatingsResponse
import com.gracehopper.laserchessapp.data.model.user.AccountResponse
import com.gracehopper.laserchessapp.data.model.user.ChangePasswordRequest
import com.gracehopper.laserchessapp.data.model.user.MyAccountResponse
import com.gracehopper.laserchessapp.data.model.user.MyProfile
import com.gracehopper.laserchessapp.data.model.user.UpdateAccountRequest
import com.gracehopper.laserchessapp.data.model.user.UserProfile
import com.gracehopper.laserchessapp.data.model.user.UserRatings
import com.gracehopper.laserchessapp.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repositorio encargado de gestionar las operaciones relacionadas con el usuario.
 *
 * Esta clase actúa como un mediador entre la capa de datos remota y el resto
 * de la aplicación.
 *
 * @property apiService Instancia de la interfaz de Retrofit para realizar las peticiones a la API
 */
class UserRepository(private val apiService: ApiService) {

    /**
     * Obtiene el perfil del usuario actual.
     */
    fun getMyProfile(onSuccess: (MyProfile) -> Unit,
                     onError: () -> Unit) {

        apiService.getMyAccount().enqueue(
            object : Callback<MyAccountResponse> {
                override fun onResponse(
                    call: Call<MyAccountResponse>,
                    response: Response<MyAccountResponse>
                ) {
                    val myAccount = response.body()
                    if (!response.isSuccessful || myAccount == null) {
                        onError()
                        return
                    }

                    getUserRatings(
                        userId = myAccount.accountId,
                        onSuccess = { ratings ->
                            val profile = MyProfile(
                                id = myAccount.accountId,
                                mail = myAccount.mail,
                                username = myAccount.username,
                                avatar = myAccount.avatar,
                                level = myAccount.level,
                                xp = myAccount.xp,
                                money = myAccount.money,
                                boardSkin = myAccount.boardSkin,
                                pieceSkin = myAccount.pieceSkin,
                                winAnimation = myAccount.winAnimation,
                                ratings = ratings
                            )
                            onSuccess(profile)
                        }, onError = {
                            onError()
                        }
                    )

                }

                override fun onFailure(call: Call<MyAccountResponse>, t: Throwable) {
                    onError()
                }

            }
        )

    }

    /**
     * Obtiene el perfil de un usuario dado su ID.
     *
     * @param userId ID del usuario
     */
    fun getUserProfile(userId: Long,
                       onSuccess: (UserProfile) -> Unit,
                       onError: () -> Unit) {

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
                                id = account.accountId,
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

                override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                    onError()
                }

            }
        )

    }

    /**
     * Obtiene los ratings de un usuario dado su ID.
     *
     * @param userId ID del usuario
     */
    private fun getUserRatings(userId: Long,
                               onSuccess: (UserRatings) -> Unit,
                               onError: () -> Unit) {

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

    /**
     * Actualiza el perfil del usuario actual.
     *
     * @param request Objeto que contiene los datos actualizados del usuario
     */
    fun updateMyProfile(request: UpdateAccountRequest,
                        onSuccess: (MyProfile) -> Unit,
                        onError: (Int?) -> Unit) {

        apiService.updateMyAccount(request).enqueue(
            object : Callback<MyAccountResponse> {


                override fun onResponse(
                    call: Call<MyAccountResponse>,
                    response: Response<MyAccountResponse>) {

                    val myAccount = response.body()
                    if (!response.isSuccessful || myAccount == null) {
                        onError(response.code())
                        return
                    }

                    val currentRatings = CurrentUserManager.getMyCurrentRatings()

                    // si he podido recuperar los ratings del usuario
                    if (currentRatings != null) {

                        val profile = MyProfile(
                            id = myAccount.accountId,
                            mail = myAccount.mail,
                            username = myAccount.username,
                            avatar = myAccount.avatar,
                            level = myAccount.level,
                            xp = myAccount.xp,
                            money = myAccount.money,
                            boardSkin = myAccount.boardSkin,
                            pieceSkin = myAccount.pieceSkin,
                            winAnimation = myAccount.winAnimation,
                            ratings = currentRatings
                        )

                        onSuccess(profile)
                        return

                    }

                    // si no he podido recuperarlos, vuelvo a solicitarlos
                    getUserRatings(
                        userId = myAccount.accountId,
                        onSuccess = { ratings ->
                            val profile = MyProfile(
                                id = myAccount.accountId,
                                mail = myAccount.mail,
                                username = myAccount.username,
                                avatar = myAccount.avatar,
                                level = myAccount.level,
                                xp = myAccount.xp,
                                money = myAccount.money,
                                boardSkin = myAccount.boardSkin,
                                pieceSkin = myAccount.pieceSkin,
                                winAnimation = myAccount.winAnimation,
                                ratings = ratings
                            )
                            onSuccess(profile)
                        },
                        onError = {
                            onError(null)
                        }
                    )

                }

                override fun onFailure(call: Call<MyAccountResponse?>,
                                       t: Throwable) {
                    onError(null)
                }

            }
        )

    }

    /**
     * Elimina la cuenta del usuario actual.
     */
    fun deleteMyAccount(onSuccess: () -> Unit,
                     onError: (Int?) -> Unit) {

        apiService.deleteMyAccount().enqueue(
            object : Callback<Unit> {

                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {

                    if (!response.isSuccessful) {
                        onError(response.code())
                        return
                    }

                    onSuccess()

                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onError(null)
                }

            }
        )

    }

    fun changePassword(
        request: ChangePasswordRequest,
        onSuccess: () -> Unit,
        onError: (Int) -> Unit
    ) {

        apiService.changePassword(request).enqueue(
            object : Callback<Unit> {

                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onError(response.code())
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onError(-1)
                }

            }
        )

    }

}
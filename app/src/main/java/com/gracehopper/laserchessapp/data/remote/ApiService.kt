package com.gracehopper.laserchessapp.data.remote

import com.gracehopper.laserchessapp.data.model.auth.RegisterResponse
import com.gracehopper.laserchessapp.data.model.auth.LoginRequest
import com.gracehopper.laserchessapp.data.model.auth.LoginResponse
import com.gracehopper.laserchessapp.data.model.auth.RegisterRequest
import com.gracehopper.laserchessapp.data.model.user.UpdateAccountRequest
import com.gracehopper.laserchessapp.data.model.game.PendingChallengeResponse
import com.gracehopper.laserchessapp.data.model.ranking.AllRatingsResponse
import com.gracehopper.laserchessapp.data.model.social.CreateFriendshipRequest
import com.gracehopper.laserchessapp.data.model.social.FriendSummary
import com.gracehopper.laserchessapp.data.model.social.FriendshipStatusResponse
import com.gracehopper.laserchessapp.data.model.user.AccountResponse
import com.gracehopper.laserchessapp.data.model.ranking.RatingResponse
import com.gracehopper.laserchessapp.data.model.social.ReceivedRequestsResponse
import com.gracehopper.laserchessapp.data.model.user.MyAccountResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * Interfaz que define los endpoints de la API para interactuar con las cuentas de usuario.
 */
interface ApiService {

    // Endpoint para iniciar sesión
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Endpoint para registrar una nueva cuenta
    @POST("register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("refresh")
    fun refreshToken(): Call<LoginResponse>

    // Endpoint para obtener información de una cuenta
    @GET("api/account")
    fun getMyAccount(): Call<MyAccountResponse>

    @GET("api/account/{id}")
    fun getAccount(@Path("id") id: Long): Call<AccountResponse>

    // Endpoint para actualizar información de una cuenta
    @POST("api/account/update")
    fun updateMyAccount(@Body request: UpdateAccountRequest): Call<MyAccountResponse>

    // Endpoint para eliminar una cuenta
    @DELETE("api/account/delete/")
    fun deleteMyAccount(): Call<Unit>

    // ratings

    @GET("api/rating/{userID}")
    fun getRatings(@Path("userID") userId: Long): Call<AllRatingsResponse>

    @GET("api/rating/{userID}/blitz")
    fun getBlitzElo(@Path("userID") userId: Long): Call<RatingResponse>

    @GET("api/rating/{userID}/rapid")
    fun getRapidElo(@Path("userID") userId: Long): Call<RatingResponse>

    @GET("api/rating/{userID}/classic")
    fun getClassicElo(@Path("userID") userId: Long): Call<RatingResponse>

    @GET("api/rating/{userID}/extended")
    fun getExtendedElo(@Path("userID") userId: Long): Call<RatingResponse>

    // friendship
    @GET("api/friendship")
    fun getFriendships(): Call<List<FriendSummary>>

    @POST("api/friendship")
    fun addFriend(@Body request: CreateFriendshipRequest): Call<Unit>


    @GET("api/friendship/pending")
    fun getReceivedFriendshipRequests(): Call<List<FriendSummary>>

    @GET("api/friendship/pending/count")
    fun getNumReceivedFriendshipRequests(): Call<ReceivedRequestsResponse>

    @GET("api/friendship/sent")
    fun getSentFriendshipRequests(): Call<List<FriendSummary>>

    @GET("api/friendship/{user2Username}")
    fun getFriendshipStatus(@Path("user2Username") username: String): Call<FriendshipStatusResponse>

    @PUT("api/friendship/{user2Username}")
    fun acceptFriendship(@Path("user2Username") username: String): Call<Unit>

    @DELETE("api/friendship/{user2Username}")
    fun deleteFriendship(@Path("user2Username") username: String): Call<Unit>


    // para ver retos de amistosas
    @GET("api/rt/challenges")
    fun getPendingChallenges(): Call<List<PendingChallengeResponse>>

}
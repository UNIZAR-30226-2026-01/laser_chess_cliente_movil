package com.gracehopper.laserchessapp.data.remote

import com.gracehopper.laserchessapp.data.model.auth.RegisterResponse
import com.gracehopper.laserchessapp.data.model.auth.LoginRequest
import com.gracehopper.laserchessapp.data.model.auth.LoginResponse
import com.gracehopper.laserchessapp.data.model.auth.RegisterRequest
import com.gracehopper.laserchessapp.data.model.user.UpdateAccountRequest
import com.gracehopper.laserchessapp.data.model.game.PendingChallengeResponse
import com.gracehopper.laserchessapp.data.model.ranking.AllRatingsResponse
import com.gracehopper.laserchessapp.data.model.ranking.RankingEntryResponse
import com.gracehopper.laserchessapp.data.model.social.CreateFriendshipRequest
import com.gracehopper.laserchessapp.data.model.social.FriendSummary
import com.gracehopper.laserchessapp.data.model.social.FriendshipStatusResponse
import com.gracehopper.laserchessapp.data.model.user.AccountResponse
import com.gracehopper.laserchessapp.data.model.ranking.RatingResponse
import com.gracehopper.laserchessapp.data.model.shop.BuyItemRequest
import com.gracehopper.laserchessapp.data.model.shop.ShopItem
import com.gracehopper.laserchessapp.data.model.social.ReceivedRequestsResponse
import com.gracehopper.laserchessapp.data.model.user.ChangePasswordRequest
import com.gracehopper.laserchessapp.data.model.user.MyAccountResponse
import com.gracehopper.laserchessapp.data.model.user.TimeMode
import com.gracehopper.laserchessapp.data.model.user.XPInfoResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz que define los endpoints de la API para interactuar con las cuentas de usuario.
 */
interface ApiService {

    // Endpoints de AUTH
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("logout")
    fun logout(): Call<Unit>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("refresh")
    fun refreshToken(): Call<LoginResponse>


    // Endpoints de ACCOUNT
    @GET("api/account")
    fun getMyAccount(): Call<MyAccountResponse>

    @GET("api/account/{id}")
    fun getAccount(@Path("id") id: Long): Call<AccountResponse>

    @GET("api/account/xp")
    fun getXPInfo(): Call<XPInfoResponse>

    @POST("api/account/update")
    fun updateMyAccount(@Body request: UpdateAccountRequest): Call<MyAccountResponse>

    @DELETE("api/account/delete")
    fun deleteMyAccount(): Call<Unit>

    @PUT("api/account/passwd")
    fun changePassword(@Body request: ChangePasswordRequest): Call<Unit>


    // Endpoints de RATINGS

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

    @GET("api/rating/ranking/{eloType}/{id}")
    fun getRankById(
        @Path("eloType") eloType: String,
        @Path("id") userId: Long
    ): Call<Long>

    @GET("api/rating/top/{eloType}")
    fun getTopRankUsers(
        @Path("eloType") eloType: String
    ): Call<List<RankingEntryResponse>>

    // Endpoints de FRIENDSHIPS
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

    // Endpoints de ITEM

    @POST("api/item")
    fun buyItem(@Body request: BuyItemRequest): Call<Unit>

    @GET("api/item/inventory")
    fun getInventory(): Call<List<ShopItem>>

    @GET("api/item/{itemID}")
    fun getItem(@Path("itemID") itemID: Int): Call<ShopItem>

    @GET("api/item/all")
    fun getAllShopItems(): Call<List<ShopItem>>

    // Endpoint de CHALLENGE
    @GET("api/rt/challenges")
    fun getPendingChallenges(): Call<List<PendingChallengeResponse>>

}
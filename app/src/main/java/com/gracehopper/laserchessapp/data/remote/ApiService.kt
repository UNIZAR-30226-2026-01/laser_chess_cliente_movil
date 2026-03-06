package com.gracehopper.laserchessapp.data.remote

import com.gracehopper.laserchessapp.network.AccountResponse
import com.gracehopper.laserchessapp.network.LoginRequest
import com.gracehopper.laserchessapp.network.LoginResponse
import com.gracehopper.laserchessapp.network.RegisterRequest
import com.gracehopper.laserchessapp.network.UpdateAccountRequest
import retrofit2.Call
import retrofit2.http.*

// Para definir los endpoints
interface ApiService {

    // Login endpoint
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Registro (crear cuenta)
    @POST("register")
    fun register(@Body request: RegisterRequest): Call<AccountResponse>

    // Obtener cuenta por ID
    @GET("api/account/{id}")  // /api/account/algomas
    fun getAccount(@Path("id") id: Long): Call<AccountResponse>

    // Actualizar cuenta
    @POST("api/account/update")
    fun updateAccount(@Body request: UpdateAccountRequest): Call<AccountResponse>

    // Eliminar cuenta
    @DELETE("api/account/delete/")
    fun deleteAccount(): Call<Unit>
}
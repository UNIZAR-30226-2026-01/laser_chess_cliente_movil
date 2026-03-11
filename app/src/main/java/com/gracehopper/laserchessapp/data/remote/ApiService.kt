package com.gracehopper.laserchessapp.data.remote

import com.gracehopper.laserchessapp.data.model.auth.AccountResponse
import com.gracehopper.laserchessapp.data.model.auth.LoginRequest
import com.gracehopper.laserchessapp.data.model.auth.LoginResponse
import com.gracehopper.laserchessapp.data.model.auth.RegisterRequest
import com.gracehopper.laserchessapp.data.model.auth.UpdateAccountRequest
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
    fun register(@Body request: RegisterRequest): Call<AccountResponse>

    // Endpoint para obtener información de una cuenta
    @GET("api/account/{id}")  // /api/account/algomas
    fun getAccount(@Path("id") id: Long): Call<AccountResponse>

    // Endpoint para actualizar información de una cuenta
    @POST("api/account/update")
    fun updateAccount(@Body request: UpdateAccountRequest): Call<AccountResponse>

    // Endpoint para eliminar una cuenta
    @DELETE("api/account/delete/")
    fun deleteAccount(): Call<Unit>
}
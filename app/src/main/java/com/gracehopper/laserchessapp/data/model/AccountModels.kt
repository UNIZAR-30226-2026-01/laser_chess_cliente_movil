package com.gracehopper.laserchessapp.network

//Para definir los datos que se envian

// Para login (los mismos de LoginDTO)
data class LoginRequest(
    val credential: String, //email/username
    val password: String
)

// Respuesta del login
data class LoginResponse(
    val access_token: String
)

// Registro (los mismos de CreateAccountDTO)
data class RegisterRequest(
    val username: String,
    val mail: String,
    val password: String
)

// Respuesta del registro
data class AccountResponse(
    val account_id: Long
)

// Para actualizar cuenta
data class UpdateAccountRequest(
    val username: String? = null,
    val mail: String? = null,
    val board_skin: Int? = null,
    val piece_skin: Int? = null,
    val win_animation: Int? = null
)
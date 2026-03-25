package com.gracehopper.laserchessapp.data.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Clase de datos que representa una solicitud de registro de una cuenta.
 *
 * @property username Nombre de usuario
 * @property mail Dirección de correo electrónico del usuario
 * @property password Contraseña asociada a la cuenta del usuario
 */
data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("mail") val mail: String,
    @SerializedName("password") val password: String
)
package com.gracehopper.laserchessapp.data.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Clase de datos que representa una solicitud de inicio de sesión.
 *
 * @property credential Identificador del usuario, que puede ser su email o username
 * @property password Contraseña asociada a la cuenta del usuario
 */
data class LoginRequest(
    @SerializedName("credential") val credential: String,
    @SerializedName("password") val password: String
)
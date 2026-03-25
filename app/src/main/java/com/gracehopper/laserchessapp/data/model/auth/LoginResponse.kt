package com.gracehopper.laserchessapp.data.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Clase de datos que representa una respuesta de inicio de sesión.
 *
 * @property accessToken Token de acceso para autenticar en futuras solicitudes
 */
data class LoginResponse(
    @SerializedName("access_token") val accessToken: String
)
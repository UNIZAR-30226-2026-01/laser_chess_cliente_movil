package com.gracehopper.laserchessapp.data.model.auth

/**
 * Clase de datos que representa una respuesta de inicio de sesión.
 *
 * @property access_token Token de acceso para autenticar en futuras solicitudes
 */
data class LoginResponse(
    val access_token: String
)
package com.gracehopper.laserchessapp.data.model.auth

/**
 * Clase de datos que representa una solicitud de inicio de sesión.
 *
 * @property credential Identificador del usuario, que puede ser su email o username
 * @property password Contraseña asociada a la cuenta del usuario
 */
data class LoginRequest(
    val credential: String,
    val password: String
)
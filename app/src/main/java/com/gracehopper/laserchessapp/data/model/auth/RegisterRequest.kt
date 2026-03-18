package com.gracehopper.laserchessapp.data.model.auth

/**
 * Clase de datos que representa una solicitud de registro de una cuenta.
 *
 * @property username Nombre de usuario
 * @property mail Dirección de correo electrónico del usuario
 * @property password Contraseña asociada a la cuenta del usuario
 */
data class RegisterRequest(
    val username: String,
    val mail: String,
    val password: String
)
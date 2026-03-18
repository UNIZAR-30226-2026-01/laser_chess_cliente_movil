package com.gracehopper.laserchessapp.data.model.auth

/**
 * Clase de datos que representa una solicitud de update de una cuenta.
 *
 * @property username Nombre de usuario
 * @property mail Dirección de correo electrónico del usuario
 * @property board_skin Skin del tablero
 * @property piece_skin Skin de las piezas
 * @property win_animation Animación de fin de partida
 */
data class UpdateAccountRequest(
    val username: String? = null,
    val mail: String? = null,
    val board_skin: Int? = null,
    val piece_skin: Int? = null,
    val win_animation: Int? = null
)
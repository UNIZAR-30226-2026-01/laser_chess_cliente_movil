package com.gracehopper.laserchessapp.data.model.user

/**
 * Clase que representa mi perfil de usuario.
 *
 * @property id Identificador único del usuario
 * @property mail Dirección de correo electrónico del usuario
 * @property username Nombre de usuario
 * @property avatar Skin de avatar
 * @property level Nivel del usuario
 * @property xp Puntos de experiencia del usuario
 * @property money Monedas virtuales del usuario
 * @property board_skin Skin del tablero
 * @property piece_skin Skin de las piezas
 * @property win_animation Animación de fin de partida
 * @property elo_blitz Elo en modo blitz
 * @property elo_rapid Elo en modo rapid
 * @property elo_classic Elo en modo classic
 * @property elo_extended Elo en modo extended
 */
data class MyProfile(
    val id: String,
    val mail: String,
    val username: String,
    val avatar: Int,
    val level: Int,
    val xp: Int,
    val money: Int,
    val board_skin: Int,
    val piece_skin: Int,
    val win_animation: Int,
    val ratings: UserRatings
)
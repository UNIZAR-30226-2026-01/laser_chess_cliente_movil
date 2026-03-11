package com.gracehopper.laserchessapp.data.model.profile

/**
 * Clase que representa mi perfil de usuario.
 * @property username Nombre de usuario
 * @property level Nivel del usuario
 * @property xp Puntos de experiencia del usuario
 * @property board_skin Skin del tablero
 * @property piece_skin Skin de las piezas
 * @property win_animation Animación de fin de partida
 * @property elo_blitz Elo en modo blitz
 * @property elo_rapid Elo en modo rapid
 * @property elo_classic Elo en modo classic
 * @property elo_extended Elo en modo extended
 */
data class UserProfile(
    val username: String,
    val level: Int,
    val xp: Int,
    val board_skin: Int,
    val piece_skin: Int,
    val win_animation: Int,
    val elo_blitz: Int,
    val elo_rapid: Int,
    val elo_classic: Int,
    val elo_extended: Int
)
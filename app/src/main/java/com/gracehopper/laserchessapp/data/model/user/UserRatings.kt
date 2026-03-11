package com.gracehopper.laserchessapp.data.model.user

/**
 * Clase que representa las estadísticas de un usuario.
 *
 * @property elo_blitz Elo en modo blitz
 * @property elo_rapid Elo en modo rapid
 * @property elo_classic Elo en modo classic
 * @property elo_extended Elo en modo extended
 */
data class UserRatings(
    val elo_blitz: Int,
    val elo_rapid: Int,
    val elo_classic: Int,
    val elo_extended: Int
)
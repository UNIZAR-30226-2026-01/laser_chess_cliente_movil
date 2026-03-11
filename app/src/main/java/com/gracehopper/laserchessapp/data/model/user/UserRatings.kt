package com.gracehopper.laserchessapp.data.model.user

/**
 * Clase que representa las estadísticas de un usuario.
 *
 * @property blitz Elo en modo blitz
 * @property rapid Elo en modo rapid
 * @property classic Elo en modo classic
 * @property extended Elo en modo extended
 */
data class UserRatings(
    val blitz: Int,
    val rapid: Int,
    val classic: Int,
    val extended: Int
)
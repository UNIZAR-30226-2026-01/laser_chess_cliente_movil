package com.gracehopper.laserchessapp.data.model.social

/**
 * Clase que representa un resumen de Friend.
 *
 * @property id Identificador único del Friend
 * @property username Nombre de usuario del Friend
 * @property avatar Skin de avatar del Friend
 * @property xp Puntos de experiencia del Friend
 */
data class FriendSummary (
    val id: String,
    val username: String,
    val avatar: Int,
    val xp: Int
)
package com.gracehopper.laserchessapp.data.model.user

/**
 * Clase que representa los datos del Profile Card.
 *
 * @property username Nombre de usuario
 * @property avatar Skin de avatar
 * @property xp Puntos de experiencia del usuario
 * @property level Nivel del usuario
 */
data class ProfileCardData (
    val username: String,
    val avatar: Int,
    val xp: Int,
    val level: Int
)
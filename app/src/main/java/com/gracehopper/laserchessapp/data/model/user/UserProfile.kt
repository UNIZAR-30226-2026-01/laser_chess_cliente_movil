package com.gracehopper.laserchessapp.data.model.user

/**
 * Clase que representa mi perfil de usuario.ç
 *
 * @property id Identificador único del usuario
 * @property username Nombre de usuario
 * @property avatar Skin de avatar
 * @property level Nivel del usuario
 * @property xp Puntos de experiencia del usuario
 * @property boardSkin Skin del tablero
 * @property pieceSkin Skin de las piezas
 * @property winAnimation Animación de fin de partida
 * @property ratings Calificaciones del usuario
 */
data class UserProfile(
    val id: Long,
    val username: String,
    val avatar: Int,
    val level: Int,
    val xp: Int,
    val boardSkin: Int,
    val pieceSkin: Int,
    val winAnimation: Int,
    val ratings: UserRatings
)
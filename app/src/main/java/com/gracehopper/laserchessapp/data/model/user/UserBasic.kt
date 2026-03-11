package com.gracehopper.laserchessapp.data.model.user

/**
 * Clase que representa la información básica de un usuario.
 *
 * @property id Identificador único del usuario
 * @property username Nombre de usuario
 * @property avatar Skin de avatar
 */
data class UserBasic (
    val id: String,
    val username: String,
    val avatar: Int
)
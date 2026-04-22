package com.gracehopper.laserchessapp.data.model.ranking

import com.google.gson.annotations.SerializedName

/**
 * Clase que representa un perfil del ranking.
 *
 * @property id Identificador único del usuario
 * @property username Nombre de usuario
 * @property avatar Skin de avatar
 * @property elo Elo del usuario
 * @property position Posición en el ranking
 * @property isCurrentUser Indica si el usuario es el actual
 */
data class RankingEntry(
    val id: Long,
    val username: String,
    val avatar: Int,
    val elo: Int,
    val position: Int,
    val isCurrentUser: Boolean = false
)
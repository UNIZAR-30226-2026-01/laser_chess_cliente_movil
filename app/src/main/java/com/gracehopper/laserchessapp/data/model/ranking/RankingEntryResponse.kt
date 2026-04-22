package com.gracehopper.laserchessapp.data.model.ranking

import com.google.gson.annotations.SerializedName

/**
 * Clase que representa un perfil del ranking.
 *
 * @property id Identificador único del usuario
 * @property username Nombre de usuario
 * @property avatar Skin de avatar
 * @property rating Elo del usuario
 */
data class RankingEntryResponse (
    @SerializedName("user_id") val id: Long,
    @SerializedName("username") val username: String,
    @SerializedName("avatar") val avatar: Int,
    @SerializedName("rating") val rating: Int
)
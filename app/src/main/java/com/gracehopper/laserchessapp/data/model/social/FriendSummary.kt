package com.gracehopper.laserchessapp.data.model.social

import com.google.gson.annotations.SerializedName

/**
 * Clase que representa un resumen de Friend.
 *
 * @property id Identificador único del Friend
 * @property username Nombre de usuario del Friend
 * @property avatar Skin de avatar del Friend
 * @property xp Puntos de experiencia del Friend
 */
data class FriendSummary (
    @SerializedName("account_id") val id: Long,
    @SerializedName("username") val username: String,
    @SerializedName("avatar") val avatar: Int,
    @SerializedName("xp") val xp: Int
)
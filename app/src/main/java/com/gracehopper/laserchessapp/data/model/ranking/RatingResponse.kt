package com.gracehopper.laserchessapp.data.model.ranking

import com.google.gson.annotations.SerializedName
import com.gracehopper.laserchessapp.data.model.user.TimeMode

/**
 * Clase que representa la respuesta de la API de Elo
 *
 * @param elo rating Elo del usuario
 */
data class RatingResponse (
    @SerializedName("user_id") val userID: Int,
    @SerializedName("elo_type") val eloType: TimeMode,
    @SerializedName("value") val value: Int
)
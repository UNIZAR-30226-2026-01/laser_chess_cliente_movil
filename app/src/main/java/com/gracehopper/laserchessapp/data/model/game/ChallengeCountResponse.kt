package com.gracehopper.laserchessapp.data.model.game

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para representar el número de retos pendientes.
 *
 * @property count El número de retos pendientes
 */
data class ChallengeCountResponse (
    @SerializedName("count") val count : Int
)

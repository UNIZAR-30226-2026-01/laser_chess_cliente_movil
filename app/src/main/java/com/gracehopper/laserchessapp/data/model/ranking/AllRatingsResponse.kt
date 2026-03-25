package com.gracehopper.laserchessapp.data.model.ranking

import com.google.gson.annotations.SerializedName

/**
 * Clase de datos para obtener todos los ratings del usuario
 * @property userId Id del usuario
 * @property blitz Rating de blitz
 * @property rapid Rating de rapid
 * @property classic Rating de classic
 * @property extended Rating de extended
 */
data class AllRatingsResponse (
    @SerializedName("user_id") val userId: String,
    @SerializedName("blitz") val blitz: Int,
    @SerializedName("rapid") val rapid: Int,
    @SerializedName("classic") val classic: Int,
    @SerializedName("extended") val extended: Int
)
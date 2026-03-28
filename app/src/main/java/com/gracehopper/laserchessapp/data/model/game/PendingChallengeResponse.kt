package com.gracehopper.laserchessapp.data.model.game

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para representar una solicitud de reto.
 *
 * @property challengerId El ID del usuario que envía la solicitud
 * @property challengerUsername El nombre de usuario del que envía la solicitud
 * @property board El tablero del juego
 * @property startingTime El tiempo de inicio del juego
 * @property timeIncrement El incremento de tiempo
 */
data class PendingChallengeResponse (

    @SerializedName("challenger_id") val challengerId: Long,

    @SerializedName("challenger_username") val challengerUsername: String,

    @SerializedName("board") val board: Int,

    @SerializedName("starting_time") val startingTime: Int,

    @SerializedName("time_increment") val timeIncrement: Int

)
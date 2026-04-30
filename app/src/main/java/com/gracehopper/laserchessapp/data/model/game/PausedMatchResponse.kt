package com.gracehopper.laserchessapp.data.model.game

import com.google.gson.annotations.SerializedName

/**
 * DTO que representa una partida pausada recibida del backend.
 *
 * @property matchId Identificador único de la partida
 * @property p1Id Identificador del primer jugador
 * @property p2Id Identificador del segundo jugador
 * @property p1Username Nombre de usuario del primer jugador
 * @property p2Username Nombre de usuario del segundo jugador
 * @property p2Elo Elo del segundo jugador
 * @property matchType Tipo de partida
 * @property board Tipo de tablero
 * @property timeBase Tiempo base de la partida
 * @property timeIncrement Incremento de tiempo de la partida
 *
 */
data class PausedMatchResponse(
    @SerializedName("match_id")       val matchId: Long,
    @SerializedName("p1_id")         val p1Id: Long,
    @SerializedName("p2_id")         val p2Id: Long,
    @SerializedName("p1_username")   val p1Username: String,
    @SerializedName("p2_username")   val p2Username: String,
    @SerializedName("p2_elo")        val p2Elo: Int,
    @SerializedName("match_type")    val matchType: String,
    @SerializedName("board")         val board: String,
    @SerializedName("time_base")     val timeBase: Int,
    @SerializedName("time_increment") val timeIncrement: Int
)
package com.gracehopper.laserchessapp.data.model.game

import com.google.gson.annotations.SerializedName

data class GameResume(
    @SerializedName("id")               val id: Long = 0,
    @SerializedName("p1_id")           val p1Id: Long = 0,
    @SerializedName("p2_id")           val p2Id: Long = 0,
    @SerializedName("p1_elo")          val p1Elo: Int = 0,
    @SerializedName("p2_elo")          val p2Elo: Int = 0,
    @SerializedName("date")             val date: String = "",
    @SerializedName("winner")           val winner: String = "",
    @SerializedName("termination")      val termination: String = "",
    @SerializedName("match_type")       val matchType: String = "",
    @SerializedName("board")            val board: String = "",
    @SerializedName("movement_history") val movementHistory: String = "",
    @SerializedName("time_base")        val timeBase: Int = 0,
    @SerializedName("time_increment")   val timeIncrement: Int = 0
)
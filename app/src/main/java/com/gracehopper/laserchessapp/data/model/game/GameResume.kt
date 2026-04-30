package com.gracehopper.laserchessapp.data.model.game

data class GameResume(
    val id: Long,
    val p1_id: Long,
    val p2_id: Long,
    val p1_elo: Int,
    val p2_elo: Int,
    val date: String,
    val winner: String,
    val termination: String,
    val match_type: String,
    val board: String,
    val movement_history: String,
    val time_base: Int,
    val time_increment: Int
)
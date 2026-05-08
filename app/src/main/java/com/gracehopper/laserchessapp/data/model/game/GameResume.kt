package com.gracehopper.laserchessapp.data.model.game

data class GameResume(
    val id: Long,
    val p1Id: Long,
    val p2Id: Long,
    val p1Elo: Int,
    val p2Elo: Int,
    val date: String,
    val winner: String,
    val termination: String,
    val matchType: String,
    val board: String,
    val movementHistory: String,
    val timeBase: Int,
    val timeIncrement: Int
)
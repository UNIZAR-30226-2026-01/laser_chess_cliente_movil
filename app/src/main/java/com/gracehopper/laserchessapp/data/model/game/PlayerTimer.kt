package com.gracehopper.laserchessapp.data.model.game

data class PlayerTimer(
    val timeLeftMillis: Long,
    val isRunning: Boolean = false
)
package com.gracehopper.laserchessapp.data.model.game

import com.gracehopper.laserchessapp.data.model.user.TimeMode

data class InProgressMatchSummary(
    val id: String,
    val myTime: String,
    val opponentUsername: String,
    val opponentTime: String,
    val timeMode: TimeMode,
    val boardType: BoardType
)
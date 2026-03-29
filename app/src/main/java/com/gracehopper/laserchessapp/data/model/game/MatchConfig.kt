package com.gracehopper.laserchessapp.data.model.game

import com.gracehopper.laserchessapp.data.model.user.TimeMode
import java.io.Serializable

data class MatchConfig (
    var boardId: Int? = null,
    var boardName: String? = null,
    var mode: TimeMode = TimeMode.BLITZ,
    var startingTimeSeconds: Int = 300, // 5 min
    var incrementSeconds: Int = 0,
    var isCustom: Boolean = false
) : Serializable
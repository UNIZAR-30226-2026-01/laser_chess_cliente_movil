package com.gracehopper.laserchessapp.data.model.user

object TimeModeConfig {

    fun getName(mode: TimeMode): String = when (mode) {
        TimeMode.BLITZ -> "Blitz"
        TimeMode.RAPID -> "Rapid"
        TimeMode.CLASSIC -> "Classic"
        TimeMode.EXTENDED -> "Extended"
        TimeMode.CUSTOM -> "Custom"
    }


    fun getBaseTimeSeconds(mode: TimeMode): Int = when (mode) {
        TimeMode.BLITZ -> 5 * 60
        TimeMode.RAPID -> 15 * 60
        TimeMode.CLASSIC -> 30 * 60
        TimeMode.EXTENDED -> 60 * 60
        TimeMode.CUSTOM -> 0
    }

    fun getAllowedIncrements(mode: TimeMode): List<Int> = when (mode) {
        TimeMode.BLITZ -> listOf(0, 2, 5)
        TimeMode.RAPID -> listOf(0, 5, 10)
        TimeMode.CLASSIC -> listOf(0, 10, 15)
        TimeMode.EXTENDED -> listOf(0, 15, 20)
        TimeMode.CUSTOM -> emptyList()
    }

}

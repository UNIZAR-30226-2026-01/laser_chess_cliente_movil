package com.gracehopper.laserchessapp.data.model.game

data class GamePlayerInfo (
    val id: Long,
    val username: String,
    val avatar: Int,
    val pieceSkin: Int,
    val boardSkin: Int,
    val winAnimation: Int,
)
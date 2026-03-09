package com.gracehopper.laserchessapp.ui.game.pieces

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset

interface Piece {

    val color : Color

    enum class Color {
        Blue,
        Red;

        val isBlue : Boolean
            get() = this == Blue

        val isRed : Boolean
            get() = this == Red
    }

    var position: IntOffset // x e y

    fun getAvailableMoves(pieces: List<Piece>): Set<IntOffset>

}

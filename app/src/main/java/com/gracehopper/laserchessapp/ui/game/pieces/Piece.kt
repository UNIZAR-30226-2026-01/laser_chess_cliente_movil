package com.gracehopper.laserchessapp.ui.game.pieces

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gracehopper.laserchessapp.ui.game.board.Board

abstract class Piece(
    val isRed: Boolean
) {

    var rotation by mutableIntStateOf(0)

    abstract fun getImageRes(): Int

    abstract fun getValidMoves(
        row: Int,
        col: Int,
        board: Board
    ): List<Pair<Int, Int>>
}
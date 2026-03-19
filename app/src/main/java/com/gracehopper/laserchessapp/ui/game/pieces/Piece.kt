package com.gracehopper.laserchessapp.ui.game.pieces

import com.gracehopper.laserchessapp.ui.game.board.Board

abstract class Piece(

    val isRed: Boolean

) {

    var rotation: Int = 0

    abstract fun getImageRes(): Int

    abstract fun getValidMoves(
        row: Int,
        col: Int,
        board: Board
    ): List<Pair<Int, Int>>
}
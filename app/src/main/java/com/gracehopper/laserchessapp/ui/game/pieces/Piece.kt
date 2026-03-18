package com.gracehopper.laserchessapp.ui.game.pieces

import com.gracehopper.laserchessapp.ui.game.board.Board

interface Piece {

    val isRed: Boolean

    fun getImageRes(): Int

    fun getValidMoves(row: Int, col: Int, board: Board): List<Pair<Int, Int>>

}

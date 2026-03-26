package com.gracehopper.laserchessapp.gameLogic.pieces

import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.gameLogic.board.Board

class Deflector(
    isRed: Boolean
) : Piece(isRed) {

    override fun getImageRes(): Int {
        return R.drawable.blue_deflector
    }

    override fun getValidMoves(
        row: Int,
        col: Int,
        board: Board
    ): List<Pair<Int, Int>> {


}
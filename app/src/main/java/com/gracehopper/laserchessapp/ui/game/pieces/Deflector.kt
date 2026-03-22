package com.gracehopper.laserchessapp.ui.game.pieces;

import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.ui.game.board.Board

class Deflector(
    isRed: Boolean
) : Piece(isRed) {

    override fun getImageRes(): Int {
        return R.drawable.img
    }

    override fun getValidMoves(
        row: Int,
        col: Int,
        board: Board
    ): List<Pair<Int, Int>> {

        val moves = mutableListOf<Pair<Int, Int>>()

        val directions = listOf(
            Pair(0, -1),
            Pair(0, 1),
            Pair(-1, 0),
            Pair(1, 0),
            Pair(-1, -1),
            Pair(1, -1),
            Pair(-1, 1),
            Pair(1, 1)
        )

        for ((dcx, dry) in directions) {

            val newCol = col + dcx
            val newRow = row + dry

            if (newRow in 0 until board.rows && newCol in 0 until board.cols) {

                if (board.getPiece(newRow, newCol) == null) {
                    moves.add(Pair(newRow, newCol))
                }
            }
        }

        return moves
    }
}
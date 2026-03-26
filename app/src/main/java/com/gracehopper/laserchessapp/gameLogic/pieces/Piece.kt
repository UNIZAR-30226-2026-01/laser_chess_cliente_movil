package com.gracehopper.laserchessapp.gameLogic.pieces

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.gracehopper.laserchessapp.gameLogic.board.Board

enum class PieceType {
    KING, DEFENDER, SWITCHER, DEFLECTOR
}

abstract class Piece(
    val isRed: Boolean,
    val type: PieceType
) {

    var rotation by mutableIntStateOf(0)

    abstract fun getImageRes(): Int

    open fun getValidMoves(
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

    open fun canSwap(): Boolean = false

    fun rotateLeft() {
        rotation -= 90
    }

    fun rotateRight() {
        rotation += 90
    }
}
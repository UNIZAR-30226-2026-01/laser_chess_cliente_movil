package com.gracehopper.laserchessapp.gameLogic.pieces

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.gameLogic.board.Board

class Piece(
    val isRed: Boolean,
    val type: PieceType
) {

    var rotation by mutableIntStateOf(0)

    fun rotateLeft() {
        rotation -= 90
    }

    fun rotateRight() {
        rotation += 90
    }

     fun getImageRes(): Int {
        return when(type) {
            PieceType.KING -> if (isRed) R.drawable.red_king else R.drawable.blue_king
            PieceType.DEFENDER -> if (isRed) R.drawable.red_shield else R.drawable.blue_shield
            PieceType.SWITCHER -> if (isRed) R.drawable.red_switch else R.drawable.blue_switch
            PieceType.DEFLECTOR -> if (isRed) R.drawable.red_deflector else R.drawable.blue_deflector
            PieceType.LASER -> if (isRed) R.drawable.red_lasser else R.drawable.blue_lasser
        }
    }

    fun getValidMoves(
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

                    val target = board.getPiece(newRow, newCol)

                    if (target == null) {
                        moves.add(Pair(newRow, newCol))

                    } else if (type == PieceType.SWITCHER && target.type != PieceType.SWITCHER) {
                        moves.add(Pair(newRow, newCol))
                    }
                }
            }

            return moves
        }
    }
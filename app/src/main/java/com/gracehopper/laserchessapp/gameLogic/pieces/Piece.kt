package com.gracehopper.laserchessapp.gameLogic.pieces

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.ui.game.GameActivity.Companion.imInternalRed

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

     fun getImageRes(imInternalRed: Boolean): Int {

         val isMyPiece = (this.isRed == imInternalRed)

        return when(type) {
            PieceType.KING -> if (isMyPiece) R.drawable.blue_king else R.drawable.red_king
            PieceType.DEFENDER -> if (isMyPiece) R.drawable.blue_shield else R.drawable.red_shield
            PieceType.DEFLECTOR -> if (isMyPiece) R.drawable.blue_deflector else R.drawable.red_deflector
            PieceType.SWITCHER -> if (isMyPiece) R.drawable.blue_switch else R.drawable.red_switch
            PieceType.LASER -> if (isMyPiece) R.drawable.blue_lasser else R.drawable.red_lasser
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
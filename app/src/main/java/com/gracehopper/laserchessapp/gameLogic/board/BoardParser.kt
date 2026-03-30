package com.gracehopper.laserchessapp.gameLogic.board

import com.gracehopper.laserchessapp.gameLogic.pieces.Piece
import com.gracehopper.laserchessapp.gameLogic.pieces.PieceType

object BoardParser {

    fun boadFromCSV(board: Board, csv: String) {
        val rows = csv.split("\n")

        for (r in rows.indices) {
            val cols = rows[r].split(",")
            for (c in cols.indices) {
                val cell = cols[c].trim()

                if (cell.isNotEmpty()) {
                    val piece = parsePieceFromCode(cell)
                    board.setPiece(r, c, piece)
                }
            }
        }
    }

    private fun parsePieceFromCode(code: String): Piece {

        val pieceChar = code[0]         // L K S D E
        val teamChar = code[1]         // A R
        val orientationChar = if (code.length > 2) code[2] else 'U'

        val isRed = teamChar == 'R'

        val type = when (pieceChar) {
            'L' -> PieceType.LASER
            'K' -> PieceType.KING
            'S' -> PieceType.SWITCHER
            'D' -> PieceType.DEFENDER
            'E' -> PieceType.DEFLECTOR
            else -> {
                throw IllegalArgumentException("Invalid piece code: $code")
            }
        }

        val piece = Piece(isRed, type)

        when (orientationChar) {
            'U' -> piece.rotation = 0
            'R' -> piece.rotation = 90
            'D' -> piece.rotation = 180
            'L' -> piece.rotation = 270
        }

        return piece
    }
}
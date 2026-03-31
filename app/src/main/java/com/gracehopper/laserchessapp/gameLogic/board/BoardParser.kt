package com.gracehopper.laserchessapp.gameLogic.board

import com.gracehopper.laserchessapp.gameLogic.pieces.Piece
import com.gracehopper.laserchessapp.gameLogic.pieces.PieceType

object BoardParser {

    fun boadFromCSV(board: Board, csv: String) {
        val csvRows = csv.split("\n")

        for (r in csvRows.indices) {
            val cols = csvRows[r].split(",")

            for (c in cols.indices) {
                val cell = cols[c].trim()

                if (cell.isNotEmpty()) {
                    val piece = parsePieceFromCode(cell)

                    // Transformación de coordenadas a vertical
                    val boardRow = c
                    val boardCol = board.cols - 1 - r

                    board.setPiece(boardRow, boardCol, piece)
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
            'D' -> PieceType.DEFLECTOR
            'E' -> PieceType.DEFENDER
            else -> {
                throw IllegalArgumentException("Invalid piece code: $code")
            }
        }

        val piece = Piece(isRed, type)

        when (orientationChar) {
            'U' -> piece.rotation = 270
            'R' -> piece.rotation = 0
            'D' -> piece.rotation = 90
            'L' -> piece.rotation = 180
        }

        return piece
    }
}
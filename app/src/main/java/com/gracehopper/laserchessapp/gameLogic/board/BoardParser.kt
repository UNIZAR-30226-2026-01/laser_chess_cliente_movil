package com.gracehopper.laserchessapp.gameLogic.board

import com.gracehopper.laserchessapp.gameLogic.pieces.Piece
import com.gracehopper.laserchessapp.gameLogic.pieces.PieceType

/**
 * Objeto encargado de convertir un tablero en formato CSV
 * en el modelo interno de Board con objetos Piece.
 */
object BoardParser {

    /**
     * Convierte un CSV en un tablero funcional.
     *
     * @param board Tablero donde se insertarán las piezas
     * @param csv Representación del tablero en formato CSV
     */
    fun boadFromCSV(board: Board, csv: String) {
        val csvRows = csv.split("\n")

        for (r in csvRows.indices) {
            val cols = csvRows[r].split(",")

            for (c in cols.indices) {
                val cell = cols[c].trim()

                if (cell.isNotEmpty()) {
                    val piece = parsePieceFromCode(cell)

                    /**
                     * Transformación de coordenadas:
                     * - El CSV viene en formato horizontal (fila-columna)
                     * - Se transforma a coordenadas internas del tablero (orientación vertical)
                     */
                    val boardRow = c
                    val boardCol = board.cols - 1 - r

                    board.setPiece(boardRow, boardCol, piece)
                }
            }
        }
    }

    /**
     * Convierte un código de pieza en un objeto Piece.
     *
     * Formato del código:
     * - 1º char: tipo de pieza (L, K, S, D, E)
     * - 2º char: equipo/color (R = rojo, A = azul)
     * - 3º char (opcional): orientación (U, R, D, L)
     *
     * @param code Código de la pieza
     * @return Objeto Piece configurado
     */
    private fun parsePieceFromCode(code: String): Piece {

        val pieceChar = code[0]         // L K S D E
        val teamChar = code[1]         // A R
        val orientationChar = if (code.length > 2) code[2] else 'U'

        val isRed = teamChar == 'R'

        /**
         * Determinar tipo de pieza
         */
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

        /**
         * Asignar rotación según orientación:
         * U = arriba, R = derecha, D = abajo, L = izquierda
         */
        when (orientationChar) {
            'U' -> piece.rotation = 270
            'R' -> piece.rotation = 0
            'D' -> piece.rotation = 90
            'L' -> piece.rotation = 180
        }

        return piece
    }
}
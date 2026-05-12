package com.gracehopper.laserchessapp.gameLogic.pieces

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.ui.utils.ItemUtils

/**
 * Clase que representa una pieza del juego.
 *
 * Contiene:
 * - Tipo de pieza
 * - Equipo/Color (rojo o azul)
 * - Rotación
 * - Lógica de movimientos y rotaciones
 */
class Piece(
    val isRed: Boolean, val type: PieceType
) {

    /**
     * Rotación de la pieza en grados (0, 90, 180, 270).
     */
    var rotation by mutableIntStateOf(0)

    /**
     * Indica si la pieza puede rotar.
     *
     * @return true si puede rotar, false si no
     */
    fun canRotate(): Boolean {
        return type != PieceType.KING
    }

    /**
     * Indica si la pieza puede rotar a la izquierda.
     *
     * Para el láser:
     * - Se limita la rotación según orientación visual
     *
     * @param isRedPlayer Indica si el jugador actual es rojo
     */
    fun canRotateLeft(isRedPlayer: Boolean): Boolean {
        if (type != PieceType.LASER) return true

        var visualRot = ((rotation % 360) + 360) % 360
        if (isRedPlayer) visualRot = (visualRot + 180) % 360

        return visualRot == 90
    }

    /**
     * Indica si la pieza puede rotar a la derecha.
     *
     * Para el láser:
     * - Se limita la rotación según orientación visual
     *
     * @param isRedPlayer Indica si el jugador actual es rojo
     */
    fun canRotateRight(isRedPlayer: Boolean): Boolean {
        if (type != PieceType.LASER) return true

        var visualRot = ((rotation % 360) + 360) % 360
        if (isRedPlayer) visualRot = (visualRot + 180) % 360

        return visualRot == 0
    }

    /**
     * Rota la pieza 90º a la izquierda.
     */
    fun rotateLeft() {
        if (canRotate()) {
            rotation -= 90
        }
    }

    /**
     * Rota la pieza 90º a la derecha.
     */
    fun rotateRight() {
        if (canRotate()) {
            rotation += 90
        }
    }

    /**
     * Obtiene el recurso de imagen correspondiente a la pieza.
     *
     * @param imInternalRed Indica si el jugador actual es rojo (para perspectiva)
     * @return ID del recurso drawable
     */
    fun getImageRes(imInternalRed: Boolean, myPieceSkin: Int, opponentPieceSkin: Int = 1): Int {

        val isMyPiece = (this.isRed == imInternalRed)

        val myPieces = ItemUtils.getPiecesPackDrawables(myPieceSkin)
        val opponentPieces = ItemUtils.getOpponentPiecesPackDrawables(opponentPieceSkin)

        return when (type) {
            PieceType.KING -> if (isMyPiece) myPieces[0] else opponentPieces[0]
            PieceType.DEFENDER -> if (isMyPiece) myPieces[1] else opponentPieces[1]
            PieceType.DEFLECTOR -> if (isMyPiece) myPieces[2] else opponentPieces[2]
            PieceType.SWITCHER -> if (isMyPiece) myPieces[3] else opponentPieces[3]
            PieceType.LASER -> if (isMyPiece) myPieces[4] else opponentPieces[4]
        }
    }

    /**
     * Calcula los movimientos válidos de la pieza.
     *
     * @param row Fila actual
     * @param col Columna actual
     * @param board Tablero de juego
     * @return Lista de posiciones válidas
     */
    fun getValidMoves(
        row: Int, col: Int, board: Board
    ): List<Pair<Int, Int>> {

        // El láser no se mueve
        if (type == PieceType.LASER) return emptyList()

        val moves = mutableListOf<Pair<Int, Int>>()

        /**
         * Direcciones posibles (8 direcciones)
         */
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

            // Comprobar límites del tablero
            if (newRow in 0 until board.rows && newCol in 0 until board.cols) {

                // Comprobar casillas prohibidas
                if (board.isForbiddenCell(newRow, newCol, this.isRed)) continue

                val target = board.getPiece(newRow, newCol)

                // Movimiento a casilla vacía
                if (target == null) {
                    moves.add(Pair(newRow, newCol))

                    /**
                     * Lógica especial del SWITCHER:
                     * - Puede intercambiarse con otras piezas
                     * - No puede hacerlo con KING ni SWITCHER ni LASER
                     */
                } else if (type == PieceType.SWITCHER && target.type != PieceType.SWITCHER && target.type != PieceType.KING && target.type != PieceType.LASER) {
                    val imRed = ActiveGameManager.imRedPlayer

                    if (target.isRed != imRed) {
                        val enemyForbidden = board.isForbiddenCell(row, col, target.isRed)
                        if (!enemyForbidden) {
                            moves.add(Pair(newRow, newCol))
                        }
                    } else {
                        moves.add(Pair(newRow, newCol))
                    }
                }
            }
        }

        return moves
    }
}
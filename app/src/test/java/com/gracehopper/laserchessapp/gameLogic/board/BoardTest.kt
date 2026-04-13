package com.gracehopper.laserchessapp.gameLogic.board

import com.gracehopper.laserchessapp.gameLogic.pieces.Piece
import com.gracehopper.laserchessapp.gameLogic.pieces.PieceType
import org.junit.Assert.*
import org.junit.Test

class BoardTest {

    /**
     * TEST 1: SET AND GET PIECE
     *
     * Comprueba:
     * - se coloca una pieza en el tablero
     * -> se obtiene correctamente en la misma posición
     */
    @Test
    fun set_and_get_piece() {
        val board = Board(10, 8)
        val piece = Piece(true, PieceType.DEFLECTOR)

        board.setPiece(5, 4, piece)

        val result = board.getPiece(5, 4)

        assertEquals(piece, result)
    }

    /**
     * TEST 2: GET EMPTY CELL
     *
     * Comprueba:
     * - la celda no contiene pieza
     * -> devuelve null
     */
    @Test
    fun get_empty_cell_returns_null() {
        val board = Board(10, 8)

        val result = board.getPiece(3, 3)

        assertNull(result)
    }

    /**
     * TEST 3: FORBIDDEN CELL RED
     *
     * Comprueba:
     * - la celda es prohibida para piezas rojas
     * -> devuelve true
     */
    @Test
    fun forbidden_cell_red_piece() {
        val board = Board(10, 8)

        val result = board.isForbiddenCell(0, 3, true)

        assertTrue(result)
    }

    /**
     * TEST 4: FORBIDDEN CELL BLUE
     *
     * Comprueba:
     * - la celda es prohibida para piezas azules
     * -> devuelve true
     */
    @Test
    fun forbidden_cell_blue_piece() {
        val board = Board(10, 8)

        val result = board.isForbiddenCell(9, 3, false)

        assertTrue(result)
    }

    /**
     * TEST 5: ALLOWED CELL
     *
     * Comprueba:
     * - la celda no es prohibida
     * -> devuelve false
     */
    @Test
    fun allowed_cell() {
        val board = Board(10, 8)

        val result = board.isForbiddenCell(5, 4, true)

        assertFalse(result)
    }

    /**
     * TEST 6: CAPTURE PIECE
     *
     * Comprueba:
     * - una pieza es eliminada del tablero
     * -> la celda queda vacía (null)
     */
    @Test
    fun capture_piece_removes_it_from_board() {
        val board = Board(10, 8)

        val attacker = Piece(true, PieceType.DEFLECTOR)
        val victim = Piece(false, PieceType.DEFENDER)

        board.setPiece(5, 4, attacker)
        board.setPiece(5, 5, victim)

        board.setPiece(5, 5, null)

        val result = board.getPiece(5, 5)

        assertNull(result)
    }
}
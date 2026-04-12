package com.gracehopper.laserchessapp.gameLogic.board

import com.gracehopper.laserchessapp.gameLogic.pieces.Piece
import com.gracehopper.laserchessapp.gameLogic.pieces.PieceType
import org.junit.Assert.*
import org.junit.Test

class BoardTest {

    /**
     * TEST 1: Poner pieza y obtenerla
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
     * TEST 2: Obtener celda vacía da null
     */
    @Test
    fun get_empty_cell_returns_null() {
        val board = Board(10, 8)

        val result = board.getPiece(3, 3)

        assertNull(result)
    }

    /**
     * TEST 4: Celda prohibida roja
     */
    @Test
    fun forbidden_cell_red_piece() {
        val board = Board(10, 8)

        val result = board.isForbiddenCell(0, 3, true)

        assertTrue(result)
    }

    /**
     * TEST 5: Celda prohibida azul
     */
    @Test
    fun forbidden_cell_blue_piece() {
        val board = Board(10, 8)

        val result = board.isForbiddenCell(9, 3, false)

        assertTrue(result)
    }

    /**
     * TEST 6: Celda permitida
     */
    @Test
    fun allowed_cell() {
        val board = Board(10, 8)

        val result = board.isForbiddenCell(5, 4, true)

        assertFalse(result)
    }

    /**
     * TEST 7: Capturar pieza (simulacion)
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
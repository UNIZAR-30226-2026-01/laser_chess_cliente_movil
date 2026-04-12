package com.gracehopper.laserchessapp.gameLogic.pieces

import com.gracehopper.laserchessapp.gameLogic.board.Board
import org.junit.Assert.*
import org.junit.Test

class PieceMovementTest {

    /**
     * TEST 1: Pieza se puede moverse totalmente
     */
    @Test
    fun piece_all_movements_possible() {
        val board = Board(10, 8)
        val piece = Piece(true, PieceType.DEFLECTOR)

        board.setPiece(5, 4, piece)

        val moves = piece.getValidMoves(5, 4, board)

        assertEquals(8, moves.size)
    }

    /**
     * TEST 2: Pieza en una esquina tiene menos movimientos
     */
    @Test
    fun piece_movements_in_corner() {
        val board = Board(10, 8)
        val piece = Piece(false, PieceType.DEFLECTOR)

        board.setPiece(0, 0, piece)

        val moves = piece.getValidMoves(0, 0, board)

        assertEquals(2, moves.size)
    }

    /**
     * TEST 3: Laser no se puede mover
     */
    @Test
    fun laser_dont_move() {
        val board = Board(10, 8)
        val laser = Piece(true, PieceType.LASER)

        board.setPiece(5, 4, laser)

        val moves = laser.getValidMoves(5, 4, board)

        assertTrue(moves.isEmpty())
    }

    /**
     * TEST 4: Pieza que no sea switcher no puede permutar con otra
     */
    @Test
    fun normal_piece_cant_permute() {
        val board = Board(10, 8)

        val piece = Piece(true, PieceType.DEFLECTOR)
        val other = Piece(true, PieceType.DEFENDER)

        board.setPiece(5, 4, piece)
        board.setPiece(5, 5, other)

        val moves = piece.getValidMoves(5, 4, board)

        assertFalse(moves.contains(Pair(5, 5)))
    }

    /**
     * TEST 5: Switcher permuta
     */
    @Test
    fun switcher_permute() {
        val board = Board(10, 8)

        val switcher = Piece(true, PieceType.SWITCHER)
        val other = Piece(true, PieceType.DEFENDER)

        board.setPiece(5, 4, switcher)
        board.setPiece(5, 5, other)

        val moves = switcher.getValidMoves(5, 4, board)

        assertTrue(moves.contains(Pair(5, 5)))
    }

    /**
     * TEST 6: Una pieza no se puede salir del tablero
     */
    @Test
    fun dont_get_out() {
        val board = Board(10, 8)
        val piece = Piece(true, PieceType.DEFLECTOR)

        board.setPiece(0, 0, piece)

        val moves = piece.getValidMoves(0, 0, board)

        val out = moves.any { it.first < 0 || it.second < 0 }

        assertFalse(out)
    }
}
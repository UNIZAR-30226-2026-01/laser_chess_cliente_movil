package com.gracehopper.laserchessapp.gameLogic.pieces

import com.gracehopper.laserchessapp.gameLogic.board.Board
import org.junit.Assert.*
import org.junit.Test

class PieceMovementTest {

    /**
     * TEST 1: ALL MOVES AVAILABLE
     *
     * Comprueba:
     * - la pieza está en el centro
     * -> tiene todos los movimientos posibles
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
     * TEST 2: CORNER MOVES LIMITED
     *
     * Comprueba:
     * - la pieza está en una esquina
     * -> tiene menos movimientos disponibles
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
     * TEST 3: LASER CANNOT MOVE
     *
     * Comprueba:
     * - la pieza es un láser
     * -> no tiene movimientos disponibles
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
     * TEST 4: NORMAL PIECE NO PERMUTE
     *
     * Comprueba:
     * - una pieza normal intenta intercambiarse
     * -> el movimiento no está permitido
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
     * TEST 5: SWITCHER PERMUTE
     *
     * Comprueba:
     * - una pieza switcher intenta intercambiarse
     * -> el movimiento está permitido
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
     * TEST 6: NO OUT OF BOUNDS
     *
     * Comprueba:
     * - la pieza está en el borde
     * -> ningún movimiento sale del tablero
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
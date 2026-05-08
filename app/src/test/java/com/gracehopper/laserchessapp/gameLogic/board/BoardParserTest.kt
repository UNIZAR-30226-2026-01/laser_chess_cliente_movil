package com.gracehopper.laserchessapp.gameLogic.board

import com.gracehopper.laserchessapp.gameLogic.pieces.PieceType
import org.junit.Assert.*
import org.junit.Test

class BoardParserTest {

    /**
     * TEST 1: PARSE PIECE TYPE — LASER
     *
     * Comprueba:
     * - el CSV contiene un código de láser
     * -> la pieza colocada en el tablero es de tipo LASER
     */
    @Test
    fun parse_piece_type_laser() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "LR")

        val piece = board.getPiece(0, 7)

        assertNotNull(piece)
        assertEquals(PieceType.LASER, piece!!.type)
    }

    /**
     * TEST 2: PARSE PIECE TYPE — KING
     *
     * Comprueba:
     * - el CSV contiene un código de rey
     * -> la pieza colocada en el tablero es de tipo KING
     */
    @Test
    fun parse_piece_type_king() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "KR")

        val piece = board.getPiece(0, 7)

        assertNotNull(piece)
        assertEquals(PieceType.KING, piece!!.type)
    }

    /**
     * TEST 3: PARSE PIECE TYPE — DEFLECTOR
     *
     * Comprueba:
     * - el CSV contiene un código de deflector
     * -> la pieza colocada en el tablero es de tipo DEFLECTOR
     */
    @Test
    fun parse_piece_type_deflector() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "DR")

        val piece = board.getPiece(0, 7)

        assertNotNull(piece)
        assertEquals(PieceType.DEFLECTOR, piece!!.type)
    }

    /**
     * TEST 4: PARSE PIECE TYPE — DEFENDER
     *
     * Comprueba:
     * - el CSV contiene un código de defensor
     * -> la pieza colocada en el tablero es de tipo DEFENDER
     */
    @Test
    fun parse_piece_type_defender() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "ER")

        val piece = board.getPiece(0, 7)

        assertNotNull(piece)
        assertEquals(PieceType.DEFENDER, piece!!.type)
    }

    /**
     * TEST 5: PARSE PIECE TYPE — SWITCHER
     *
     * Comprueba:
     * - el CSV contiene un código de switcher
     * -> la pieza colocada en el tablero es de tipo SWITCHER
     */
    @Test
    fun parse_piece_type_switcher() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "SR")

        val piece = board.getPiece(0, 7)

        assertNotNull(piece)
        assertEquals(PieceType.SWITCHER, piece!!.type)
    }

    /**
     * TEST 6: PARSE TEAM RED
     *
     * Comprueba:
     * - el código de pieza lleva equipo R
     * -> la pieza tiene isRed = true
     */
    @Test
    fun parse_team_red() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "DR")

        val piece = board.getPiece(0, 7)

        assertNotNull(piece)
        assertTrue(piece!!.isRed)
    }

    /**
     * TEST 7: PARSE TEAM BLUE
     *
     * Comprueba:
     * - el código de pieza lleva equipo A
     * -> la pieza tiene isRed = false
     */
    @Test
    fun parse_team_blue() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "DA")

        val piece = board.getPiece(0, 7)

        assertNotNull(piece)
        assertFalse(piece!!.isRed)
    }

    /**
     * TEST 8: PARSE ORIENTATION UP
     *
     * Comprueba:
     * - la orientación del código es U (arriba)
     * -> la rotación de la pieza es 270
     */
    @Test
    fun parse_orientation_up() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "DRU")

        val piece = board.getPiece(0, 7)

        assertNotNull(piece)
        assertEquals(270, piece!!.rotation)
    }

    /**
     * TEST 9: PARSE ORIENTATION RIGHT
     *
     * Comprueba:
     * - la orientación del código es R (derecha)
     * -> la rotación de la pieza es 0
     */
    @Test
    fun parse_orientation_right() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "DRR")

        val piece = board.getPiece(0, 7)

        assertNotNull(piece)
        assertEquals(0, piece!!.rotation)
    }

    /**
     * TEST 10: PARSE ORIENTATION DOWN
     *
     * Comprueba:
     * - la orientación del código es D (abajo)
     * -> la rotación de la pieza es 90
     */
    @Test
    fun parse_orientation_down() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "DRD")

        val piece = board.getPiece(0, 7)

        assertNotNull(piece)
        assertEquals(90, piece!!.rotation)
    }

    /**
     * TEST 11: PARSE ORIENTATION LEFT
     *
     * Comprueba:
     * - la orientación del código es L (izquierda)
     * -> la rotación de la pieza es 180
     */
    @Test
    fun parse_orientation_left() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "DRL")

        val piece = board.getPiece(0, 7)

        assertNotNull(piece)
        assertEquals(180, piece!!.rotation)
    }

    /**
     * TEST 12: PARSE ORIENTATION DEFAULT
     *
     * Comprueba:
     * - el código de pieza no incluye orientación
     * -> la rotación por defecto es 270 (equivalente a U)
     */
    @Test
    fun parse_orientation_default_is_up() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "DR")

        val piece = board.getPiece(0, 7)

        assertNotNull(piece)
        assertEquals(270, piece!!.rotation)
    }

    /**
     * TEST 13: COORDINATE TRANSFORM — FIRST CSV ROW
     *
     * Comprueba:
     * - una pieza en la primera fila del CSV (r=0, c=0)
     * -> se coloca en boardRow=0, boardCol=7
     */
    @Test
    fun coordinate_transform_first_csv_row() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "DR")

        val piece = board.getPiece(0, 7)

        assertNotNull(piece)
    }

    /**
     * TEST 14: COORDINATE TRANSFORM — LAST CSV ROW
     *
     * Comprueba:
     * - una pieza en la última fila del CSV (r=7, c=0)
     * -> se coloca en boardRow=0, boardCol=0
     */
    @Test
    fun coordinate_transform_last_csv_row() {
        val board = Board(10, 8)

        val csv = buildString {
            repeat(7) { append("\n") }
            append("DR")
        }

        BoardParser.boardFromCSV(board, csv)

        val piece = board.getPiece(0, 0)

        assertNotNull(piece)
    }

    /**
     * TEST 15: COORDINATE TRANSFORM — SECOND CSV COLUMN
     *
     * Comprueba:
     * - una pieza en la primera fila, segunda columna del CSV (r=0, c=1)
     * -> se coloca en boardRow=1, boardCol=7
     */
    @Test
    fun coordinate_transform_second_csv_column() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, ",DR")

        val piece = board.getPiece(1, 7)

        assertNotNull(piece)
    }

    /**
     * TEST 16: EMPTY CELL NOT PLACED
     *
     * Comprueba:
     * - el CSV contiene una celda vacía
     * -> no se coloca ninguna pieza en esa posición
     */
    @Test
    fun empty_cell_not_placed_on_board() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, ",DR")

        val emptyCell = board.getPiece(0, 7)

        assertNull(emptyCell)
    }

    /**
     * TEST 17: MULTIPLE PIECES PLACED
     *
     * Comprueba:
     * - el CSV contiene varias piezas en la misma fila
     * -> cada pieza se coloca en su boardRow correcta
     */
    @Test
    fun multiple_pieces_in_same_csv_row() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "DR,KA")

        val first = board.getPiece(0, 7)
        val second = board.getPiece(1, 7)

        assertNotNull(first)
        assertEquals(PieceType.DEFLECTOR, first!!.type)

        assertNotNull(second)
        assertEquals(PieceType.KING, second!!.type)
    }

    /**
     * TEST 18: INVALID PIECE CODE THROWS EXCEPTION
     *
     * Comprueba:
     * - el CSV contiene un código de pieza inválido
     * -> se lanza IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException::class)
    fun invalid_piece_code_throws_exception() {
        val board = Board(10, 8)

        BoardParser.boardFromCSV(board, "XR")
    }
}
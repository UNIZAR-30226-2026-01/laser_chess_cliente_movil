package com.gracehopper.laserchessapp.gameLogic.move

import org.junit.Assert.*
import org.junit.Test

class CoordsConverterTest {

    /**
     * TEST 1: NOTATION TO POSITION — ORIGEN
     *
     * Comprueba:
     * - la notación es "a1" (esquina origen)
     * -> devuelve la posición (0, 0)
     */
    @Test
    fun notation_to_position_origin() {
        val result = CoordsConverter.notationToPosition("a1")

        assertEquals(Pair(0, 0), result)
    }

    /**
     * TEST 2: NOTATION TO POSITION — ESQUINA OPUESTA
     *
     * Comprueba:
     * - la notación es "j8" (esquina opuesta)
     * -> devuelve la posición (9, 7)
     */
    @Test
    fun notation_to_position_far_corner() {
        val result = CoordsConverter.notationToPosition("j8")

        assertEquals(Pair(9, 7), result)
    }

    /**
     * TEST 3: NOTATION TO POSITION — ÚLTIMA FILA PRIMERA COLUMNA
     *
     * Comprueba:
     * - la notación es "j1"
     * -> devuelve la posición (9, 0)
     */
    @Test
    fun notation_to_position_last_row_first_col() {
        val result = CoordsConverter.notationToPosition("j1")

        assertEquals(Pair(9, 0), result)
    }

    /**
     * TEST 4: NOTATION TO POSITION — PRIMERA FILA ÚLTIMA COLUMNA
     *
     * Comprueba:
     * - la notación es "a8"
     * -> devuelve la posición (0, 7)
     */
    @Test
    fun notation_to_position_first_row_last_col() {
        val result = CoordsConverter.notationToPosition("a8")

        assertEquals(Pair(0, 7), result)
    }

    /**
     * TEST 5: NOTATION TO POSITION — CENTRO
     *
     * Comprueba:
     * - la notación es "e4" (posición central)
     * -> devuelve la posición (4, 3)
     */
    @Test
    fun notation_to_position_center() {
        val result = CoordsConverter.notationToPosition("e4")

        assertEquals(Pair(4, 3), result)
    }

    /**
     * TEST 6: POSITION TO NOTATION — ORIGEN
     *
     * Comprueba:
     * - la posición es (0, 0)
     * -> devuelve la notación "a1"
     */
    @Test
    fun position_to_notation_origin() {
        val result = CoordsConverter.positionToNotation(Pair(0, 0))

        assertEquals("a1", result)
    }

    /**
     * TEST 7: POSITION TO NOTATION — ESQUINA OPUESTA
     *
     * Comprueba:
     * - la posición es (9, 7)
     * -> devuelve la notación "j8"
     */
    @Test
    fun position_to_notation_far_corner() {
        val result = CoordsConverter.positionToNotation(Pair(9, 7))

        assertEquals("j8", result)
    }

    /**
     * TEST 8: POSITION TO NOTATION — ÚLTIMA FILA PRIMERA COLUMNA
     *
     * Comprueba:
     * - la posición es (9, 0)
     * -> devuelve la notación "j1"
     */
    @Test
    fun position_to_notation_last_row_first_col() {
        val result = CoordsConverter.positionToNotation(Pair(9, 0))

        assertEquals("j1", result)
    }

    /**
     * TEST 9: POSITION TO NOTATION — PRIMERA FILA ÚLTIMA COLUMNA
     *
     * Comprueba:
     * - la posición es (0, 7)
     * -> devuelve la notación "a8"
     */
    @Test
    fun position_to_notation_first_row_last_col() {
        val result = CoordsConverter.positionToNotation(Pair(0, 7))

        assertEquals("a8", result)
    }

    /**
     * TEST 10: POSITION TO NOTATION — CENTRO
     *
     * Comprueba:
     * - la posición es (4, 3)
     * -> devuelve la notación "e4"
     */
    @Test
    fun position_to_notation_center() {
        val result = CoordsConverter.positionToNotation(Pair(4, 3))

        assertEquals("e4", result)
    }

    /**
     * TEST 11: IDA Y VUELTA — NOTATION → POSITION → NOTATION
     *
     * Comprueba:
     * - se convierte una notación a posición y de vuelta a notación
     * -> el resultado es idéntico a la entrada original
     */
    @Test
    fun roundtrip_notation_to_position_to_notation() {
        val original = "f5"

        val result = CoordsConverter.positionToNotation(
            CoordsConverter.notationToPosition(original)
        )

        assertEquals(original, result)
    }

    /**
     * TEST 12: IDA Y VUELTA — POSITION → NOTATION → POSITION
     *
     * Comprueba:
     * - se convierte una posición a notación y de vuelta a posición
     * -> el resultado es idéntico a la entrada original
     */
    @Test
    fun roundtrip_position_to_notation_to_position() {
        val original = Pair(7, 5)

        val result = CoordsConverter.notationToPosition(
            CoordsConverter.positionToNotation(original)
        )

        assertEquals(original, result)
    }
}
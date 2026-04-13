package com.gracehopper.laserchessapp.gameLogic.laser

import org.junit.Assert.*
import org.junit.Test

class LaserUtilsTest {

    /**
     * TEST 1: LASER VERTICAL
     *
     * Comprueba:
     * - el láser se mueve verticalmente
     * -> se genera el camino correcto
     */
    @Test
    fun laser_vertical_line() {
        val path = LaserUtils.parseLaserPath("a1,c1")

        val expected = listOf(
            Pair(0, 0),
            Pair(1, 0),
            Pair(2, 0)
        )

        assertEquals(expected, path)
    }

    /**
     * TEST 2: LASER HORIZONTAL
     *
     * Comprueba:
     * - el láser se mueve horizontalmente
     * -> se genera el camino correcto
     */
    @Test
    fun laser_horizontal_line() {
        val path = LaserUtils.parseLaserPath("a1,a3")

        val expected = listOf(
            Pair(0, 0),
            Pair(0, 1),
            Pair(0, 2)
        )

        assertEquals(expected, path)
    }

    /**
     * TEST 3: LASER WITH TURN
     *
     * Comprueba:
     * - el láser cambia de dirección
     * -> se genera el camino completo correctamente
     */
    @Test
    fun laser_with_turn() {
        val path = LaserUtils.parseLaserPath("a1,a3,c3")

        val expected = listOf(
            Pair(0, 0),
            Pair(0, 1),
            Pair(0, 2),
            Pair(1, 2),
            Pair(2, 2)
        )

        assertEquals(expected, path)
    }

    /**
     * TEST 4: EMPTY PATH
     *
     * Comprueba:
     * - el path es null o vacío
     * -> devuelve lista vacía
     */
    @Test
    fun laser_empty_path() {
        val path = LaserUtils.parseLaserPath(null)

        assertTrue(path.isEmpty())
    }
}
package com.gracehopper.laserchessapp.gameLogic.laser

import org.junit.Assert.*
import org.junit.Test

class LaserUtilsTest {

    /**
     * Test 1: Laser vertical
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
     * Test 2: Laser horizontal
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
     * Test 3: Laser con giro
     */
    @Test
    fun laser_with_turn() {
        val path = LaserUtils.parseLaserPath("a1,a3,c3")

        val expected = listOf(
            Pair(0,0),
            Pair(0,1),
            Pair(0,2),
            Pair(1,2),
            Pair(2,2)
        )

        assertEquals(expected, path)
    }

    /**
     * Test 4: Laser vacío
     */
    @Test
    fun laser_empty_path() {
        val path = LaserUtils.parseLaserPath(null)

        assertTrue(path.isEmpty())
    }
}
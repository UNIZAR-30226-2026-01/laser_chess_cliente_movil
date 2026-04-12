package com.gracehopper.laserchessapp.gameLogic.move

import org.junit.Assert.*
import org.junit.Test

class MoveParserTest {

    /**
     * TEST 1: Parsear movimiento simple
     */
    @Test
    fun parseMove_simple_traslation() {
        val move = MoveParser.parseMove("Tj4:f4")

        assertEquals('T', move.type)
        assertEquals("j4", move.from)
        assertEquals("f4", move.to)
        assertNull(move.destroyed)
        assertNull(move.timer)
    }

    /**
     * TEST 2: Parsear rotacion derecha
     */
    @Test
    fun parseMove_right_rotation() {
        val move = MoveParser.parseMove("Rj1")

        assertEquals('R', move.type)
        assertEquals("j1", move.from)
        assertNull(move.to)
        assertNull(move.destroyed)
    }

    /**
     * TEST 3: Parsear movimiento con timer
     */
    @Test
    fun parseMove_with_timer() {
        val move = MoveParser.parseMove("Tj4:f4%{123.45}")

        assertEquals('T', move.type)
        assertEquals("j4", move.from)
        assertEquals("f4", move.to)
        assertEquals(123.45, move.timer)
    }

    /**
     * TEST 4: Parsear movimiento completo
     */
    @Test
    fun parseMove_complete() {
        val move = MoveParser.parseMove("Tj4:f4xh6%{123.45}")

        assertEquals('T', move.type)
        assertEquals("j4", move.from)
        assertEquals("f4", move.to)
        assertEquals("h6", move.destroyed)
        assertEquals(123.45, move.timer)
    }

    /**
     * TEST 5: Parsear movimiento con captura
     */
    @Test
    fun parseMove_with_capture_sets_destroyed() {
        val move = MoveParser.parseMove("Tj1:f1xh1")

        assertEquals("h1", move.destroyed)
    }
}
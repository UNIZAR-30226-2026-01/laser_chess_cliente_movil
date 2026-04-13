package com.gracehopper.laserchessapp.gameLogic.move

import org.junit.Assert.*
import org.junit.Test

class MoveParserTest {

    /**
     * TEST 1: PARSE SIMPLE MOVE
     *
     * Comprueba:
     * - se parsea un movimiento de traslación
     * -> los campos se asignan correctamente
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
     * TEST 2: PARSE ROTATE RIGHT
     *
     * Comprueba:
     * - se parsea una rotación derecha
     * -> type y from son correctos
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
     * TEST 3: PARSE WITH TIMER
     *
     * Comprueba:
     * - el movimiento incluye timer
     * -> el valor se parsea correctamente
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
     * TEST 4: PARSE COMPLETE MOVE
     *
     * Comprueba:
     * - el movimiento incluye destino, captura y timer
     * -> todos los campos se asignan correctamente
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
     * TEST 5: PARSE WITH CAPTURE
     *
     * Comprueba:
     * - el movimiento incluye captura
     * -> destroyed se asigna correctamente
     */
    @Test
    fun parseMove_with_capture_sets_destroyed() {
        val move = MoveParser.parseMove("Tj1:f1xh1")

        assertEquals("h1", move.destroyed)
    }
}
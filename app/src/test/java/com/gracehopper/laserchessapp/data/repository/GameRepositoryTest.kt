package com.gracehopper.laserchessapp.data.repository

import org.junit.Assert.*
import org.junit.Test

class GameRepositoryTest {

    /**
     * TEST 1: SEND MESSAGE OK
     *
     * Comprueba:
     * - el mensaje se serializa correctamente
     * -> sender recibe el JSON correcto
     */
    @Test
    fun sendMessage_ok() {

        var sentMessage: String? = null
        val repository = GameRepository { sentMessage = it }

        repository.sendMessage("Test", "contenido")

        assertEquals(
            """{"Type":"Test","Content":"contenido"}""",
            sentMessage
        )
    }

    /**
     * TEST 2: SEND MOVE OK
     *
     * Comprueba:
     * - las coordenadas se convierten correctamente
     * -> sender recibe el formato correcto de movimiento
     */
    @Test
    fun sendMove_ok() {

        var sentMessage: String? = null
        val repository = GameRepository { sentMessage = it }

        repository.sendMove(Pair(0, 0), Pair(1, 1))

        // a1 -> b2
        assertEquals(
            """{"Type":"Move","Content":"Ta1:b2"}""",
            sentMessage
        )
    }

    /**
     * TEST 3: ROTATE RIGHT OK
     *
     * Comprueba:
     * - la posición se convierte correctamente
     * -> sender recibe el formato correcto de rotación derecha
     */
    @Test
    fun sendRotateRight_ok() {

        var sentMessage: String? = null
        val repository = GameRepository { sentMessage = it }

        repository.sendRotateRight(Pair(2, 3))

        assertEquals(
            """{"Type":"Move","Content":"Rc4"}""",
            sentMessage
        )
    }

    /**
     * TEST 4: ROTATE LEFT OK
     *
     * Comprueba:
     * - la posición se convierte correctamente
     * -> sender recibe el formato correcto de rotación izquierda
     */
    @Test
    fun sendRotateLeft_ok() {

        var sentMessage: String? = null
        val repository = GameRepository { sentMessage = it }

        repository.sendRotateLeft(Pair(2, 3))

        assertEquals(
            """{"Type":"Move","Content":"Lc4"}""",
            sentMessage
        )
    }

    /**
     * TEST 5: SEND MOVE LIMITS
     *
     * Comprueba:
     * - las coordenadas máximas del tablero se convierten correctamente
     * -> sender recibe la notación correcta
     */
    @Test
    fun sendMove_limits() {

        var sentMessage: String? = null
        val repository = GameRepository { sentMessage = it }

        repository.sendMove(Pair(9, 7), Pair(0, 0))

        assertEquals(
            """{"Type":"Move","Content":"Tj8:a1"}""",
            sentMessage
        )
    }
}
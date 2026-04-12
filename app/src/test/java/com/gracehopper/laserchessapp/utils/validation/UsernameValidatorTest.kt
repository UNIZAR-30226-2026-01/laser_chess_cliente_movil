package com.gracehopper.laserchessapp.utils.validation

import org.junit.Assert.*
import org.junit.Test

/**
 * Clase de prueba para el validador de username.
 */
class UsernameValidatorTest {

    /**
     * TEST 1: USERNAME VACÍO
     *
     * Comprueba:
     * - username vacío -> EmptyUsername
     */
    @Test
    fun username_validate_vacio_error() {
        val result = UsernameValidator.validate("")
        assertEquals(UsernameValidationResult.EmptyUsername, result)
    }

    /**
     * TEST 2: USERNAME LARGO
     *
     * Comprueba:
     * - username largo -> LongUsername
     */
    @Test
    fun username_validate_largo_error() {
        val result = UsernameValidator.validate("username9012345678901234567890123456789012345678901")
        assertEquals(UsernameValidationResult.LongUsername, result)
    }

    /**
     * TEST 3: USERNAME INVÁLIDO
     *
     * Comprueba:
     * - username inválido -> InvalidUsername
     */
    @Test
    fun username_validate_invalido_error() {
        val result = UsernameValidator.validate("invalid username")
        assertEquals(UsernameValidationResult.InvalidUsername, result)
    }

    /**
     * TEST 4: USERNAME VÁLIDO
     *
     * Comprueba:
     * - username válido -> Valid
     */
    @Test
    fun username_validate_valido() {
        val result = UsernameValidator.validate("username")
        assertEquals(UsernameValidationResult.Valid, result)
    }

}
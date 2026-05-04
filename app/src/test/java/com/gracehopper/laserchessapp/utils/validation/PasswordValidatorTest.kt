package com.gracehopper.laserchessapp.utils.validation

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Clase de prueba para el validador de contraseña.
 */
class PasswordValidatorTest {

    /**
     * TEST 1: PASSWORD VACÍA
     *
     * Comprueba:
     * - password vacía -> EmptyPassword
     */
    @Test
    fun password_validate_vacia_error() {
        val result = PasswordValidator.validate("")
        assertEquals(PasswordValidationResult.EmptyPassword, result)
    }

    /**
     * TEST 2: PASSWORD CORTA
     *
     * Comprueba:
     * - password corta -> ShortPassword
     */
    @Test
    fun password_validate_corta_error() {
        val result = PasswordValidator.validate("short")
        assertEquals(PasswordValidationResult.ShortPassword, result)
    }

    /**
     * TEST 3: PASSWORD LARGA
     *
     * Comprueba:
     * - password larga -> LongPassword
     */
    @Test
    fun password_validate_larga_error() {
        val result = PasswordValidator.validate("longpassword345678901234567890123456789012345678901")
        assertEquals(PasswordValidationResult.LongPassword, result)
    }

    /**
     * TEST 4: PASSWORD VÁLIDA
     *
     * Comprueba:
     * - password válida -> Valid
     */
    @Test
    fun password_validate_valida() {
        val result = PasswordValidator.validate("password")
        assertEquals(PasswordValidationResult.Valid, result)
    }

}
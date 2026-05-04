package com.gracehopper.laserchessapp.utils.validation

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Clase de prueba para el validador de mail.
 */
class MailValidatorTest {

    /**
     * TEST 1: MAIL VACÍO
     *
     * Comprueba:
     * - mail vacío -> EmptyMail
     */
    @Test
    fun mail_validate_vacio_error() {
        val result = MailValidator.validate("")
        assertEquals(MailValidationResult.EmptyMail, result)
    }

    /**
     * TEST 2: MAIL INVÁLIDO
     *
     * Comprueba:
     * - mail inválido -> InvalidMail
     */
    @Test
    fun mail_validate_invalido_error() {
        val result = MailValidator.validate("invalid mail")
        assertEquals(MailValidationResult.InvalidMail, result)
    }

    /**
     * TEST 3: MAIL VÁLIDO
     *
     * Comprueba:
     * - mail válido -> Valid
     */
    @Test
    fun mail_validate_valido() {
        val result = MailValidator.validate("mail@test.ts")
        assertEquals(MailValidationResult.Valid, result)
    }

}
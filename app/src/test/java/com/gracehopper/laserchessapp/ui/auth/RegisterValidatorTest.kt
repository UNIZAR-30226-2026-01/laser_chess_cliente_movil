package com.gracehopper.laserchessapp.ui.auth

import org.junit.Test
import org.junit.Assert.*

/**
 * Clase de prueba para el validador de registro.
 */
class RegisterValidatorTest {

    /**
     * TEST 1: USERNAME VACÍO
     *
     * Comprueba:
     * - username vacío -> EmptyUsername
     */
    @Test
    fun register_validate_username_vacio_error() {
        val result = RegisterValidator.validate("", "mail@test.tst",
            "password", "password")
        assertEquals(RegisterValidationResult.EmptyUsername, result)
    }

    /**
     * TEST 2: USERNAME LARGO
     *
     * Comprueba:
     * - username largo -> LongUsername
     */
    @Test
    fun register_validate_username_largo_error() {
        val result = RegisterValidator.validate(
            "username9012345678901234567890123456789012345678901",
            "mail@test.tst", "password", "password")
        assertEquals(RegisterValidationResult.LongUsername, result)
    }

    /**
     * TEST 3: USERNAME INVÁLIDO
     *
     * Comprueba:
     * - username inválido -> InvalidUsername
     */
    @Test
    fun register_validate_username_invalido_error() {
        val result = RegisterValidator.validate("invalid username",
            "mail@test.tst", "password", "password")
        assertEquals(RegisterValidationResult.InvalidUsername, result)
    }

    /**
     * TEST 4: MAIL VACÍO
     *
     * Comprueba:
     * - mail vacío -> EmptyMail
     */
    @Test
    fun register_validate_mail_vacio_error() {
        val result = RegisterValidator.validate("username", "",
            "password", "password")
        assertEquals(RegisterValidationResult.EmptyMail, result)
    }

    /**
     * TEST 5: MAIL INVÁLIDO
     *
     * Comprueba:
     * - mail inválido -> InvalidMail
     */
    @Test
    fun register_validate_mail_invalido_error() {
        val result = RegisterValidator.validate("username", "invalidmail",
            "password", "password")
        assertEquals(RegisterValidationResult.InvalidMail, result)
    }

    /**
     * TEST 6: PASSWORD VACÍA
     *
     * Comprueba:
     * - password vacío -> EmptyPassword
     */
    @Test
    fun register_validate_password_vacia_error() {
        val result = RegisterValidator.validate("username", "mail@test.tst",
            "", "password")
        assertEquals(RegisterValidationResult.EmptyPassword, result)
    }

    /**
     * TEST 7: PASSWORD CORTA
     *
     * Comprueba:
     * - password corta -> ShortPassword
     */
    @Test
    fun register_validate_password_corta_error() {
        val result = RegisterValidator.validate("username", "mail@test.tst",
            "short", "short")
        assertEquals(RegisterValidationResult.ShortPassword, result)
    }

    /**
     * TEST 8: PASSWORD LARGA
     *
     * Comprueba:
     * - password larga -> LongPassword
     */
    @Test
    fun register_validate_password_larga_error() {
        val result = RegisterValidator.validate("username", "mail@test.tst",
            "longpassword345678901234567890123456789012345678901",
            "longpassword345678901234567890123456789012345678901")
        assertEquals(RegisterValidationResult.LongPassword, result)
    }

    /**
     * TEST 9: CONFIRMACIÓN DE PASSWORD VACÍA
     *
     * Comprueba:
     * - confirmación de password vacía -> EmptyConfirmPassword
     */
    @Test
    fun register_validate_confirm_password_vacia_error() {
        val result = RegisterValidator.validate("username", "mail@test.tst",
            "password", "")
        assertEquals(RegisterValidationResult.EmptyConfirmPassword, result)
    }

    /**
     * TEST 10: PASSWORD Y CONFIRMACIÓN DE PASSWORD NO COINCIDEN
     *
     * Comprueba:
     * - password y confirmación de password no coinciden -> PasswordsMismatch
     */
    @Test
    fun register_validate_passwords_no_coinciden_error() {
        val result = RegisterValidator.validate("username", "mail@test.tst",
            "password", "different")
        assertEquals(RegisterValidationResult.PasswordsMismatch, result)
    }

    /**
     * TEST 11: CREDENCIALES VÁLIDAS
     *
     * Comprueba:
     * - credenciales válidas -> Valid
     */
    @Test
    fun register_validate_credenciales_correctas() {
        val result = RegisterValidator.validate("username", "mail@test.tst",
            "password", "password")
        assertEquals(RegisterValidationResult.Valid, result)
    }

}
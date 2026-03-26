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
     * TEST 2: MAIL VACÍO
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
     * TEST 3: MAIL INVÁLIDO
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
     * TEST 4: PASSWORD VACÍA
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
     * TEST 5: PASSWORD CORTA
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
     * TEST 6: CONFIRMACIÓN DE PASSWORD VACÍA
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
     * TEST 7: PASSWORD Y CONFIRMACIÓN DE PASSWORD NO COINCIDEN
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
     * TEST 8: CREDENCIALES VÁLIDAS
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
package com.gracehopper.laserchessapp.ui.auth

import org.junit.Assert.*
import org.junit.Test
/**
 * Clase de prueba para el validador de inicio de sesión.
 */
class LoginValidatorTest {

    /**
     * TEST 1: CREDENCIALES VACÍAS
     *
     * Comprueba:
     * - credenciales vacías -> EmptyCredential
     */
    @Test
    fun login_validate_credential_vacia_error() {
        val result = LoginValidator.validate("", "password")
        assertEquals(LoginValidationResult.EmptyCredential, result)
    }

    /**
     * TEST 2: CREDENCIALES INVÁLIDAS
     *
     * Comprueba:
     * - credenciales inválidas -> InvalidCredential
     */
    @Test
    fun login_validate_credencial_invalida_error() {
        val result = LoginValidator.validate("invalid credential", "password")
        assertEquals(LoginValidationResult.InvalidCredential, result)
    }

    /**
     * TEST 3: PASSWORD VACÍA
     *
     * Comprueba:
     * - password vacía -> EmptyPassword
     */
    @Test
    fun login_validate_password_vacia_error() {
        val result = LoginValidator.validate("username", "")
        assertEquals(LoginValidationResult.EmptyPassword, result)
    }

    /**
     * TEST 4: PASSWORD CORTA
     *
     * Comprueba:
     * - password corto -> ShortPassword
     */
    @Test
    fun login_validate_password_corta_error() {
        val result = LoginValidator.validate("username", "short")
        assertEquals(LoginValidationResult.ShortPassword, result)
    }

    /**
     * TEST 5: PASSWORD LARGA
     *
     * Comprueba:
     * - password largo -> LongPassword
     */
    @Test
    fun login_validate_password_larga_error() {
        val result = LoginValidator.validate("username",
            "longpassword345678901234567890123456789012345678901")
        assertEquals(LoginValidationResult.LongPassword, result)
    }

    /**
     * TEST 6: CREDENCIALES VÁLIDAS
     *
     * Comprueba:
     * - credenciales válidas -> Valid
     */
    @Test
    fun login_validate_credenciales_correctas() {
        val result = LoginValidator.validate("username", "password")
        assertEquals(LoginValidationResult.Valid, result)
    }

}
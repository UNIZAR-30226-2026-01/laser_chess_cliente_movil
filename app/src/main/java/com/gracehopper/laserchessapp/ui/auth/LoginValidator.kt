package com.gracehopper.laserchessapp.ui.auth

/**
 * Clase de validación para el inicio de sesión.
 */
object LoginValidator {

    /**
     * Valida los datos de inicio de sesión.
     *
     * @param credential Credenciales del usuario (e-mail o nombre de usuario)
     * @param password Contraseña del usuario
     * @return Resultado de la validación
     */
    fun validate(credential: String, password: String): LoginValidationResult {

        val credentialTrimmed = credential.trim()

        return when {
            credentialTrimmed.isEmpty() -> LoginValidationResult.EmptyCredential
            credentialTrimmed.contains(" ") -> LoginValidationResult.InvalidCredential
            password.isEmpty() -> LoginValidationResult.EmptyPassword
            password.length < 6 -> LoginValidationResult.ShortPassword
            password.length > 50 -> LoginValidationResult.LongPassword
            else -> LoginValidationResult.Valid
        }

    }

}

/**
 * Resultados de la validación del inicio de sesión.
 */
sealed class LoginValidationResult {

    data object Valid : LoginValidationResult()
    data object EmptyCredential : LoginValidationResult()
    data object InvalidCredential : LoginValidationResult()
    data object EmptyPassword : LoginValidationResult()
    data object ShortPassword : LoginValidationResult()
    data object LongPassword: LoginValidationResult()

}
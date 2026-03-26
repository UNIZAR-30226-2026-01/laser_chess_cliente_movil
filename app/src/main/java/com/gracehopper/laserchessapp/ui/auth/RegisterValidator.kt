package com.gracehopper.laserchessapp.ui.auth

import android.util.Patterns

/**
 * Clase de validación para el registro de usuarios.
 */
object RegisterValidator {

    /**
     * Valida los datos de registro.
     *
     * @param username Nombre de usuario
     * @param mail Dirección de correo electrónico
     * @param password Contraseña
     * @param confirmPassword Confirmación de contraseña
     * @return Resultado de la validación
     */
    fun validate(username: String, mail: String, password: String, confirmPassword: String)
            : RegisterValidationResult {

        return when {
            username.isEmpty() -> RegisterValidationResult.EmptyUsername
            mail.isEmpty() -> RegisterValidationResult.EmptyMail
            !Patterns.EMAIL_ADDRESS.matcher(mail).matches() -> RegisterValidationResult.InvalidMail
            password.isEmpty() -> RegisterValidationResult.EmptyPassword
            confirmPassword.isEmpty() -> RegisterValidationResult.EmptyConfirmPassword
            password.length < 6 -> RegisterValidationResult.ShortPassword
            password != confirmPassword -> RegisterValidationResult.PasswordsMismatch
            else -> RegisterValidationResult.Valid
        }

    }

}

/**
 * Resultados de la validación del registro.
 */
sealed class RegisterValidationResult {

    data object Valid : RegisterValidationResult()
    data object EmptyUsername : RegisterValidationResult()
    data object EmptyMail : RegisterValidationResult()
    data object InvalidMail : RegisterValidationResult()
    data object EmptyPassword : RegisterValidationResult()
    data object EmptyConfirmPassword : RegisterValidationResult()
    data object ShortPassword : RegisterValidationResult()
    data object PasswordsMismatch : RegisterValidationResult()

}
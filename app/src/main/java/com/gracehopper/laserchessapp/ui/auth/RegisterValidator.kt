package com.gracehopper.laserchessapp.ui.auth

import java.util.regex.Pattern

/**
 * Clase de validación para el registro de usuarios.
 */
object RegisterValidator {

    // Expresión regular para validar direcciones de correo electrónico
    private val EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    )

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
            username.length > 50 -> RegisterValidationResult.LongUsername
            mail.isEmpty() -> RegisterValidationResult.EmptyMail
            !EMAIL_PATTERN.matcher(mail).matches() -> RegisterValidationResult.InvalidMail
            password.isEmpty() -> RegisterValidationResult.EmptyPassword
            confirmPassword.isEmpty() -> RegisterValidationResult.EmptyConfirmPassword
            password.length < 6 -> RegisterValidationResult.ShortPassword
            password.length > 50 -> RegisterValidationResult.LongPassword
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
    data object LongUsername : RegisterValidationResult()
    data object EmptyMail : RegisterValidationResult()
    data object InvalidMail : RegisterValidationResult()
    data object EmptyPassword : RegisterValidationResult()
    data object EmptyConfirmPassword : RegisterValidationResult()
    data object ShortPassword : RegisterValidationResult()
    data object LongPassword : RegisterValidationResult()
    data object PasswordsMismatch : RegisterValidationResult()

}
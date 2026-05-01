package com.gracehopper.laserchessapp.utils.validation

/**
 * Clase de validación para el correo electrónico.
 */
object PasswordValidator {

    // Longitud mínima de la contraseña
    const val MIN_LENGTH = 6

    // Longitud máxima de la contraseña
    const val MAX_LENGTH = 50

    /**
     * Valida la contraseña
     *
     * @param password Contraseña a valida
     * @return Resultado de la validación
     */
    fun validate (password : String) : PasswordValidationResult {

        val trimmed = password.trim()

        return when {

            trimmed.isEmpty() -> {
                PasswordValidationResult.EmptyPassword
            }

            trimmed.length < MIN_LENGTH -> {
                PasswordValidationResult.ShortPassword
            }

            trimmed.length > MAX_LENGTH -> {
                PasswordValidationResult.LongPassword
            }

            else -> {
                PasswordValidationResult.Valid
            }

        }

    }

}

/**
 * Resultados de la validación del correo electrónico.
 */
sealed class PasswordValidationResult {
    object Valid : PasswordValidationResult()
    object EmptyPassword : PasswordValidationResult()
    object ShortPassword : PasswordValidationResult()
    object LongPassword : PasswordValidationResult()
}
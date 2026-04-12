package com.gracehopper.laserchessapp.utils.validation

/**
 * Clase de validación para el nombre de usuario.
 */
object UsernameValidator {

    // Longitud máxima del nombre de usuario
    const val MAX_LENGTH = 50

    /**
     * Valida el nombre de usuario.
     *
     * @param username Nombre de usuario a validar
     * @return Resultado de la validación
     */
    fun validate (username : String) : UsernameValidationResult {

        val trimmed = username.trim()

        return when {

            trimmed.isEmpty() -> {
                UsernameValidationResult.EmptyUsername
            }

            trimmed.length > MAX_LENGTH -> {
                UsernameValidationResult.LongUsername
            }

            trimmed.contains(" ") -> {
                UsernameValidationResult.InvalidUsername
            }

            else -> {
                UsernameValidationResult.Valid
            }

        }

    }

}

/**
 * Resultados de la validación del nombre de usuario.
 */
sealed class UsernameValidationResult {
    object Valid : UsernameValidationResult()
    object EmptyUsername : UsernameValidationResult()
    object LongUsername : UsernameValidationResult()
    object InvalidUsername : UsernameValidationResult()
}
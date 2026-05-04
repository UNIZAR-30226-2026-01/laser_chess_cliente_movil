package com.gracehopper.laserchessapp.utils.validation

import java.util.regex.Pattern

/**
 * Clase de validación para el correo electrónico.
 */
object MailValidator {

    // Expresión regular para validar direcciones de correo electrónico
    private val EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    )

    /**
     * Valida el correo electrónico.
     *
     * @param mail Mail a validar
     * @return Resultado de la validación
     */
    fun validate (mail : String) : MailValidationResult {

        val trimmed = mail.trim()

        return when {

            trimmed.isEmpty() -> {
                MailValidationResult.EmptyMail
            }

            !EMAIL_PATTERN.matcher(trimmed).matches() -> {
                MailValidationResult.InvalidMail
            }

            else -> {
                MailValidationResult.Valid
            }

        }

    }

}

/**
 * Resultados de la validación del correo electrónico.
 */
sealed class MailValidationResult {
    object Valid : MailValidationResult()
    object EmptyMail : MailValidationResult()
    object InvalidMail : MailValidationResult()
}
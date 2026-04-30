package com.gracehopper.laserchessapp.ui.auth

import com.gracehopper.laserchessapp.utils.validation.MailValidationResult
import com.gracehopper.laserchessapp.utils.validation.MailValidator
import com.gracehopper.laserchessapp.utils.validation.UsernameValidationResult
import com.gracehopper.laserchessapp.utils.validation.UsernameValidator
import java.util.regex.Pattern

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

        // Validación del username
        return when (UsernameValidator.validate(username)) {
            UsernameValidationResult.EmptyUsername -> RegisterValidationResult.EmptyUsername
            UsernameValidationResult.LongUsername -> RegisterValidationResult.LongUsername
            UsernameValidationResult.InvalidUsername -> RegisterValidationResult.InvalidUsername
            UsernameValidationResult.Valid -> {

                // Validación del mail
                when (MailValidator.validate(mail)) {

                    MailValidationResult.EmptyMail -> RegisterValidationResult.EmptyMail
                    MailValidationResult.InvalidMail -> RegisterValidationResult.InvalidMail
                    MailValidationResult.Valid -> {

                        when {
                            password.isEmpty() -> RegisterValidationResult.EmptyPassword
                            confirmPassword.isEmpty() -> RegisterValidationResult.EmptyConfirmPassword
                            password.length < 6 -> RegisterValidationResult.ShortPassword
                            password.length > 50 -> RegisterValidationResult.LongPassword
                            password != confirmPassword -> RegisterValidationResult.PasswordsMismatch
                            else -> RegisterValidationResult.Valid
                        }


                }

            }
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
    data object InvalidUsername: RegisterValidationResult()
    data object EmptyMail : RegisterValidationResult()
    data object InvalidMail : RegisterValidationResult()
    data object EmptyPassword : RegisterValidationResult()
    data object EmptyConfirmPassword : RegisterValidationResult()
    data object ShortPassword : RegisterValidationResult()
    data object LongPassword : RegisterValidationResult()
    data object PasswordsMismatch : RegisterValidationResult()

}
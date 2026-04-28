package com.gracehopper.laserchessapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.auth.LoginRequest
import com.gracehopper.laserchessapp.data.model.auth.RegisterRequest
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.AuthRepository
import com.gracehopper.laserchessapp.data.repository.UserRepository
import com.gracehopper.laserchessapp.ui.main.MainActivity
import com.gracehopper.laserchessapp.utils.TokenManager
import com.gracehopper.laserchessapp.utils.validation.UsernameValidator

/**
 * Actividad de inicio de sesión.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var loadingLayout: LinearLayout
    private lateinit var loginLayout: View
    private lateinit var registerLayout: View
    private lateinit var loginCredential: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button

    private lateinit var goToRegisterLink: TextView
    private lateinit var goToLoginLink: TextView

    private lateinit var registerUsername: EditText
    private lateinit var registerEmail: EditText
    private lateinit var registerPassword: EditText
    private lateinit var registerConfirmPassword: EditText
    private lateinit var registerButton: Button

    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        authRepository = AuthRepository(NetworkUtils.getApiService())
        userRepository = UserRepository(NetworkUtils.getApiService())

        initViews()
        initListeners()
        loginLayout.visibility = View.GONE

        checkSession()
    }

    private fun initViews() {
        loginLayout = findViewById(R.id.loginLayout)
        registerLayout = findViewById(R.id.registerLayout)

        // Elementos de login
        loginCredential = findViewById(R.id.editTextCredential)
        loginPassword = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLoginContinue)

        goToRegisterLink = findViewById(R.id.textViewGoToRegisterLink)
        goToLoginLink = findViewById(R.id.textViewGoToLoginLink)

        // Elementos de register
        registerUsername = findViewById(R.id.editTextRegisterUsername)
        registerEmail = findViewById(R.id.editTextRegisterEmail)
        registerPassword = findViewById(R.id.editTextRegisterPassword)
        registerConfirmPassword = findViewById(R.id.editTextRegisterConfirmPassword)
        registerButton = findViewById(R.id.buttonRegisterConfirm)

    }

    private fun initListeners() {

        // Listener para el botón de login
        loginButton.setOnClickListener {
            performLogin()
        }

        // Listeners para cambiar entre pop-ups
        goToRegisterLink.setOnClickListener {
            showRegister()
        }
        goToLoginLink.setOnClickListener {
            showLogin()
        }

        // Listener para el botón de register
        registerButton.setOnClickListener {
            performRegister()
        }

    }

    private fun checkSession() {

        val token = TokenManager.getAccessToken()

        if (!token.isNullOrBlank()) {

            // Si se realiza la llamada correctamente, el token es válido
            userRepository.getMyProfile(
                onSuccess = { account ->
                    Log.d("SESSION", "Sesión válida. userId=${account.id}")
                    TokenManager.saveUserId(account.id)
                    goToMain()
                },
                onError = {
                    Log.w("SESSION", "Token inválido/no refrescable")
                    showLogin()
                }
            )

        } else {

            authRepository.refreshToken(
                onSuccess = {
                    Log.d("SESSION", "Refresh inicial OK")
                    goToMain()
                },
                onError = {
                    Log.w("SESSION", "No hay sesión recuperable")
                    showLogin()
                }
            )

        }

    }

    // Pop-up de registro
    private fun showRegister() {
        // Ocultar login, mostrar registro
        loginLayout.visibility = View.GONE
        registerLayout.visibility = View.VISIBLE

        // Limpiar por si acaso
        clearRegisterForm()
    }

    // Pop-up de login
    private fun showLogin() {
        // Ocultar registro, mostrar login
        registerLayout.visibility = View.GONE
        loginLayout.visibility = View.VISIBLE

        // Limpiar por si acaso
        clearLoginForm()
    }

    // Limpiar
    private fun clearRegisterForm() {
        registerUsername.text?.clear()
        registerEmail.text?.clear()
        registerPassword.text?.clear()
        registerConfirmPassword.text?.clear()
    }

    // Limpiar
    private fun clearLoginForm() {
        loginCredential.text?.clear()
        loginPassword.text?.clear()
    }

    private fun performLogin() {
        val credential = loginCredential.text.toString().trim()
        val password = loginPassword.text.toString().trim()

        when (LoginValidator.validate(credential, password)) {
            LoginValidationResult.EmptyCredential -> {
                loginCredential.error = "Introduce un username/e-mail"
                loginCredential.requestFocus()
                return
            }

            LoginValidationResult.InvalidCredential -> {
                loginCredential.error = "Username/e-mail no debe tener espacios"
                loginCredential.requestFocus()
                return
            }

            LoginValidationResult.EmptyPassword -> {
                loginPassword.error = "Introduce una contraseña"
                loginPassword.requestFocus()
                return
            }

            LoginValidationResult.ShortPassword -> {
                loginPassword.error = "Mínimo 6 caracteres"
                loginPassword.requestFocus()
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            LoginValidationResult.LongPassword -> {
                loginPassword.error = "Máximo 50 caracteres"
                loginPassword.requestFocus()
                Toast.makeText(
                    this,
                    "La contraseña debe tener como máximo 50 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            LoginValidationResult.Valid -> Unit

        }

        // Por si acaso apago el boton
        loginButton.isEnabled = false
        loginButton.text = "Iniciando sesión..."
        val request = LoginRequest(credential, password)

        authRepository.login(
            request = request,
            onSuccess = { loginResponse ->
                restoreLoginButton()

                TokenManager.saveAccessToken(loginResponse.accessToken)
                TokenManager.saveUserCredential(credential)

                Log.d("LoginActivity", "Token guardado: ${loginResponse.accessToken}")

                loginLayout.visibility = View.GONE
                registerLayout.visibility = View.GONE

                Toast.makeText(this, "¡Bienvenid@!", Toast.LENGTH_SHORT).show()
                goToMain()
            },
            onError = { errorCode ->
                restoreLoginButton()
                loginPassword.setText("")

                when (errorCode) {
                    401 -> Toast.makeText(
                        this,
                        "Credenciales incorrectas",
                        Toast.LENGTH_SHORT
                    ).show()

                    400 -> Toast.makeText(
                        this,
                        "Datos inválidos",
                        Toast.LENGTH_SHORT
                    ).show()

                    null -> Toast.makeText(
                        this,
                        "Error de conexión",
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> Toast.makeText(
                        this,
                        "Error del servidor: $errorCode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    // Validar registro
    private fun performRegister() {
        val username = registerUsername.text.toString().trim()
        val mail = registerEmail.text.toString().trim()
        val password = registerPassword.text.toString().trim()
        val confirmPassword = registerConfirmPassword.text.toString().trim()

        when (RegisterValidator.validate(username, mail, password, confirmPassword)) {

            RegisterValidationResult.EmptyUsername -> {
                registerUsername.error = "Introduce un username"
                registerUsername.requestFocus()
                return
            }

            RegisterValidationResult.LongUsername -> {
                registerUsername.error = "Máximo ${UsernameValidator.MAX_LENGTH} caracteres"
                registerUsername.requestFocus()
                Toast.makeText(
                    this,
                    "El username debe tener máximo ${UsernameValidator.MAX_LENGTH} caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            RegisterValidationResult.InvalidUsername -> {
                registerUsername.error = "El username no puede contener espacios"
                registerUsername.requestFocus()
                return
            }

            RegisterValidationResult.EmptyMail -> {
                registerEmail.error = "Introduce un e-mail"
                registerEmail.requestFocus()
                return
            }

            RegisterValidationResult.InvalidMail -> {
                registerEmail.error = "Email inválido"
                registerEmail.requestFocus()
                return
            }

            RegisterValidationResult.EmptyPassword -> {
                registerPassword.error = "Introduce una contraseña"
                registerPassword.requestFocus()
                return
            }

            RegisterValidationResult.ShortPassword -> {
                registerPassword.error = "Mínimo 6 caracteres"
                registerPassword.requestFocus()
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            RegisterValidationResult.LongPassword -> {
                registerPassword.error = "Máximo 50 caracteres"
                registerPassword.requestFocus()
                Toast.makeText(
                    this,
                    "La contraseña debe tener máximo 50 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            RegisterValidationResult.EmptyConfirmPassword -> {
                registerConfirmPassword.error = "Confirma tu contraseña"
                registerConfirmPassword.requestFocus()
                return
            }

            RegisterValidationResult.PasswordsMismatch -> {
                registerConfirmPassword.error = "No coinciden las contraseñas"
                registerConfirmPassword.requestFocus()
                return
            }

            RegisterValidationResult.Valid -> Unit

        }

        // Deshabilitar botón mientras se procesa
        registerButton.isEnabled = false
        registerButton.text = "Registrando..."

        val request = RegisterRequest(username, mail, password)

        authRepository.register(request = request, onSuccess = { account ->
            restoreRegisterButton()

            TokenManager.saveUserId(account.accountId)

            Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()

            showLogin()
            clearRegisterForm()
            loginCredential.setText(mail)
        },
        onError = { errorCode ->
            restoreRegisterButton()

            when (errorCode) {
                409 -> Toast.makeText(
                    this,
                    "El usuario ya existe",
                    Toast.LENGTH_SHORT
                ).show()

                400 -> Toast.makeText(
                    this,
                    "Datos inválidos",
                    Toast.LENGTH_SHORT
                ).show()

                null -> Toast.makeText(
                    this,
                    "Error de conexión",
                    Toast.LENGTH_SHORT
                ).show()

                else -> Toast.makeText(
                    this,
                    "Error del servidor: $errorCode",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun restoreLoginButton() {
        loginButton.isEnabled = true
        loginButton.text = "Continuar"
    }

    private fun restoreRegisterButton() {
        registerButton.isEnabled = true
        registerButton.text = "Confirmar"
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
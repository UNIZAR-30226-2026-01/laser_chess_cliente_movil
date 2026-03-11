package com.gracehopper.laserchessapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.auth.AccountResponse
import com.gracehopper.laserchessapp.data.model.auth.LoginRequest
import com.gracehopper.laserchessapp.data.model.auth.LoginResponse
import com.gracehopper.laserchessapp.data.model.auth.RegisterRequest
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.AuthRepository
import com.gracehopper.laserchessapp.ui.main.MainActivity
import com.gracehopper.laserchessapp.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Actividad de inicio de sesión.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var loadingLayout: LinearLayout
    private lateinit var loginLayout: LinearLayout
    private lateinit var registerLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
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

    private val handler = Handler(Looper.getMainLooper())
    private var progress = 0
    private var paused = false

    private val loadingRunnable = object : Runnable {
        override fun run() {
            if (!paused && progress < 100) {
                progress += 1
                progressBar.progress = progress

                // Si 40% (2 segundos) pausar y popup
                if (progress == 40) {
                    pausarCargaYMostrarLogin()
                } else if (progress < 100) {
                    // prox actualizacion de la barra
                    handler.postDelayed(this, 50) // 50ms por paso = 5 segundos total
                } else {
                    goToMain()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)
        if (TokenManager.isLoggedIn()) {
            goToMain()
            return
        }

        setContentView(R.layout.activity_login)

        authRepository = AuthRepository(NetworkUtils.getApiService())

        initViews()
        initListeners()
        startLoading()
    }

    private fun initViews() {
        loadingLayout = findViewById(R.id.loadingLayout)
        loginLayout = findViewById(R.id.loginLayout)
        registerLayout = findViewById(R.id.registerLayout)
        progressBar = findViewById(R.id.progressBar)

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

    private fun startLoading() {
        handler.post(loadingRunnable)
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

    private fun pausarCargaYMostrarLogin() {
        paused = true //barra pausada
        // popup login
        loginLayout.visibility = View.VISIBLE
    }

    private fun performLogin() {
        val credential = loginCredential.text.toString().trim()
        val password = loginPassword.text.toString().trim()

        when {
            credential.isEmpty() -> {
                loginCredential.error = "Introduce un username/e-mail"
                loginCredential.requestFocus()
                return
            }

            password.isEmpty() -> {
                loginPassword.error = "Introduce una contraseña"
                loginPassword.requestFocus()
                return
            }

            password.length < 6 -> {
                registerPassword.error = "Mínimo 6 caracteres"
                registerPassword.requestFocus()
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        // Por si acaso apago el boton
        loginButton.isEnabled = false
        loginButton.text = "Iniciando sesión..."
        val request = LoginRequest(credential, password)

        authRepository.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                restoreLoginButton()

                if (response.isSuccessful) {

                    val loginResponse = response.body()
                    if (loginResponse == null) {
                        Toast.makeText(this@LoginActivity,
                            "Error de conexión", Toast.LENGTH_SHORT).show()
                        return
                    }

                    // Guardar el token
                    TokenManager.saveAccessToken(loginResponse.access_token)
                    TokenManager.saveUserCredential(credential)

                    Log.d("LoginActivity", "Token guardado: ${loginResponse.access_token}")

                    // Ocultar layouts y continuar
                    loginLayout.visibility = View.GONE
                    registerLayout.visibility = View.GONE

                    Toast.makeText(this@LoginActivity, "¡Bienvenid@!", Toast.LENGTH_SHORT).show()
                    resumeLoading()

                } else {

                    var errorCode = response.code()
                    when (errorCode) {
                        401 -> Toast.makeText(this@LoginActivity,
                            "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        400 -> Toast.makeText(this@LoginActivity,
                            "Datos inválidos", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@LoginActivity,
                            "Error del servidor: $errorCode", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                restoreLoginButton()
                Toast.makeText(this@LoginActivity,
                    "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Validar registro
    private fun performRegister() {
        val username = registerUsername.text.toString().trim()
        val mail = registerEmail.text.toString().trim()
        val password = registerPassword.text.toString().trim()
        val confirmPassword = registerConfirmPassword.text.toString().trim()

        when {
            username.isEmpty() -> {
                registerUsername.error = "Introduce un nombre de usuario"
                registerUsername.requestFocus()
                return
            }

            mail.isEmpty() -> {
                registerEmail.error = "Introduce un e-mail"
                registerEmail.requestFocus()
                return
            }

            !Patterns.EMAIL_ADDRESS.matcher(mail).matches() -> {
                registerEmail.error = "Email inválido"
                registerEmail.requestFocus()
                return
            }

            password.isEmpty() -> {
                registerPassword.error = "Introduce una contraseña"
                registerPassword.requestFocus()
                return
            }

            confirmPassword.isEmpty() -> {
                registerConfirmPassword.error = "Confirma tu contraseña"
                registerConfirmPassword.requestFocus()
                return
            }

            password.length < 6 -> {
                registerPassword.error = "Mínimo 6 caracteres"
                registerPassword.requestFocus()
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            password != confirmPassword -> {
                registerConfirmPassword.error = "No coinciden las contraseñas"
                registerConfirmPassword.requestFocus()
                return
            }

        }

        // Deshabilitar botón mientras se procesa
        registerButton.isEnabled = false
        registerButton.text = "Registrando..."

        val request = RegisterRequest(username, mail, password)

        authRepository.register(request).enqueue(object : Callback<AccountResponse> {
            override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                restoreRegisterButton()

                if (response.isSuccessful) {

                    val account = response.body()
                    if (account == null) {
                        Toast.makeText(this@LoginActivity,
                            "Error de conexión", Toast.LENGTH_SHORT).show()
                        return
                    }

                    TokenManager.saveUserId(account.account_id)  // Guardar ID
                    Toast.makeText(this@LoginActivity,
                        "¡Registro exitoso! ID: ${account.account_id}",
                        Toast.LENGTH_LONG).show()
                    showLogin()
                    clearRegisterForm()
                    loginCredential.setText(mail)

                } else {
                    when (response.code()) {
                        409 -> Toast.makeText(this@LoginActivity,
                            "El usuario o email ya existe", Toast.LENGTH_SHORT).show()
                        400 -> Toast.makeText(this@LoginActivity,
                            "Datos inválidos", Toast.LENGTH_SHORT).show()
                        401 -> Toast.makeText(this@LoginActivity,
                            "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@LoginActivity,
                            "Error ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                restoreRegisterButton()
                Log.e("LoginActivity", "Error de conexión en registro", t)
                Toast.makeText(this@LoginActivity,
                    "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
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

    private fun resumeLoading() {
        paused = false
        handler.post(loadingRunnable)
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpiar por si acaso
        handler.removeCallbacksAndMessages(null)
    }

}
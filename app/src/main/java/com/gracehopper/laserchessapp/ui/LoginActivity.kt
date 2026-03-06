package com.gracehopper.laserchessapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gracehopper.laserchessapp.ui.main.MainActivity
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.remote.ApiService
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.network.AccountResponse
import com.gracehopper.laserchessapp.network.LoginRequest
import com.gracehopper.laserchessapp.network.LoginResponse
import com.gracehopper.laserchessapp.network.RegisterRequest
import com.gracehopper.laserchessapp.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    //Pa el layout y el login
    private lateinit var layoutCarga: LinearLayout
    private lateinit var layoutLogin: LinearLayout    //Layout log
    private lateinit var layoutRegistro: LinearLayout //Layout reg
    private lateinit var barraP: ProgressBar
    private lateinit var email: EditText        //email Log
    private lateinit var contrasena: EditText   //contr log
    private lateinit var botonContinuar: Button //boton log

    //Pa los enlaces
    private lateinit var registroLink: TextView
    private lateinit var registroLink2: TextView
    //Pa el registro
    private lateinit var registroNombre: EditText
    private lateinit var registroEmail: EditText
    private lateinit var registroContr: EditText
    private lateinit var registroConfirmContr: EditText
    private lateinit var botonRegistroConfirmar: Button

    private var progreso = 0
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var pausado = false

    //Para la conexion
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        TokenManager.init(this)
        apiService = NetworkUtils.getApiService()
        checkSavedSession() //para comprobacion
        initViews()
        iniciarCarga()
    }

    private fun initViews() {
        layoutCarga = findViewById(R.id.layoutCarga)
        layoutLogin = findViewById(R.id.layoutLogin)
        layoutRegistro = findViewById(R.id.layoutRegistro)
        barraP = findViewById(R.id.progressBar)

        //Elemntos login
        email = findViewById(R.id.editTextEmail)
        contrasena = findViewById(R.id.editTextPassword)
        botonContinuar = findViewById(R.id.buttonContinue)

        registroLink = findViewById(R.id.textViewRegistroLink)
        registroLink2 = findViewById(R.id.textViewRegistroLink2)

        //Elementos del registro
        registroNombre = findViewById(R.id.editTextRegistroNombre)
        registroEmail = findViewById(R.id.editTextRegistroEmail)
        registroContr = findViewById(R.id.editTextRegistroPassword)
        registroConfirmContr = findViewById(R.id.editTextRegistroConfirmPassword)
        botonRegistroConfirmar = findViewById(R.id.buttonRegistroConfirmar)

        // Configurar listeners
        botonContinuar.setOnClickListener {
            validarYContinuar()
        }

        //Listeners para cambiar entre pop-ups
        registroLink.setOnClickListener {
            mostrarRegistro()
        }

        registroLink2.setOnClickListener {
            mostrarLogin()
        }

        //Listener para el botón de registro
        botonRegistroConfirmar.setOnClickListener {
            validarRegistro()
        }
    }

    // pop-up de registro
    private fun mostrarRegistro() {
        // Ocultar login, mostrar registro
        layoutLogin.visibility = View.GONE
        layoutRegistro.visibility = View.VISIBLE

        //Limpiar por si acaso
        limpiarCamposRegistro()
    }

    // pop-up de login
    private fun mostrarLogin() {
        // Ocultar registro, mostrar login
        layoutRegistro.visibility = View.GONE
        layoutLogin.visibility = View.VISIBLE

        // Limpiar por si acaso
        limpiarCamposLogin()
    }

    // Limpiar
    private fun limpiarCamposRegistro() {
        registroNombre.text?.clear()
        registroEmail.text?.clear()
        registroContr.text?.clear()
        registroConfirmContr.text?.clear()
    }

    // Limpiar
    private fun limpiarCamposLogin() {
        email.text?.clear()
        contrasena.text?.clear()
    }

    private fun iniciarCarga() {
        runnable = object : Runnable {
            override fun run() {
                if (!pausado && progreso < 100) {
                    progreso += 1
                    handler.post {
                        barraP.progress = progreso
                    }

                    // Si 40% (2 segundos) pausar y popup
                    if (progreso == 40) {
                        pausarCargaYMostrarLogin()
                    } else {
                        // prox actualizacion de la barra
                        handler.postDelayed(this, 50) // 50ms por paso = 5 segundos total
                    }
                }
            }
        }

        // Iniciar
        handler.post(runnable!!)
    }

    private fun pausarCargaYMostrarLogin() {
        pausado = true //barra pausada
        // popup login
        layoutLogin.visibility = View.VISIBLE
    }

    private fun validarYContinuar() {
        val credential = email.text.toString().trim()
        val password = contrasena.text.toString().trim()

        if (credential.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Por si acaso apago el boton
        botonContinuar.isEnabled = false
        botonContinuar.text = "Iniciando sesión..."
        val request = LoginRequest(credential, password)

        apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                botonContinuar.isEnabled = true
                botonContinuar.text = "Continuar"

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        // Guardar el token
                        TokenManager.saveAccessToken(it.access_token)
                        TokenManager.saveUserCredential(credential)

                        Log.d("LoginActivity", "Token guardado: ${it.access_token}")

                        // Ocultar layouts y continuar
                        layoutLogin.visibility = View.GONE
                        layoutRegistro.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                        reanudarCarga()
                    }
                } else {
                    var codigo = response.code()
                    when (codigo) {
                        401 -> Toast.makeText(this@LoginActivity,
                            "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        400 -> Toast.makeText(this@LoginActivity,
                            "Datos inválidos", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@LoginActivity,
                            "Error del servidor: $codigo", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                botonContinuar.isEnabled = true
                botonContinuar.text = "Continuar"
                Toast.makeText(this@LoginActivity,
                    "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }




    // Validar registro
    private fun validarRegistro() {
        val username = registroNombre.text.toString().trim()
        val mail = registroEmail.text.toString().trim()
        val password = registroContr.text.toString().trim()
        val confirmPassword = registroConfirmContr.text.toString().trim()

        when {
            username.isEmpty() -> {
                registroNombre.error = "Falta el nombre"
                Toast.makeText(this, "Falta el nombre", Toast.LENGTH_SHORT).show()
            }
            mail.isEmpty() -> {
                registroEmail.error = "Falta el correo"
                Toast.makeText(this, "Falta el correo electrónico", Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
                registroContr.error = "Falta la contraseña"
                Toast.makeText(this, "Falta la contraseña", Toast.LENGTH_SHORT).show()
            }
            password.length < 6 -> {
                registroContr.error = "Mínimo 6 caracteres"
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            }
            password != confirmPassword -> {
                registroConfirmContr.error = "No coinciden las contraseñas"
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Deshabilitar botón mientras se procesa
                botonRegistroConfirmar.isEnabled = false
                botonRegistroConfirmar.text = "Registrando..."

                val request = RegisterRequest(username, mail, password)

                apiService.register(request).enqueue(object : Callback<AccountResponse> {
                    override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                        botonRegistroConfirmar.isEnabled = true
                        botonRegistroConfirmar.text = "Confirmar"

                        if (response.isSuccessful) {
                            val account = response.body()
                            account?.let {
                                TokenManager.saveUserId(it.account_id)  // Guardar ID
                                Toast.makeText(this@LoginActivity,
                                    "Registro exitoso! ID: ${it.account_id}",
                                    Toast.LENGTH_LONG).show()
                                mostrarLogin()
                                limpiarCamposRegistro()
                            }
                        } else {
                            when (response.code()) {
                                409 -> Toast.makeText(this@LoginActivity,
                                    "El usuario o email ya existe", Toast.LENGTH_SHORT).show()
                                400 -> Toast.makeText(this@LoginActivity,
                                    "Datos inválidos", Toast.LENGTH_SHORT).show()
                                else -> Toast.makeText(this@LoginActivity,
                                    "Error ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                        botonRegistroConfirmar.isEnabled = true
                        botonRegistroConfirmar.text = "Confirmar"
                        Log.e("LoginActivity", "Error de conexión en registro", t)
                        Toast.makeText(this@LoginActivity,
                            "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }

    private fun reanudarCarga() {
        pausado = false

        // 40%
        if (progreso < 100) {
            handler.post(object : Runnable {
                override fun run() {
                    if (!pausado && progreso < 100) {
                        progreso += 1
                        barraP.progress = progreso

                        if (progreso < 100) {
                            handler.postDelayed(this, 50)
                        } else {
                            irAMain()
                        }
                    }
                }
            })
        } else {
            irAMain()
        }
    }

    private fun irAMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpiar por si acaso
        handler.removeCallbacksAndMessages(null)
    }


    private fun checkSavedSession() {
        if (TokenManager.isLoggedIn()) {
            irAMain()
        }
    }


}
package com.example.laserchessapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    private lateinit var layoutCarga: LinearLayout
    private lateinit var layoutLogin: LinearLayout    //Layout log
    private lateinit var layoutRegistro: LinearLayout //Layout reg
    private lateinit var barraP: ProgressBar
    private lateinit var email: EditText        //email Log
    private lateinit var contrasena: EditText   //contr log
    private lateinit var botonContinuar: Button //boton log

    //Pa los enlaces
    private lateinit var RegistroLink: TextView
    private lateinit var RegistroLink2: TextView

    private lateinit var RegistroNombre: EditText
    private lateinit var RegistroEmail: EditText
    private lateinit var RegistroContr: EditText
    private lateinit var RegistroConfirmContr: EditText
    private lateinit var botonRegistroConfirmar: Button

    private var progreso = 0
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var pausado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        iniciarCarga()
    }

    private fun initViews() {
        layoutCarga = findViewById(R.id.layoutCarga)
        layoutLogin = findViewById(R.id.layoutLogin)
        layoutRegistro = findViewById(R.id.layoutRegistro)

        barraP = findViewById(R.id.progressBar)
        email = findViewById(R.id.editTextEmail)
        contrasena = findViewById(R.id.editTextPassword)
        botonContinuar = findViewById(R.id.buttonContinue)


        RegistroLink = findViewById(R.id.textViewRegistroLink)
        RegistroLink2 = findViewById(R.id.textViewRegistroLink2)

        //Elementos del registro
        RegistroNombre = findViewById(R.id.editTextRegistroNombre)
        RegistroEmail = findViewById(R.id.editTextRegistroEmail)
        RegistroContr = findViewById(R.id.editTextRegistroPassword)
        RegistroConfirmContr = findViewById(R.id.editTextRegistroConfirmPassword)
        botonRegistroConfirmar = findViewById(R.id.buttonRegistroConfirmar)

        // Configurar listeners
        botonContinuar.setOnClickListener {
            validarYContinuar()
        }

        //Listeners para cambiar entre pop-ups
        RegistroLink.setOnClickListener {
            mostrarRegistro()
        }

        RegistroLink2.setOnClickListener {
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
        layoutLogin.visibility = android.view.View.GONE
        layoutRegistro.visibility = android.view.View.VISIBLE

        //Limpiar por si acaso
        limpiarCamposRegistro()
    }

    // pop-up de login
    private fun mostrarLogin() {
        // Ocultar registro, mostrar login
        layoutRegistro.visibility = android.view.View.GONE
        layoutLogin.visibility = android.view.View.VISIBLE

        // Limpiar por si acaso
        limpiarCamposLogin()
    }

    // Limpiar
    private fun limpiarCamposRegistro() {
        RegistroNombre.text?.clear()
        RegistroEmail.text?.clear()
        RegistroContr.text?.clear()
        RegistroConfirmContr.text?.clear()
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
        layoutLogin.visibility = android.view.View.VISIBLE
    }

    private fun validarYContinuar() {
        val emailTexto = email.text.toString()
        val contrasenaTexto = contrasena.text.toString()

        if (emailTexto.isNotEmpty() && contrasenaTexto.isNotEmpty()) {
            // po pup oculto
            layoutLogin.visibility = android.view.View.GONE
            layoutRegistro.visibility = android.view.View.GONE
            Toast.makeText(this, "Se ha iniciado sesión", Toast.LENGTH_SHORT).show()

            // Reanudar
            reanudarCarga()
        } else {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    // Validar registro
    private fun validarRegistro() {
        val nombre = RegistroNombre.text.toString().trim()
        val email = RegistroEmail.text.toString().trim()
        val password = RegistroContr.text.toString().trim()
        val confirmPassword = RegistroConfirmContr.text.toString().trim()

        when {
            nombre.isEmpty() -> {
                RegistroNombre.error = "Falta el nombre"
                Toast.makeText(this, "Falta el nombre", Toast.LENGTH_SHORT).show()
            }
            email.isEmpty() -> {
                RegistroEmail.error = "Falta el correo"
                Toast.makeText(this, "Falta el correo electrónico", Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
                RegistroContr.error = "Falta la contraseña"
                Toast.makeText(this, "Falta la contraseña", Toast.LENGTH_SHORT).show()
            }
            password.length < 6 -> {
                RegistroContr.error = "Mínimo 6 caracteres"
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            }
            password != confirmPassword -> {
                RegistroConfirmContr.error = "No coinciden las contraseñas"
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
            else -> {
                layoutLogin.visibility = android.view.View.GONE    //con esto se va Login
                layoutRegistro.visibility = android.view.View.GONE
                Toast.makeText(this, "Se ha registrado la sesión", Toast.LENGTH_SHORT).show()
                limpiarCamposRegistro()
                // Reanudar
                reanudarCarga()
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
                            irAHome()
                        }
                    }
                }
            })
        } else {
            irAHome()
        }
    }

    private fun irAHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpiar por si acaso
        handler.removeCallbacksAndMessages(null)
    }
}
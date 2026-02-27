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

class LoginActivity : AppCompatActivity() {

    private lateinit var layoutCarga: LinearLayout
    private lateinit var layoutLogin: LinearLayout
    private lateinit var barraP: ProgressBar
    private lateinit var email: EditText
    private lateinit var contrasena: EditText
    private lateinit var botonContinuar: Button

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
        barraP = findViewById(R.id.progressBar)
        email = findViewById(R.id.editTextEmail)
        contrasena = findViewById(R.id.editTextPassword)
        botonContinuar = findViewById(R.id.buttonContinue)

        botonContinuar.setOnClickListener {
            validarYContinuar()
        }
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
            // popup oculto
            layoutLogin.visibility = android.view.View.GONE
            Toast.makeText(this, "Se ha iniciado sesión", Toast.LENGTH_SHORT).show()

            // Reanudar
            reanudarCarga()
        } else {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
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
}
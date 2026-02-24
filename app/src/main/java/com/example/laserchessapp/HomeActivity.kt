package com.example.laserchessapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val btnTiendaActivity = findViewById<ImageButton>(R.id.btnTienda)
        val btnPersoActivity = findViewById<ImageButton>(R.id.btnPerso)
        val btnSocialActivity = findViewById<ImageButton>(R.id.btnSocial)
        val btnClasifActivity = findViewById<ImageButton>(R.id.btnClasif)
        btnTiendaActivity.setOnClickListener { navigateToTienda() }
        btnPersoActivity.setOnClickListener { navigateToPerso() }
        btnSocialActivity.setOnClickListener { navigateToSocial() }
        btnClasifActivity.setOnClickListener { navigateToClasif() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun navigateToTienda(){
        val intent = Intent(this, TiendaActivity::class.java)
        startActivity(intent)
    }

    fun navigateToPerso(){
        val intent = Intent(this, PersoActivity::class.java)
        startActivity(intent)
    }

    fun navigateToSocial(){
        val intent = Intent(this, SocialActivity::class.java)
        startActivity(intent)
    }

    fun navigateToClasif(){
        val intent = Intent(this, ClasifActivity::class.java)
        startActivity(intent)
    }
}
package com.example.laserchessapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TiendaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tienda)

        val btnClasifActivity = findViewById<ImageButton>(R.id.btnClasif)
        val btnPersoActivity = findViewById<ImageButton>(R.id.btnPerso)
        val btnSocialActivity = findViewById<ImageButton>(R.id.btnSocial)
        val btnHomeActivity = findViewById<ImageButton>(R.id.btnHome)
        btnClasifActivity.setOnClickListener { navigateToClasif() }
        btnPersoActivity.setOnClickListener { navigateToPerso() }
        btnSocialActivity.setOnClickListener { navigateToSocial() }
        btnHomeActivity.setOnClickListener { navigateToHome() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun navigateToClasif(){
        val intent = Intent(this, ClasifActivity::class.java)
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

    fun navigateToHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}
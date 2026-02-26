package com.example.laserchessapp

import android.content.Intent
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    protected fun setupBarraNav(selectedButtonId: Int) {

        val btnTienda = findViewById<ImageButton>(R.id.btnTienda)
        val btnPerso = findViewById<ImageButton>(R.id.btnPerso)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        val btnSocial = findViewById<ImageButton>(R.id.btnSocial)
        val btnClasif = findViewById<ImageButton>(R.id.btnClasif)

        val navBtns = listOf(btnTienda,btnPerso,btnHome,btnSocial,btnClasif)

        // Valor original Desseleccionado
        navBtns.forEach{
            it.isSelected = false
        }
        // Seleccionar boton actual (si existe)
        findViewById<ImageButton>(selectedButtonId)?.isSelected = true

        // Asociamos cada boton a una activity (Para no replicar el mismo codigo)
        val navMap = mapOf(
            R.id.btnTienda to TiendaActivity::class.java,
            R.id.btnPerso to PersoActivity::class.java,
            R.id.btnHome to HomeActivity::class.java,
            R.id.btnSocial to SocialActivity::class.java,
            R.id.btnClasif to ClasifActivity::class.java
        )

        // Para cada boton/activity, el listener
        navMap.forEach { (buttonId, activityClass) ->
            findViewById<ImageButton>(buttonId).setOnClickListener {

                if (selectedButtonId != buttonId) {
                    startActivity(Intent(this, activityClass))
                }
            }
        }
    }

}
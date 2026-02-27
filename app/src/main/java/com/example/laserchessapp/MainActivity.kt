package com.example.laserchessapp

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity(){

    private lateinit var viewPager2: ViewPager2
    private lateinit var navBtns: List<ImageButton>

    private val cambioPaginaCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            actualizarBotones(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        viewPager2 = findViewById(R.id.viewPager2)
        viewPager2.adapter = ViewPagerAdapter(this)

        // registramos el callback de cambiar de pagina
        viewPager2.registerOnPageChangeCallback(cambioPaginaCallback)

        setupBarraNav()

        // Home por defecto
        viewPager2.setCurrentItem(2, false)
        actualizarBotones(2)
    }

    private fun setupBarraNav() {

        navBtns = listOf(
            findViewById(R.id.btnTienda),
            findViewById(R.id.btnPerso),
            findViewById(R.id.btnHome),
            findViewById(R.id.btnSocial),
            findViewById(R.id.btnClasif)
        )

        navBtns.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewPager2.currentItem = index
            }
        }
    }

    private fun actualizarBotones(index: Int) {
        navBtns.forEach {
            it.isSelected = false }
        navBtns[index].isSelected = true
    }

    // Destructor
    override fun onDestroy(){
        super.onDestroy()
        viewPager2.unregisterOnPageChangeCallback(cambioPaginaCallback)
    }

}
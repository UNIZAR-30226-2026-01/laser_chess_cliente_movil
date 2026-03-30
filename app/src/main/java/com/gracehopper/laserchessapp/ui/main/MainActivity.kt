package com.gracehopper.laserchessapp.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.utils.ChallengeNotificationHelper

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var navBtns: List<ImageButton>

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            granted ->
                Toast.makeText(this,
                    "Permiso de notificaciones ${if (granted) "concedido" else "denegado"}",
                    Toast.LENGTH_SHORT).show()
        }

    private val cambioPaginaCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            actualizarBotones(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // crear canal al arrancar la app
        ChallengeNotificationHelper.createChannels(this)
        requestNotificationPermissionIfNeeded()

        viewPager2 = findViewById(R.id.viewPager2)
        viewPager2.adapter = ViewPagerAdapter(this)

        // registramos el callback de cambiar de pagina
        viewPager2.registerOnPageChangeCallback(cambioPaginaCallback)

        setupBarraNav()

        // Home por defecto
        viewPager2.setCurrentItem(2, false)
        actualizarBotones(2)
    }

    /**
     * Método que solicita el permiso de notificaciones si es necesario
     */
    private fun requestNotificationPermissionIfNeeded() {

        // si la version de android es menor a TIRAMISU(13) no es necesario
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        // si ya se ha concedido el permiso no es necesario solicitarlo
        val alreadyGranted = ContextCompat.checkSelfPermission(this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!alreadyGranted) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

    }

    private fun setupBarraNav() {

        navBtns = listOf(
            findViewById(R.id.btnShop),
            findViewById(R.id.btnCustomize),
            findViewById(R.id.btnHome),
            findViewById(R.id.btnSocial),
            findViewById(R.id.btnRanking)
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
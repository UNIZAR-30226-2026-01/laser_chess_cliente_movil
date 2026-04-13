package com.gracehopper.laserchessapp.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.model.user.MyProfile
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.UserRepository
import com.gracehopper.laserchessapp.ui.notifications.NotificationsDialogFragment
import com.gracehopper.laserchessapp.ui.user.MyProfileDialogFragment
import com.gracehopper.laserchessapp.ui.utils.AvatarUtils
import com.gracehopper.laserchessapp.utils.ChallengeNotificationHelper

/**
 * Activity principal de la aplicación.
 *
 * Se encarga de:
 * - Gestionar la navegación entre pantallas mediante ViewPager
 * - Mostrar información del perfil del usuario
 * - Gestionar notificaciones
 * - Inicializar datos del usuario
 */
class MainActivity : AppCompatActivity() {

    /**
     * Repositorio para obtener datos del usuario desde el backend
     */
    private val repository by lazy {
        UserRepository(NetworkUtils.getApiService())
    }

    private lateinit var viewPager2: ViewPager2
    private lateinit var navBtns: List<ImageButton>

    // Elementos del perfil
    private lateinit var imgProfileAvatar: ImageView
    private lateinit var txtProfileUsername: TextView
    private lateinit var txtProfileLevel: TextView
    private lateinit var txtProfileXp: TextView
    private lateinit var progressProfileXP: ProgressBar
    private lateinit var profileCardContainer: View


    /**
     * Launcher para solicitar permiso de notificaciones
     */
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Toast.makeText(
                this,
                "Permiso de notificaciones ${if (granted) "concedido" else "denegado"}",
                Toast.LENGTH_SHORT
            ).show()
        }

    /**
     * Callback al cambiar de página en el ViewPager
     */
    private val cambioPaginaCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            updateButtonSelection(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        /**
         * Pantalla completa
         */
        val controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
        controller.hide(WindowInsetsCompat.Type.systemBars())

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
        updateButtonSelection(2)

        handleNotificationIntent(intent)

        initViews()
        observeCurrentUserProfile()
        isMyProfileLoaded()
        setupProfileCard()

    }

    /**
     * Método que solicita el permiso de notificaciones si es necesario
     */
    private fun requestNotificationPermissionIfNeeded() {

        // si la version de android es menor a TIRAMISU(13) no es necesario
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        // si ya se ha concedido el permiso no es necesario solicitarlo
        val alreadyGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!alreadyGranted) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

    }

    /**
     * Configura la barra de navegación inferior
     */
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

    /**
     * Actualiza el botón seleccionado en la barra de navegación
     */
    private fun updateButtonSelection(index: Int) {
        navBtns.forEach {
            it.isSelected = false
        }
        navBtns[index].isSelected = true
    }

    /**
     * Maneja intent de notificaciones (abrir diálogo)
     */
    private fun handleNotificationIntent(intent: Intent?) {

        val openNotifications =
            intent?.getBooleanExtra("open_notifications_dialog", false) == true

        if (openNotifications) {
            openNotificationsDialog()
            intent?.removeExtra("open_notifications_dialog")
        }

    }

    private fun openNotificationsDialog() {
        val existing = supportFragmentManager.findFragmentByTag("NotificationsDialog")
        if (existing != null) return

        NotificationsDialogFragment().show(supportFragmentManager, "NotificationsDialog")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    /**
     * Inicializa las vistas del perfil
     */
    private fun initViews() {

        imgProfileAvatar = findViewById(R.id.imgMyProfileAvatar)
        txtProfileUsername = findViewById(R.id.txtMyProfileUsername)
        txtProfileLevel = findViewById(R.id.txtMyProfileLevel)
        txtProfileXp = findViewById(R.id.txtMyProfileXp)
        progressProfileXP = findViewById(R.id.progressMyProfileXp)
        profileCardContainer = findViewById(R.id.profileCardInclude)

    }

    /**
     * Observa cambios en el perfil del usuario
     */
    private fun observeCurrentUserProfile() {

        CurrentUserManager.myProfile.observe(this) { profile ->
            if (profile != null) {
                updateProfileCard(profile)
            }
        }

    }

    /**
     * Carga el perfil si no está en memoria
     */
    private fun isMyProfileLoaded() {

        if (CurrentUserManager.isProfileLoaded()) return

        repository.getMyProfile(
            onSuccess = { profile ->
                CurrentUserManager.setMyProfile(profile)
            },
            onError = {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "No se pudo cargar tu perfil",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    }

    /**
     * Actualiza la UI del perfil
     */
    private fun updateProfileCard(profile: MyProfile) {

        txtProfileUsername.text = profile.username
        txtProfileLevel.text = "Nivel ${profile.level}"
        txtProfileXp.text = "${profile.xp} xp"
        imgProfileAvatar.setImageResource(AvatarUtils.getAvatarDrawable(profile.avatar))
        progressProfileXP.max = 100
        progressProfileXP.progress = profile.xp % 100

    }

    /**
     * Configura el click del perfil
     */
    private fun setupProfileCard() {

        profileCardContainer.setOnClickListener {

            val dialog = MyProfileDialogFragment.newInstance()
            dialog.show(supportFragmentManager, "MyProfileDialog")

        }

    }

    private fun refreshMyProfile() {

        repository.getMyProfile(
            onSuccess = { profile ->
                CurrentUserManager.setMyProfile(profile)
            },
            onError = {
                Toast.makeText(
                    this,
                    "No se puedo actualizar tu perfil",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

    }

    fun openCustomizeFragment() {
        viewPager2.currentItem = 1
    }

    // Destructor
    override fun onDestroy() {
        super.onDestroy()
        viewPager2.unregisterOnPageChangeCallback(cambioPaginaCallback)
    }

}
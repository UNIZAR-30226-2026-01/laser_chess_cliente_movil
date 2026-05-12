package com.gracehopper.laserchessapp.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.manager.SseManager
import com.gracehopper.laserchessapp.data.model.game.GameEvent
import com.gracehopper.laserchessapp.data.model.user.MyProfile
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.EventStatusRepository
import com.gracehopper.laserchessapp.data.repository.UserRepository
import com.gracehopper.laserchessapp.data.repository.ChallengeRepository
import com.gracehopper.laserchessapp.ui.settings.SettingsDialogFragment
import com.gracehopper.laserchessapp.ui.game.GameActivity
import com.gracehopper.laserchessapp.ui.history.HistoryDialogFragment
import com.gracehopper.laserchessapp.ui.notifications.NotificationsDialogFragment
import com.gracehopper.laserchessapp.ui.social.RequestsDialogFragment
import com.gracehopper.laserchessapp.ui.user.MyProfileDialogFragment
import com.gracehopper.laserchessapp.ui.utils.BackgroundUtils
import com.gracehopper.laserchessapp.ui.utils.ItemUtils
import com.gracehopper.laserchessapp.utils.AppEvents
import com.gracehopper.laserchessapp.utils.AppNotificationHelper
import com.gracehopper.laserchessapp.utils.NotificationPreferences
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

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

    private val eventStatusRepository by lazy {
        EventStatusRepository(NetworkUtils.getApiService())
    }

    /**
     * Repositorio para gestionar retos
     */
    private val challengeRepository by lazy {
        ChallengeRepository(NetworkUtils.getApiService())
    }

    /**
     * Manager de eventos SSE (Server-Sent Events)
     */
    private val sseManager = SseManager(
        onChallengeReceived = { challengerUsername ->
            runOnUiThread {
                AppEvents.challengesUpdated.tryEmit(Unit)

                if (NotificationPreferences.isEnabled(applicationContext)) {
                    AppNotificationHelper.showChallengeNotification(
                        applicationContext,
                        challengerUsername
                    )
                }
            }
        },
        onFriendRequestReceived = { requestUsername ->
            runOnUiThread {
                AppEvents.friendRequestReceived.tryEmit(Unit)

                if (NotificationPreferences.isEnabled(applicationContext)) {
                    AppNotificationHelper.showFriendRequestNotification(
                        applicationContext,
                        requestUsername
                    )
                }
            }
        },
        onNewFriendshipReceived = { newFriendUsername ->
            runOnUiThread {
                AppEvents.newFriendshipReceived.tryEmit(Unit)

                if (NotificationPreferences.isEnabled(applicationContext)) {
                    AppNotificationHelper.showNewFriendshipNotification(
                        applicationContext,
                        newFriendUsername
                    )
                }
            }
        },
        onChallengesUpdated = {
            runOnUiThread {
                AppEvents.challengesUpdated.tryEmit(Unit)
            }
        },
        onError = {
            // error en SSE
        }
    )

    private lateinit var viewPager2: ViewPager2
    private lateinit var navButtons: List<ImageButton>
    private lateinit var navBars: List<View>

    // Elementos del perfil
    private lateinit var imgProfileAvatar: ImageView
    private lateinit var txtProfileUsername: TextView
    private lateinit var txtProfileLevel: TextView
    private lateinit var progressProfileXP: ProgressBar
    private lateinit var profileCardContainer: View
    private lateinit var txtNumCoins: TextView

    // Botones extras al lado de perfil
    private lateinit var btnSettings: ImageButton
    private lateinit var btnNotifications: ImageButton
    private lateinit var btnHistory: ImageButton
    private lateinit var txtBadgePendingChallenges: TextView

    private lateinit var imgBackground: ImageView

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
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            updateButtonSelection(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Pantalla completa
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // crear canal al arrancar la app
        AppNotificationHelper.createChannels(this)
        requestNotificationPermissionIfNeeded()

        viewPager2 = findViewById(R.id.viewPager2)
        viewPager2.adapter = ViewPagerAdapter(this)

        // registramos el callback de cambiar de página
        viewPager2.registerOnPageChangeCallback(pageChangeCallback)

        setupBottomNavigation()

        // Home por defecto
        viewPager2.setCurrentItem(2, false)
        updateButtonSelection(2)

        initViews()
        setupAdditionalButtons()

        observeChallengeBadge()
        loadChallengeBadge()

        observeCurrentUserProfile()
        loadMyProfileIfNeeded()
        setupProfileCard()

        handleNotificationIntent(intent)
    }

    private fun setupAdditionalButtons() {
        btnSettings = findViewById(R.id.btnSettings)
        btnNotifications = findViewById(R.id.btnNotifications)
        btnHistory = findViewById(R.id.btnHistory)
        txtBadgePendingChallenges = findViewById(R.id.txtBadgePendingChallenges)

        btnSettings.setOnClickListener {
            val dialog = SettingsDialogFragment()
            dialog.show(supportFragmentManager, "SettingsDialog")
        }

        btnNotifications.setOnClickListener {
            val dialog = NotificationsDialogFragment()
            dialog.show(supportFragmentManager, "NotificationsDialog")
        }

        btnHistory.setOnClickListener {
            val dialog = HistoryDialogFragment()
            dialog.show(supportFragmentManager, "HistoryDialog")
        }

    }

    override fun onStart() {
        super.onStart()

        Log.d("MAIN_ACTIVITY", "onStart -> online + connect SSE")
        eventStatusRepository.markOnline()

        if (NotificationPreferences.isEnabled(applicationContext)) {
            sseManager.reconnect()
        }

        loadChallengeBadge()

        if (ActiveGameManager.currentState == ActiveGameManager.GameState.INACTIVE) {
            setupGameReconnection()
        }

    }

    override fun onStop() {
        super.onStop()


        Log.d("MAIN_ACTIVITY", "onStop -> offline + disconnect SSE")

        eventStatusRepository.markOffline()
        sseManager.disconnect()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        Log.d("MAIN_ACTIVITY", "onNewIntent notification=${intent.getStringExtra("notification_type")}")
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    // Destructor
    override fun onDestroy() {
        super.onDestroy()
        viewPager2.unregisterOnPageChangeCallback(pageChangeCallback)

        Log.d("MAIN_ACTIVITY", "onDestroy -> disconnect SSE")
        sseManager.disconnect()
    }

    /**
     * Solicita el permiso de notificaciones si es necesario
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
    private fun setupBottomNavigation() {

        navButtons = listOf(
            findViewById(R.id.btnShop),
            findViewById(R.id.btnCustomize),
            findViewById(R.id.btnHome),
            findViewById(R.id.btnSocial),
            findViewById(R.id.btnRanking)
        )

        navBars = listOf(
            findViewById(R.id.barShop),
            findViewById(R.id.barCustomize),
            findViewById(R.id.barHome),
            findViewById(R.id.barSocial),
            findViewById(R.id.barRanking)
        )

        navButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewPager2.currentItem = index
            }
        }
    }

    /**
     * Actualiza el botón seleccionado en la barra de navegación
     */
    private fun updateButtonSelection(index: Int) {
        navButtons.forEach {
            it.isSelected = false
        }
        navBars.forEach {
            it.visibility = View.INVISIBLE
        }
        navButtons[index].isSelected = true
        navBars[index].visibility = View.VISIBLE

    }

    /**
     * Maneja intent de notificaciones (abrir diálogo)
     */
    private fun handleNotificationIntent(intent: Intent?) {

        when (intent?.getStringExtra("notification_type")) {
            "challenge" -> {
                openNotificationsDialog()
                intent.removeExtra("notification_type")
            }

            "friend_request" -> {
                openRequestsDialog()
                intent.removeExtra("notification_type")
            }

            "new_friendship" -> {
                openSocialFragment()
                intent.removeExtra("notification_type")
            }
        }

    }

    /**
     * Abre el diálogo de notificaciones
     */
    private fun openNotificationsDialog() {
        val existing = supportFragmentManager.findFragmentByTag("NotificationsDialog")
        if (existing != null) return

        NotificationsDialogFragment().show(supportFragmentManager, "NotificationsDialog")
    }

    /**
     * Abre el diálogo de solicitudes de amistad
     */
    private fun openRequestsDialog() {
        val existing = supportFragmentManager.findFragmentByTag("RequestsDialog")
        if (existing != null) return

        RequestsDialogFragment().show(supportFragmentManager, "RequestsDialog")
    }

    private fun observeChallengeBadge() {
        lifecycleScope.launch {
            AppEvents.challengesUpdated.collect {
                loadChallengeBadge()
            }
        }
    }

    private fun loadChallengeBadge() {
        challengeRepository.getChallengeCount(
            onSuccess = { count ->
                runOnUiThread {
                    if (count > 0) {
                        txtBadgePendingChallenges.text =
                            if (count > 99) "99+" else count.toString()

                        txtBadgePendingChallenges.visibility = View.VISIBLE
                    } else {
                        txtBadgePendingChallenges.text = ""
                        txtBadgePendingChallenges.visibility = View.GONE
                    }
                }
            },
            onError = {
                txtBadgePendingChallenges.text = ""
                txtBadgePendingChallenges.visibility = View.GONE
            }
        )
    }

    /**
     * Abre el fragmento de social
     */
    private fun openSocialFragment() {
        viewPager2.currentItem = 3
    }

    /**
     * Inicializa las vistas del perfil
     */
    private fun initViews() {

        imgProfileAvatar = findViewById(R.id.imgMyProfileAvatar)
        txtProfileUsername = findViewById(R.id.txtMyProfileUsername)
        txtProfileLevel = findViewById(R.id.txtMyProfileLevel)
        progressProfileXP = findViewById(R.id.progressMyProfileXp)
        profileCardContainer = findViewById(R.id.profileCardInclude)
        txtNumCoins = findViewById(R.id.numCoins)
        imgBackground = findViewById(R.id.img_background_animated)

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
    private fun loadMyProfileIfNeeded() {

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
        txtProfileLevel.text = getString(R.string.profile_card_level_format,
            profile.level, profile.xpLevel, profile.xpRequired)
        imgProfileAvatar.setImageResource(ItemUtils.getItemDrawable(profile.avatar))
        progressProfileXP.max = profile.xpRequired
        progressProfileXP.progress = profile.xpLevel

        txtNumCoins.text = profile.money.toString()

        BackgroundUtils.setupBackground(imgBackground, profile.boardSkin)

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

    fun openCustomizeFragment() {
        viewPager2.currentItem = 1
    }

    fun disconnectSse() {
        sseManager.disconnect()
    }

    private fun setupGameReconnection() {

        ActiveGameManager.setCallbacks(

            onMessageReceived = { event ->

                if (event is GameEvent.InitialState) {
                    runOnUiThread {
                        if (!isFinishing) {
                            startActivity(Intent(this, GameActivity::class.java))
                        }
                    }
                }
            },

            onError = {
                ActiveGameManager.resetAll()
            }
        )

        ActiveGameManager.reconnectGame()
    }

}
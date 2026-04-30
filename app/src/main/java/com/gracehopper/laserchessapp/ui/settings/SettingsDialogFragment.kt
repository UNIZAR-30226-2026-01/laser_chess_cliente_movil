package com.gracehopper.laserchessapp.ui.settings

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.model.user.ChangePasswordRequest
import com.gracehopper.laserchessapp.data.model.user.MyProfile
import com.gracehopper.laserchessapp.data.model.user.UpdateAccountRequest
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.AuthRepository
import com.gracehopper.laserchessapp.data.repository.UserRepository
import com.gracehopper.laserchessapp.utils.TokenManager
import com.gracehopper.laserchessapp.utils.redirectToLogin
import com.gracehopper.laserchessapp.utils.validation.UsernameValidationResult
import com.gracehopper.laserchessapp.utils.validation.UsernameValidator

/**
 * Diálogo de notificaciones de retos de partidas amistosas
 */
class SettingsDialogFragment : DialogFragment() {

    private lateinit var userRepository: UserRepository
    private lateinit var authRepository: AuthRepository

    private lateinit var buttonClose: ImageButton
    private lateinit var txtEmail: TextView
    private lateinit var buttonEditMail: ImageButton
    private lateinit var txtChangePassword: TextView
    private lateinit var txtEliminateAccount: TextView
    private lateinit var checkNotifications: CheckBox
    private lateinit var buttonLogout: Button

    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    private var changingNotificationCheckProgrammatically = false

    private var currentMail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiService = NetworkUtils.getApiService()
        userRepository = UserRepository(apiService)
        authRepository = AuthRepository(apiService)

        notificationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (!granted) {
                    Toast.makeText(
                        requireContext(),
                        "Notificaciones desactivadas",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                syncNotificationCheck()
            }

        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.dialog_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupTexts()
        loadUserData()
        setupListeners()
        syncNotificationCheck()
    }

    private fun bindViews(view: View) {

        buttonClose = view.findViewById(R.id.buttonCloseSettingsDialog)
        txtEmail = view.findViewById(R.id.txtEmailSettings)
        buttonEditMail = view.findViewById(R.id.btnEditEmail)
        txtChangePassword = view.findViewById(R.id.txtChangePassword)
        txtEliminateAccount = view.findViewById(R.id.txtEliminateAccount)
        checkNotifications = view.findViewById(R.id.checkNotifications)
        buttonLogout = view.findViewById(R.id.buttonLogout)

    }

    private fun setupTexts() {

        txtChangePassword.paintFlags =
            txtChangePassword.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        txtEliminateAccount.paintFlags =
            txtEliminateAccount.paintFlags or Paint.UNDERLINE_TEXT_FLAG

    }

    private fun loadUserData() {

        currentMail = CurrentUserManager.getMyCurrentMail()

        if (currentMail != null) {
            txtEmail.text = currentMail
            return
        }

        userRepository.getMyProfile(
            onSuccess = { profile ->
                requireActivity().runOnUiThread {
                    CurrentUserManager.setMyProfile(profile)
                    txtEmail.text = profile.mail
                }
            },
            onError = {
                requireActivity().runOnUiThread {
                    txtEmail.text = ""
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar datos del usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    }

    private fun setupListeners() {

        buttonClose.setOnClickListener { dismiss() }

        buttonEditMail.setOnClickListener {
            openEditMailDialog()
        }

        txtChangePassword.setOnClickListener {
            openChangePasswordDialog()
        }

        txtEliminateAccount.setOnClickListener {
            openEliminateAccountDialog()
            Toast.makeText(requireContext(),
                "Eliminar cuenta",
                Toast.LENGTH_SHORT
            ).show()
        }

        checkNotifications.setOnCheckedChangeListener { _, isChecked ->

            if (changingNotificationCheckProgrammatically) return@setOnCheckedChangeListener

            if (isChecked) {
                requestNotificationPermission()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Para desactivar las notificaciones, cambia el permiso en ajustes",
                    Toast.LENGTH_LONG
                ).show()

                openAppNotificationSettings()
            }

        }

        buttonLogout.setOnClickListener {
            openLogoutDialog()
        }

    }

    private fun openEditMailDialog() {
        // AlertDialog temporal para salir del paso
        // TODO Dialog en nueva pantalla de editar email

        val editText = EditText(requireContext()).apply {
            setText(currentMail.orEmpty())
            setSelection(text.length)
            hint = "Nuevo e-mail"
            maxLines = 1
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Cambiar e-mail")
            .setMessage("Introduce tu nuevo e-mail.")
            .setView(editText)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar", null)
            .create()
            .also { dialog ->

                dialog.setOnShowListener {

                    val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

                    positiveButton.setOnClickListener {
                        val newMail = editText.text.toString().trim()
                        validateAndSaveMail(newMail, dialog)
                    }

                }

                dialog.show()

            }

    }

    private fun validateAndSaveMail(newUsername: String, dialog: AlertDialog) {
        // TODO MAIL VALIDATOR
        when (UsernameValidator.validate(newUsername)) {

            UsernameValidationResult.EmptyUsername -> {
                Toast.makeText(requireContext(),
                    "El username no puede estar vacío",
                    Toast.LENGTH_SHORT).show()
            }

            UsernameValidationResult.LongUsername -> {
                Toast.makeText(requireContext(),
                    "Máximo ${UsernameValidator.MAX_LENGTH} caracteres",
                    Toast.LENGTH_SHORT).show()
            }

            UsernameValidationResult.InvalidUsername -> {
                Toast.makeText(requireContext(),
                    "El username no puede contener espacios en blanco",
                    Toast.LENGTH_SHORT).show()
            }

            UsernameValidationResult.Valid -> {

                if (newUsername == currentUsername) {
                    Toast.makeText(requireContext(),
                        "El nuevo username debe ser distinto al actual",
                        Toast.LENGTH_SHORT).show()
                    return
                }

                userRepository.updateMyProfile(
                    request = UpdateAccountRequest(username = newUsername),
                    onSuccess = { profile ->
                        CurrentUserManager.setMyProfile(profile)
                        bindProfile(profile)
                        Toast.makeText(requireContext(),
                            "Username actualizado",
                            Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    },
                    onError = { code ->
                        when (code) {
                            409 -> {
                                Toast.makeText(requireContext(),
                                    "El username ya está en uso",
                                    Toast.LENGTH_SHORT).show()
                            }
                            400 -> {
                                Toast.makeText(requireContext(),
                                    "El username no es válido",
                                    Toast.LENGTH_SHORT).show()
                            }
                            null -> {
                                Toast.makeText(requireContext(),
                                    "Error de conexión al actualizar tu username",
                                    Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(requireContext(),
                                    "Error al actualizar tu username",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )

            }

        }

    }

    private fun openChangePasswordDialog() {
        // AlertDialog temporal para salir del paso
        // TODO Dialog en nueva pantalla de change password

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 10)
        }

        val editCurrentPassword = EditText(requireContext()).apply {
            hint = "Contraseña actual"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()
        }

        val editNewPassword = EditText(requireContext()).apply {
            hint = "Nueva contraseña"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()
        }

        val editRepeatPassword = EditText(requireContext()).apply {
            hint = "Repite la nueva contraseña"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()
        }

        container.addView(editCurrentPassword)
        container.addView(editNewPassword)
        container.addView(editRepeatPassword)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Cambiar contraseña")
            .setMessage("Introduce tu contraseña actual y la nueva.")
            .setView(container)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            positiveButton.setOnClickListener {
                val currentPassword = editCurrentPassword.text.toString().trim()
                val newPassword = editNewPassword.text.toString().trim()
                val repeatPassword = editRepeatPassword.text.toString().trim()

                validateAndChangePassword(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    repeatPassword = repeatPassword,
                    dialog = dialog
                )
            }
        }

        dialog.show()
    }

    private fun validateAndChangePassword(currentPassword: String,
                                          newPassword: String,
                                          repeatPassword: String,
                                          dialog: AlertDialog
    ) {

        when {
            currentPassword.isBlank() -> {
                Toast.makeText(requireContext(),
                    "Contraseña actual vacía",
                    Toast.LENGTH_SHORT
                ).show()
            }

            newPassword.isBlank() -> {
                Toast.makeText(requireContext(),
                    "Nueva contraseña vacía",
                    Toast.LENGTH_SHORT
                ).show()
            }

            newPassword.length < 6 -> {
                Toast.makeText(requireContext(),
                    "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
            }

            newPassword.length > 50 -> {
                Toast.makeText(requireContext(),
                    "La contraseña no puede tener más de 50 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
            }

            repeatPassword.isBlank() -> {
                Toast.makeText(requireContext(),
                    "Repite la contraseña nueva",
                    Toast.LENGTH_SHORT
                ).show()
            }

            newPassword != repeatPassword -> {
                Toast.makeText(requireContext(),
                    "Las contraseñas no coinciden",
                    Toast.LENGTH_SHORT
                ).show()
            }

            currentPassword == newPassword -> {
                Toast.makeText(requireContext(),
                    "La nueva contraseña debe ser distinta a la actual",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                changePassword(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    dialog = dialog
                )
            }

        }

    }

    private fun changePassword(currentPassword: String,
                               newPassword: String,
                               dialog: AlertDialog
    ) {

        userRepository.changePassword(
            request = ChangePasswordRequest(currentPassword, newPassword),

            onSuccess = {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Contraseña actualizada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },

            onError = { code ->
                requireActivity().runOnUiThread {

                    when (code) {

                        401 -> {
                            Toast.makeText(
                                requireContext(),
                                "Contraseña actual incorrecta",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        400 -> {
                            Toast.makeText(
                                requireContext(),
                                "Datos inválidos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                requireContext(),
                                "Error al cambiar la contraseña",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        )
        dialog.dismiss()

    }

    private fun openEliminateAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar cuenta")
            .setMessage("¿Estás seguro de que quieres eliminar tu cuenta?\n"
                        + "Esta acción no se puede deshacer")
            .setPositiveButton("Sí") { _, _ ->

                userRepository.deleteMyAccount(
                    onSuccess = {
                        CurrentUserManager.clearMyProfile()
                        TokenManager.clear()
                        // ir a login
                    },
                    onError = { code ->
                        when (code) {
                            401 -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Cuenta no válida",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            null -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Error de conexión",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Error al eliminar cuenta",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )

            }
            .setNegativeButton("Cancelar", null)
            .show()

    }

    private fun syncNotificationCheck() {
        val notificationsEnabled = NotificationManagerCompat
            .from(requireContext())
            .areNotificationsEnabled()

        val permissionGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        changingNotificationCheckProgrammatically = true
        checkNotifications.isChecked = notificationsEnabled && permissionGranted
        changingNotificationCheckProgrammatically = false
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (granted) {
                syncNotificationCheck()
                return
            }

            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Activa las notificaciones desde ajustes",
                    Toast.LENGTH_LONG
                ).show()

                openAppNotificationSettings()
                syncNotificationCheck()
            }
        } else {
            syncNotificationCheck()
        }
    }

    private fun openAppNotificationSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${requireContext().packageName}")
            }
        }

        startActivity(intent)
    }

    private fun openLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun logout() {

        authRepository.logout(

            onSuccess = {
                requireActivity().runOnUiThread {
                    clearSession()
                }
            },

            onError = {
                requireActivity().runOnUiThread {
                    // aunque falle backend → cerramos sesión igualmente
                    clearSession()
                }
            }

        )

    }

    private fun clearSession() {

        // limpio tokens
        TokenManager.clear()
        NetworkUtils.clearCookies()

        // limpio perfil en memoria
        CurrentUserManager.clearMyProfile()

        // cerrar websockets si hay, hace falta??
        // ActiveGameManager.disconnect()
        // TODO SseManager.disconnect()

        // ir a login y limpiar backstack
        redirectToLogin(requireContext())

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onResume() {
        super.onResume()

        if (::checkNotifications.isInitialized) {
            syncNotificationCheck()
        }
    }

    companion object {
        fun newInstance(): SettingsDialogFragment {
            return SettingsDialogFragment()
        }
    }
}
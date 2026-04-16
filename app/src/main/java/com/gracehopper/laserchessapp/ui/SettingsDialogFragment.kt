package com.gracehopper.laserchessapp.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.manager.SseManager
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.UserRepository
import com.gracehopper.laserchessapp.ui.auth.LoginActivity
import com.gracehopper.laserchessapp.utils.TokenManager
import com.gracehopper.laserchessapp.utils.redirectToLogin


/**
 * Diálogo de notificaciones de retos de partidas amistosas
 */
class SettingsDialogFragment : DialogFragment() {

    private lateinit var userRepository: UserRepository

    private lateinit var buttonClose: ImageButton
    private lateinit var txtEmail: TextView
    private lateinit var txtChangePassword: TextView
    private lateinit var txtEliminateAccount: TextView
    private lateinit var checkMusic: CheckBox
    private lateinit var checkSoundEffects: CheckBox
    private lateinit var checkNotifications: CheckBox
    private lateinit var buttonLogout: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userRepository = UserRepository(NetworkUtils.getApiService())
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

    }

    private fun bindViews(view: View) {

        buttonClose = view.findViewById(R.id.buttonCloseSettingsDialog)
        txtEmail = view.findViewById(R.id.txtEmailSettings)
        txtChangePassword = view.findViewById(R.id.txtChangePassword)
        txtEliminateAccount = view.findViewById(R.id.txtEliminateAccount)
        checkMusic = view.findViewById(R.id.checkMusic)
        checkSoundEffects = view.findViewById(R.id.checkSoundEffects)
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

        val currentProfile = CurrentUserManager.getMyCurrentProfile()

        if (currentProfile != null) {
            txtEmail.text = currentProfile.mail
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

        checkMusic.setOnCheckedChangeListener { _, isChecked ->
            // TODO guardar ajustes música
            Toast.makeText(requireContext(),
                if (isChecked) "Música activada" else "Música desactivada",
                Toast.LENGTH_SHORT
            ).show()
        }

        checkSoundEffects.setOnCheckedChangeListener { _, isChecked ->
            // TODO guardar ajustes efectos de sonido
            Toast.makeText(requireContext(),
                if (isChecked) "Efectos de sonido activados" else "Efectos de sonido desactivados",
                Toast.LENGTH_SHORT
            ).show()
        }

        checkNotifications.setOnCheckedChangeListener { _, isChecked ->
            // TODO guardar ajustes notificaciones
            Toast.makeText(
                requireContext(),
                if (isChecked) "Notificaciones activadas" else "Notificaciones desactivadas",
                Toast.LENGTH_SHORT
            ).show()
        }

        buttonLogout.setOnClickListener {
            openLogoutDialog()
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

                validateAndSavePassword(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    repeatPassword = repeatPassword,
                    dialog = dialog
                )
            }
        }

        dialog.show()
    }

    private fun validateAndSavePassword(currentPassword: String,
                                         newPassword: String,
                                         repeatPassword: String,
                                         dialog: AlertDialog) {

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
                saveNewPassword(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    dialog = dialog
                )
            }

        }

    }

    private fun saveNewPassword(currentPassword: String,
                                 newPassword: String,
                                 dialog: AlertDialog) {

        // TODO llamada a repository para cambiar contraseña
        Toast.makeText(requireContext(),
            "Cuando funke, aquí actualizará pass",
            Toast.LENGTH_SHORT
        ).show()
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
        // TODO: Añadir llamada a backend para cierre de sesión

        // limpio tokens
        TokenManager.clear()

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

    companion object {
        fun newInstance(): SettingsDialogFragment {
            return SettingsDialogFragment()
        }
    }
}
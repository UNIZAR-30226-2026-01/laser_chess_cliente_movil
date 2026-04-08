package com.gracehopper.laserchessapp.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
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

        buttonClose = view.findViewById(R.id.buttonCloseNotifications)
        txtEmail = view.findViewById(R.id.txtEmailSettings)
        txtChangePassword = view.findViewById(R.id.txtChangePassword)
        txtEliminateAccount = view.findViewById(R.id.txtEliminateAccount)
        checkMusic = view.findViewById(R.id.checkMusic)
        checkSoundEffects = view.findViewById(R.id.checkSoundEffects)
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
            Toast.makeText(requireContext(),
                "Cambiar contraseña",
                Toast.LENGTH_SHORT
            ).show()
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
        // TODO abrir diálogo de cambiar contraseña
    }

    private fun openEliminateAccountDialog() {
        // TODO abrir diálogo de eliminar cuenta
        /**
         * Llamada a repo para eliminar cuenta
         * userRepository.deleteMyAccount(
         *                     onSuccess = {
         *                         CurrentUserManager.clearMyProfile()
         *                         TokenManager.clear()
         *                         // ir a login
         *                     },
         *                     onError = { code ->
         *                         when(code) {
         *                             401 -> {
         *                                 Toast.makeText(requireContext(),
         *                                     "Sesión no válida",
         *                                     Toast.LENGTH_SHORT).show()
         *                             }
         *                             null -> {
         *                                 Toast.makeText(requireContext(),
         *                                     "Error de conexión",
         *                                     Toast.LENGTH_SHORT).show()
         *                             }
         *                             else -> {
         *                                 Toast.makeText(requireContext(),
         *                                     "Error al cerrar sesión",
         *                                     Toast.LENGTH_SHORT).show()
         *                             }
         *                         }
         *                     }
         *                 )
         */
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

        // limpio tokens
        TokenManager.clear()

        // limpio perfil en memoria
        CurrentUserManager.clearMyProfile()

        // cerrar websockets si hay, hace falta??
        // ActiveGameManager.disconnect()

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
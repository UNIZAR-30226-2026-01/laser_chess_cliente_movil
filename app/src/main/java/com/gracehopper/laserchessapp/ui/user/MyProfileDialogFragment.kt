package com.gracehopper.laserchessapp.ui.user

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.model.user.MyProfile
import com.gracehopper.laserchessapp.data.model.user.UpdateAccountRequest
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.UserRepository
import com.gracehopper.laserchessapp.ui.main.MainActivity
import com.gracehopper.laserchessapp.ui.utils.AvatarUtils
import com.gracehopper.laserchessapp.ui.utils.ItemUtils
import com.gracehopper.laserchessapp.utils.validation.UsernameValidationResult
import com.gracehopper.laserchessapp.utils.validation.UsernameValidator

/**
 * DialogFragment que muestra el perfil del usuario loggeado.
 */
class MyProfileDialogFragment : DialogFragment() {

    private lateinit var userRepository: UserRepository

    private lateinit var imageProfileAvatar: ImageView
    private lateinit var txtProfileUsername: TextView
    private lateinit var txtProfileLevel: TextView
    private lateinit var txtProfileXp: TextView
    private lateinit var progressProfileXP: ProgressBar
    private lateinit var txtProfileCoins: TextView

    private lateinit var txtProfileBlitzElo: TextView
    private lateinit var txtProfileRapidElo: TextView
    private lateinit var txtProfileClassicElo: TextView
    private lateinit var txtProfileExtendedElo: TextView

    private lateinit var imgPieceSkin: ImageView
    private lateinit var imgBoardSkin: ImageView
    private lateinit var imgWinAnimation: ImageView

    private lateinit var buttonEditAvatar: ImageButton
    private lateinit var buttonEditUsername: ImageButton
    private lateinit var buttonEditEquipped: ImageButton

    private lateinit var buttonClose: ImageButton

    private var currentProfile: MyProfile? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        userRepository = UserRepository(NetworkUtils.getApiService())

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_my_profile, null)

        bindViews(dialogView)
        setupCloseButton()
        setupEditButtons()
        loadMyProfile()

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog

    }

    private fun bindViews(dialogView: View) {

        imageProfileAvatar = dialogView.findViewById(R.id.imageMyProfileAvatar)
        txtProfileUsername = dialogView.findViewById(R.id.txtMyProfileUsername)
        txtProfileLevel = dialogView.findViewById(R.id.txtMyProfileLevel)
        txtProfileXp = dialogView.findViewById(R.id.txtMyProfileXp)
        progressProfileXP = dialogView.findViewById(R.id.progressMyProfileXp)
        txtProfileCoins = dialogView.findViewById(R.id.txtMyProfileCoins)

        txtProfileBlitzElo = dialogView.findViewById(R.id.txtBlitzMyElo)
        txtProfileRapidElo = dialogView.findViewById(R.id.txtRapidMyElo)
        txtProfileClassicElo = dialogView.findViewById(R.id.txtClassicMyElo)
        txtProfileExtendedElo = dialogView.findViewById(R.id.txtExtendedMyElo)

        imgPieceSkin = dialogView.findViewById(R.id.imageMyPieceSkin)
        imgBoardSkin = dialogView.findViewById(R.id.imageMyBoardSkin)
        imgWinAnimation = dialogView.findViewById(R.id.imageMyWinAnimation)

        buttonEditAvatar = dialogView.findViewById(R.id.buttonEditAvatar)
        buttonEditUsername = dialogView.findViewById(R.id.buttonEditUsername)
        buttonEditEquipped = dialogView.findViewById(R.id.buttonEditEquipped)

        buttonClose = dialogView.findViewById(R.id.buttonCloseMyProfileDialog)

    }

    private fun setupEditButtons() {

        buttonEditAvatar.setOnClickListener { openCustomizePage() }

        buttonEditUsername.setOnClickListener { openEditUsername() }

        buttonEditEquipped.setOnClickListener { openCustomizePage() }

    }

    private fun openEditUsername() {

        val editText = EditText(requireContext()).apply {
            setText(currentProfile?.username.orEmpty())
            setSelection(text.length)
            hint = "Nuevo username"
            maxLines = 1
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Cambiar username")
            .setMessage("Introduce tu nuevo username.")
            .setView(editText)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar", null)
            .create()
            .also { dialog ->

                dialog.setOnShowListener {

                    val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

                    positiveButton.setOnClickListener {
                        val newUsername = editText.text.toString().trim()
                        validateAndSaveUsername(newUsername, dialog)
                    }

                }

                dialog.show()

            }

        Toast.makeText(requireContext(), "Edit Username", Toast.LENGTH_SHORT).show()
    }

    private fun validateAndSaveUsername(newUsername: String, dialog: AlertDialog) {

        val currentUsername = currentProfile?.username.orEmpty()

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

    private fun openCustomizePage() {
        (activity as? MainActivity)?.openCustomizeFragment()
        dismiss()
    }

    private fun setupCloseButton() {
        buttonClose.setOnClickListener {
            dismiss()
        }
    }

    private fun loadMyProfile() {

        val cachedProfile = CurrentUserManager.getMyCurrentProfile()

        if (cachedProfile != null) {
            bindProfile(cachedProfile)
            return
        }

        userRepository.getMyProfile(
            onSuccess = { profile ->
                CurrentUserManager.setMyProfile(profile)
                bindProfile(profile)
            },
            onError = {
                Toast.makeText(requireContext(),
                    "Error al cargar tu perfil",
                    Toast.LENGTH_SHORT).show()
                dismiss()
            }
        )

    }

    private fun bindProfile(profile: MyProfile) {

        currentProfile = profile

        txtProfileUsername.text = profile.username
        txtProfileLevel.text = "Nivel ${profile.level}"
        txtProfileXp.text = "${profile.xp} XP"
        txtProfileCoins.text = profile.money.toString()

        txtProfileBlitzElo.text = profile.ratings.blitz.toString()
        txtProfileRapidElo.text = profile.ratings.rapid.toString()
        txtProfileClassicElo.text = profile.ratings.classic.toString()
        txtProfileExtendedElo.text = profile.ratings.extended.toString()

        imageProfileAvatar.setImageResource(AvatarUtils.getAvatarDrawable(profile.avatar))

        progressProfileXP.max = 100
        progressProfileXP.progress = profile.xp % 100

        imgPieceSkin.setImageResource(ItemUtils.getPieceSkinDrawable(profile.pieceSkin))
        imgBoardSkin.setImageResource(ItemUtils.getBoardSkinDrawable(profile.boardSkin))
        imgWinAnimation.setImageResource(ItemUtils.getWinAnimationDrawable(profile.winAnimation))

    }

    companion object {

        fun newInstance() : MyProfileDialogFragment {
            return MyProfileDialogFragment()
        }

    }

}
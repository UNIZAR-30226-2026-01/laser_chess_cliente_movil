package com.gracehopper.laserchessapp.ui.user

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.user.UserProfile
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.UserRepository

class UserProfileDialogFragment : DialogFragment() {

    private lateinit var userRepository: UserRepository

    private lateinit var imageProfileAvatar: ImageView
    private lateinit var txtProfileUsername: TextView
    private lateinit var txtProfileLevel: TextView
    private lateinit var txtProfileXp: TextView
    private lateinit var txtProfileBlitzElo: TextView
    private lateinit var txtProfileRapidElo: TextView
    private lateinit var txtProfileClassicElo: TextView
    private lateinit var txtProfileExtendedElo: TextView
    private lateinit var buttonClose: ImageButton

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        userRepository = UserRepository(NetworkUtils.getApiService())

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_friend_profile, null)

        bindViews(dialogView)
        setupCloseButton()

        val userId = requireArguments().getLong(ARG_FRIEND_ID, -1L)

        if (userId == -1L) {
            Toast.makeText(requireContext(), "Usuario no válido", Toast.LENGTH_SHORT).show()
            dismiss()
        } else {
            loadUserProfile(userId)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog

    }

    private fun bindViews(dialogView: View) {

        imageProfileAvatar = dialogView.findViewById(R.id.imageProfileAvatar)
        txtProfileUsername = dialogView.findViewById(R.id.txtProfileUsername)
        txtProfileLevel = dialogView.findViewById(R.id.txtProfileLevel)
        txtProfileXp = dialogView.findViewById(R.id.txtProfileXp)
        txtProfileBlitzElo = dialogView.findViewById(R.id.txtBlitzElo)
        txtProfileRapidElo = dialogView.findViewById(R.id.txtRapidElo)
        txtProfileClassicElo = dialogView.findViewById(R.id.txtClassicElo)
        txtProfileExtendedElo = dialogView.findViewById(R.id.txtExtendedElo)
        buttonClose = dialogView.findViewById(R.id.buttonCloseProfileDialog)

    }

    private fun setupCloseButton() {
        buttonClose.setOnClickListener {
            dismiss()
        }
    }

    private fun loadUserProfile(userId: Long) {

        userRepository.getUserProfile(userId = userId,
            onSuccess = { profile ->
                bindProfile(profile)},
            onError = { Toast.makeText(requireContext(),
                "Error al cargar el perfil",
                Toast.LENGTH_SHORT).show()
                dismiss()
            })

    }

    private fun bindProfile(profile: UserProfile) {

        txtProfileUsername.text = profile.username
        txtProfileLevel.text = "Nivel ${profile.level}"
        txtProfileXp.text = "${profile.xp} XP"

        txtProfileBlitzElo.text = profile.ratings.blitz.toString()
        txtProfileRapidElo.text = profile.ratings.rapid.toString()
        txtProfileClassicElo.text = profile.ratings.classic.toString()
        txtProfileExtendedElo.text = profile.ratings.extended.toString()

        imageProfileAvatar.setImageResource(mapAvatarToResource(profile.avatar))

    }

    private fun mapAvatarToResource(avatar: Int): Int {

        return when(avatar) {
            1 -> R.drawable.avatar_1
            2 -> R.drawable.avatar_2
            3 -> R.drawable.avatar_3
            4 -> R.drawable.avatar_4
            else -> R.drawable.avatar_1
        }

    }

    companion object {

        private const val ARG_FRIEND_ID = "friend_id"

        fun newInstance(friendId: Long): UserProfileDialogFragment {
            val fragment = UserProfileDialogFragment()
            val args = Bundle().apply {
                putLong(ARG_FRIEND_ID, friendId)
            }
            fragment.arguments = args
            return fragment
        }

    }

}
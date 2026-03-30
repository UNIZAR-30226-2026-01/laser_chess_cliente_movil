package com.gracehopper.laserchessapp.ui.user

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.user.UserProfile
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.FriendRepository
import com.gracehopper.laserchessapp.data.repository.UserRepository
import com.gracehopper.laserchessapp.ui.gameConfig.MatchConfigDialogFragment
import com.gracehopper.laserchessapp.ui.utils.AvatarUtils
import com.gracehopper.laserchessapp.ui.utils.ItemUtils

class UserProfileDialogFragment : DialogFragment() {

    private lateinit var userRepository: UserRepository
    private lateinit var friendRepository: FriendRepository

    private lateinit var imageProfileAvatar: ImageView
    private lateinit var txtProfileUsername: TextView
    private lateinit var txtProfileLevel: TextView
    private lateinit var txtProfileXp: TextView
    private lateinit var progressProfileXP: ProgressBar

    private lateinit var txtProfileBlitzElo: TextView
    private lateinit var txtProfileRapidElo: TextView
    private lateinit var txtProfileClassicElo: TextView
    private lateinit var txtProfileExtendedElo: TextView

    private lateinit var imgPieceSkin: ImageView
    private lateinit var imgBoardSkin: ImageView
    private lateinit var imgWinAnimation: ImageView

    private lateinit var buttonClose: ImageButton
    private lateinit var buttonPrimaryAction: Button
    private lateinit var buttonSecondaryAction: Button

    private var currentUsername: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        userRepository = UserRepository(NetworkUtils.getApiService())
        friendRepository = FriendRepository(NetworkUtils.getApiService())

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_user_profile, null)

        bindViews(dialogView)
        setupCloseButton()

        val userId = requireArguments().getLong(ARG_FRIEND_ID, -1L)

        if (userId == -1L) {
            Toast.makeText(requireContext(), "Usuario no válido", Toast.LENGTH_SHORT).show()
            dismiss()
        } else {
            loadUserProfile(userId)
        }

        val dialogModeName = requireArguments().getString(ARG_DIALOG_MODE)
        val dialogMode = UserProfileDialogMode.valueOf(
            dialogModeName ?: UserProfileDialogMode.FRIEND.name
        )

        setupActionButtons(dialogMode, userId)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog

    }

    private fun setupActionButtons(mode: UserProfileDialogMode, userId: Long) {

        when(mode) {

            UserProfileDialogMode.FRIEND -> {
                buttonPrimaryAction.visibility = View.VISIBLE
                buttonSecondaryAction.visibility = View.VISIBLE

                buttonPrimaryAction.text = getString(R.string.request_match)
                buttonSecondaryAction.text = getString(R.string.delete)

                buttonPrimaryAction.setOnClickListener {
                    currentUsername?.let { username ->
                        MatchConfigDialogFragment(username).show(
                            parentFragmentManager,
                            "MatchConfigDialog"
                        )
                    }
                }

                buttonSecondaryAction.setOnClickListener {
                    currentUsername?.let { username ->
                        showDeleteFriendConfirmation(username)
                    }
                }
            }

            UserProfileDialogMode.RECEIVED_REQUEST -> {
                buttonPrimaryAction.visibility = View.VISIBLE
                buttonSecondaryAction.visibility = View.VISIBLE

                buttonPrimaryAction.text = getString(R.string.accept)
                buttonSecondaryAction.text = getString(R.string.reject)

                buttonPrimaryAction.setOnClickListener {
                    currentUsername?.let { username ->
                        acceptFriendshipRequest(username)
                    }
                }

                buttonSecondaryAction.setOnClickListener {
                    currentUsername?.let { username ->
                        rejectFriendshipRequest(username)
                    }
                }

            }

            UserProfileDialogMode.SENT_REQUEST -> {
                buttonPrimaryAction.visibility = View.GONE
                buttonSecondaryAction.visibility = View.VISIBLE

                buttonSecondaryAction.text = getString(R.string.cancel_request)

                buttonSecondaryAction.setOnClickListener {
                    currentUsername?.let { username ->
                        cancelFriendshipRequest(username)
                    }
                }
            }

            UserProfileDialogMode.USER -> {
                buttonPrimaryAction.visibility = View.VISIBLE
                buttonSecondaryAction.visibility = View.GONE

                buttonPrimaryAction.text = getString(R.string.send_request)

                buttonPrimaryAction.setOnClickListener {
                    currentUsername?.let { username ->
                        // TODO Enviar solicitud de amistad
                    }
                    Toast.makeText(requireContext(),
                        "Solicitando amistad a $userId",
                        Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun bindViews(dialogView: View) {

        imageProfileAvatar = dialogView.findViewById(R.id.imageProfileAvatar)
        txtProfileUsername = dialogView.findViewById(R.id.txtProfileUsername)
        txtProfileLevel = dialogView.findViewById(R.id.txtProfileLevel)
        txtProfileXp = dialogView.findViewById(R.id.txtProfileXp)
        progressProfileXP = dialogView.findViewById(R.id.progressProfileXp)

        txtProfileBlitzElo = dialogView.findViewById(R.id.txtBlitzElo)
        txtProfileRapidElo = dialogView.findViewById(R.id.txtRapidElo)
        txtProfileClassicElo = dialogView.findViewById(R.id.txtClassicElo)
        txtProfileExtendedElo = dialogView.findViewById(R.id.txtExtendedElo)

        imgPieceSkin = dialogView.findViewById(R.id.imagePieceSkin)
        imgBoardSkin = dialogView.findViewById(R.id.imageBoardSkin)
        imgWinAnimation = dialogView.findViewById(R.id.imageWinAnimation)

        buttonClose = dialogView.findViewById(R.id.buttonCloseProfileDialog)
        buttonPrimaryAction = dialogView.findViewById(R.id.buttonPrimaryAction)
        buttonSecondaryAction = dialogView.findViewById(R.id.buttonSecondaryAction)

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

        currentUsername = profile.username

        txtProfileUsername.text = profile.username
        txtProfileLevel.text = "Nivel ${profile.level}"
        txtProfileXp.text = "${profile.xp} XP"

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

    private fun showDeleteFriendConfirmation(username: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Segur@ que deseas eliminar a $username?")
            .setPositiveButton("Sí") { _, _ ->
                removeFriend(username)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun removeFriend(username: String) {

        friendRepository.deleteFriendship(username = username,
            onSuccess = {
                Toast.makeText(requireContext(), "Amig@ eliminado",
                    Toast.LENGTH_SHORT).show()
                parentFragmentManager.setFragmentResult("friend_removed", Bundle())
                dismiss()
            },
            onError = { errorCode ->
                Toast.makeText(requireContext(), "Error al eliminar: $errorCode",
                    Toast.LENGTH_SHORT).show()
            }
        )

    }

    private fun acceptFriendshipRequest(username: String) {
        friendRepository.acceptFriendship(
            username = username,
            onSuccess = {
                Toast.makeText(requireContext(), "Solicitud aceptada", Toast.LENGTH_SHORT).show()
                parentFragmentManager.setFragmentResult("requests_updated", Bundle())
                dismiss()
            },
            onError = { errorCode ->
                Toast.makeText(
                    requireContext(),
                    "Error al aceptar: $errorCode",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun rejectFriendshipRequest(username: String) {
        friendRepository.deleteFriendship(
            username = username,
            onSuccess = {
                Toast.makeText(requireContext(), "Solicitud rechazada", Toast.LENGTH_SHORT).show()
                parentFragmentManager.setFragmentResult("requests_updated", Bundle())
                dismiss()
            },
            onError = { errorCode ->
                Toast.makeText(
                    requireContext(),
                    "Error al rechazar: $errorCode",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun cancelFriendshipRequest(username: String) {
        friendRepository.deleteFriendship(
            username = username,
            onSuccess = {
                Toast.makeText(requireContext(), "Solicitud cancelada", Toast.LENGTH_SHORT).show()
                parentFragmentManager.setFragmentResult("requests_updated", Bundle())
                dismiss()
            },
            onError = { errorCode ->
                Toast.makeText(
                    requireContext(),
                    "Error al cancelar: $errorCode",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    companion object {

        private const val ARG_FRIEND_ID = "friend_id"
        private const val ARG_DIALOG_MODE = "dialog_mode"

        fun newInstance(friendId: Long,
                        mode: UserProfileDialogMode = UserProfileDialogMode.FRIEND)
                        : UserProfileDialogFragment {
            val fragment = UserProfileDialogFragment()
            val args = Bundle().apply {
                putLong(ARG_FRIEND_ID, friendId)
                putString(ARG_DIALOG_MODE, mode.name)
            }
            fragment.arguments = args
            return fragment
        }

    }

}
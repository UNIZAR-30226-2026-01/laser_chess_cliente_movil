package com.gracehopper.laserchessapp.ui.user

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.user.MyProfile
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.UserRepository
import com.gracehopper.laserchessapp.ui.utils.AvatarUtils
import com.gracehopper.laserchessapp.ui.utils.ItemUtils

class MyProfileDialogFragment : DialogFragment() {

    private lateinit var userRepository: UserRepository

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


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        userRepository = UserRepository(NetworkUtils.getApiService())

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_my_profile, null)

        bindViews(dialogView)
        setupCloseButton()
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

        txtProfileBlitzElo = dialogView.findViewById(R.id.txtBlitzMyElo)
        txtProfileRapidElo = dialogView.findViewById(R.id.txtRapidMyElo)
        txtProfileClassicElo = dialogView.findViewById(R.id.txtClassicMyElo)
        txtProfileExtendedElo = dialogView.findViewById(R.id.txtExtendedMyElo)

        imgPieceSkin = dialogView.findViewById(R.id.imageMyPieceSkin)
        imgBoardSkin = dialogView.findViewById(R.id.imageMyBoardSkin)
        imgWinAnimation = dialogView.findViewById(R.id.imageMyWinAnimation)

        buttonClose = dialogView.findViewById(R.id.buttonCloseMyProfileDialog)

    }

    private fun setupCloseButton() {
        buttonClose.setOnClickListener {
            dismiss()
        }
    }

    private fun loadMyProfile() {

        userRepository.getMyProfile(
            onSuccess = { profile ->
                bindProfile(profile)},
            onError = { Toast.makeText(requireContext(),
                "Error al cargar tu perfil",
                Toast.LENGTH_SHORT).show()
                dismiss()
            })

    }

    private fun bindProfile(profile: MyProfile) {

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

    companion object {

        fun newInstance() : MyProfileDialogFragment {
            return MyProfileDialogFragment()
        }

    }

}
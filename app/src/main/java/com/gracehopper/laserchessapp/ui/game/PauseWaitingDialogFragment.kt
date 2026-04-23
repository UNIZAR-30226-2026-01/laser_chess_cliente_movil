package com.gracehopper.laserchessapp.ui.game

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.gracehopper.laserchessapp.R

class PauseWaitingDialogFragment (
    private val onCancel: () -> Unit
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_pause_waiting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val btnCancel = view.findViewById<ImageButton>(R.id.btnCancelPause)

        btnCancel.setOnClickListener {
            onCancel()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)

            val params = attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT

            params.gravity = Gravity.TOP

            attributes = params

            setDimAmount(0f) // sin fondo oscuro
        }
    }
}
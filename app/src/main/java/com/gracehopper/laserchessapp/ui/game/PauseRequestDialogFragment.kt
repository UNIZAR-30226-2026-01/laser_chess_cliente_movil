package com.gracehopper.laserchessapp.ui.game

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.gracehopper.laserchessapp.R

class PauseRequestDialogFragment (
    private val onAccept: () -> Unit,
    private val onReject: () -> Unit
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_pause_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val btnAccept = view.findViewById<Button>(R.id.btnAcceptPause)
        val btnReject = view.findViewById<Button>(R.id.btnRejectPause)

        btnAccept.setOnClickListener {
            onAccept()
            dismiss()
        }

        btnReject.setOnClickListener {
            onReject()
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
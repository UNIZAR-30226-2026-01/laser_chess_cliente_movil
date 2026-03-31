package com.gracehopper.laserchessapp.ui.game

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager

class WaitingGameDialogFragment : DialogFragment() {

    private lateinit var textOpponent: TextView
    private lateinit var textDetails: TextView
    private lateinit var buttonCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_waiting_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textOpponent = view.findViewById(R.id.textWaitingOpponent)
        textDetails = view.findViewById(R.id.textWaitingDetails)
        buttonCancel = view.findViewById(R.id.buttonCancelChallengeRequest)

        loadMatchInfo()
        setupListeners()
        setupCallbacks()
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setDimAmount(0.55f)
        }
    }

    private fun loadMatchInfo() {
        val opponent = ActiveGameManager.currentOpponentUsername ?: "rival"
        val board = ActiveGameManager.currentBoard ?: 1
        val startingTime = ActiveGameManager.currentStartingTime ?: 300
        val increment = ActiveGameManager.currentTimeIncrement ?: 0

        textOpponent.text = "Esperando a que $opponent acepte la partida"
        textDetails.text = "Tablero $board · ${startingTime}s + ${increment}s"
    }

    private fun setupListeners() {
        buttonCancel.setOnClickListener {
            ActiveGameManager.closeConnection()

            Toast.makeText(
                requireContext(),
                "Solicitud cancelada",
                Toast.LENGTH_SHORT
            ).show()

            dismiss()
        }
    }

    private fun setupCallbacks() {
        ActiveGameManager.setCallbacks(
            onConnected = {
                // conectados
            },
            onMessageReceived = { message ->
                requireActivity().runOnUiThread {

                    ActiveGameManager.handleServerMessage(message)

                    // si llega cualquier mensaje,
                    // asumimos que la partida ya ha empezado
                    ActiveGameManager.markInGame()

                        Toast.makeText(
                            requireContext(),
                            "La partida ha comenzado",
                            Toast.LENGTH_SHORT
                        ).show()

                        dismiss()

                        val intent = Intent(requireContext(), GameActivity::class.java)
                        startActivity(intent)
                    }
                }
            },
            onError = { error ->
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error en la solicitud: $error",
                        Toast.LENGTH_LONG
                    ).show()
                    dismiss()
                }
            },
            onClosed = {
                requireActivity().runOnUiThread {
                    if (isAdded) {
                        Toast.makeText(
                            requireContext(),
                            "La espera de partida ha finalizado",
                            Toast.LENGTH_SHORT
                        ).show()
                        dismiss()
                    }
                }
            }
        )
    }
}
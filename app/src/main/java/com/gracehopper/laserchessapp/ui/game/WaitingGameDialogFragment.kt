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
import com.gracehopper.laserchessapp.data.model.game.GameEvent

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

        loadGameInfo()
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

    private fun loadGameInfo() {
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
            onMessageReceived = { event ->
                requireActivity().runOnUiThread {

                    when (event) {

                        is GameEvent.ChallengeRejected -> {
                            Toast.makeText(
                                requireContext(),
                                "Tu rival ha rechazado la partida",
                                Toast.LENGTH_SHORT
                            ).show()

                            ActiveGameManager.resetAll()
                            dismiss()
                        }

                        is GameEvent.InitialState -> {
                            ActiveGameManager.markInGame()

                            Toast.makeText(
                                requireContext(),
                                "La partida ha comenzado",
                                Toast.LENGTH_SHORT
                            ).show()

                            dismiss()

                            val intent = Intent(requireContext(),
                                GameActivity::class.java)
                            startActivity(intent)
                        }

                        is GameEvent.Error -> {
                            Toast.makeText(
                                requireContext(),
                                event.message,
                                Toast.LENGTH_LONG
                            ).show()

                            ActiveGameManager.resetAll()
                            dismiss()
                        }

                        // Para cierres lógicos enviados por server
                        is GameEvent.ConnectionClosed -> {
                            if (isAdded) {
                                Toast.makeText(
                                    requireContext(),
                                    "La espera de partida ha finalizado",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dismiss()
                            }
                        }

                        else -> {
                            // ignorar otros eventos en esta pantalla
                        }

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

                    ActiveGameManager.resetAll()
                    dismiss()
                }
            },
            // Para cierre técnico de socket
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
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
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.UserRepository

class WaitingGameDialogFragment : DialogFragment() {

    private lateinit var textOpponent: TextView
    private lateinit var textDetails: TextView
    private lateinit var buttonCancel: Button

    @Volatile
    private var matchFound = false
    private var pendingOpponentId: Long? = null

    @Volatile
    private var opponentProfileReady = false
    private var opponentPieceSkin: Int = 1
    private var opponentBoardSkin: Int = 4
    private var opponentAvatar: Int = 1

    @Volatile
    private var waitingForProfile = false

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
        val opponent = ActiveGameManager.getOpponentUsername()
        val board = ActiveGameManager.currentBoard ?: 1
        val startingTime = ActiveGameManager.currentStartingTime ?: 300
        val increment = ActiveGameManager.currentTimeIncrement ?: 0

        when (ActiveGameManager.currentMatchType) {

            ActiveGameManager.MatchType.RANKED,
            ActiveGameManager.MatchType.CASUAL -> {

                textOpponent.text = "Buscando rival en el matchmaking..."
                textDetails.text = "Tablero $board · ${startingTime}s + ${increment}s"
            }

            ActiveGameManager.MatchType.BOTS -> {

                textOpponent.text = "Preparando partida contra IA..."
                textDetails.text = "Tablero $board · ${startingTime}s + ${increment}s"
            }

            ActiveGameManager.MatchType.PRIVATE -> {

                if (ActiveGameManager.currentMatchId != null) {

                    textOpponent.text = "Esperando al otro jugador"
                    textDetails.text = "Retomando partida con ${opponent ?: "rival"}"

                } else {

                    textOpponent.text = "Esperando al otro jugador"
                    textDetails.text = "Partida con ${opponent ?: "rival"}"
                }
            }

            null -> {

                textOpponent.text = "Preparando partida..."
                textDetails.text = "Conectando..."
            }
        }
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
                if (event is GameEvent.InitialState) {
                    matchFound = true
                }

                val act = activity ?: return@setCallbacks
                act.runOnUiThread {

                    when (event) {

                        is GameEvent.MatchStart -> {

                            pendingOpponentId = event.opponentId

                            textOpponent.text = "¡Rival encontrado!"

                            event.opponentId?.let {
                                loadOpponentProfile(it)
                            }
                        }

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
                            ActiveGameManager.initialStateConsumed = true
                            ActiveGameManager.markInGame()

                            if (opponentProfileReady) {
                                navigateToGame()
                            } else {
                                waitingForProfile = true
                            }
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
                            if (!matchFound && isAdded) {
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
                if (matchFound) return@setCallbacks
                val act = activity ?: return@setCallbacks
                act.runOnUiThread {
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
                if (matchFound) return@setCallbacks
                val act = activity ?: return@setCallbacks
                act.runOnUiThread {
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

    private fun navigateToGame() {
        if (!isAdded) return
        Toast.makeText(requireContext(), "La partida ha comenzado", Toast.LENGTH_SHORT).show()
        dismiss()
        val intent = Intent(requireContext(), GameActivity::class.java).apply {
            pendingOpponentId?.let { putExtra("OPPONENT_ID", it) }
            putExtra("OPPONENT_PIECE_SKIN", opponentPieceSkin)
            putExtra("OPPONENT_BOARD_SKIN", opponentBoardSkin)
            putExtra("OPPONENT_AVATAR", opponentAvatar)
        }
        startActivity(intent)
    }

    private fun loadOpponentProfile(opponentId: Long) {

        val userRepo = UserRepository(NetworkUtils.getApiService())

        userRepo.getUserProfile(
            userId = opponentId,
            onSuccess = { profile ->

                opponentPieceSkin = profile.pieceSkin
                opponentBoardSkin = profile.boardSkin
                opponentAvatar = profile.avatar.takeIf { it > 0 } ?: 1

                opponentProfileReady = true

                if (waitingForProfile) {
                    val act = activity ?: return@getUserProfile
                    act.runOnUiThread { navigateToGame() }
                }
            },
            onError = {

                opponentProfileReady = true

                if (waitingForProfile) {
                    val act = activity ?: return@getUserProfile
                    act.runOnUiThread { navigateToGame() }
                }
            }
        )
    }
}
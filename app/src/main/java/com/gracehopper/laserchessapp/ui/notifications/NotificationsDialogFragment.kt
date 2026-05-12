package com.gracehopper.laserchessapp.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.data.model.game.GameEvent
import com.gracehopper.laserchessapp.data.model.game.GamePlayerInfo
import com.gracehopper.laserchessapp.data.model.game.PendingChallengeResponse
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.ChallengeRepository
import com.gracehopper.laserchessapp.data.repository.UserRepository
import com.gracehopper.laserchessapp.ui.game.GameActivity
import com.gracehopper.laserchessapp.utils.AppEvents
import kotlinx.coroutines.launch

/**
 * Diálogo de notificaciones de retos de partidas amistosas
 */
class NotificationsDialogFragment : DialogFragment() {

    private lateinit var challengeRepository: ChallengeRepository
    private lateinit var userRepository: UserRepository
    private lateinit var buttonClose: ImageButton
    private lateinit var recyclerChallenges: RecyclerView
    private lateinit var textEmptyState: TextView

    private lateinit var adapter: PendingChallengesAdapter

    private var opponentId: Long? = null
    private var opponentAvatar: Int = 1
    private var opponentPieceSkin: Int = 1
    private var opponentBoardSkin: Int = 4


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        challengeRepository = ChallengeRepository(NetworkUtils.getApiService())
        userRepository = UserRepository(NetworkUtils.getApiService())
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_notifications,
            container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    AppEvents.challengeReceived.collect {
                        loadPendingChallenges()
                    }
                }
            }
        }

        val topSection = view.findViewById<View>(R.id.dialog_top_section)
        buttonClose = topSection.findViewById(R.id.buttonCloseDialog)
        val dialogTitle = topSection.findViewById<TextView>(R.id.dialogTitle)
        val dialogIcon = topSection.findViewById<ImageView>(R.id.dialogIcon)

        dialogTitle.text = "Retos pendientes"
        dialogIcon.setImageResource(R.drawable.inbox_32px)
        dialogIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.LCWhite))

        recyclerChallenges = view.findViewById(R.id.recyclerChallenges)
        textEmptyState = view.findViewById(R.id.textEmptyState)

        setupRecyclerView()
        setupListeners()
        loadPendingChallenges()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun setupRecyclerView() {
        adapter = PendingChallengesAdapter(
            challenges = emptyList(),
            onAcceptClicked = { challenge ->
                acceptChallenge(challenge)
            },
            onRejectClicked = { challenge ->
                rejectChallenge(challenge)
            }
        )

        recyclerChallenges.layoutManager = LinearLayoutManager(requireContext())
        recyclerChallenges.adapter = adapter
    }

    private fun setupListeners() {
        buttonClose.setOnClickListener {
            dismiss()
        }
    }

    private fun loadPendingChallenges() {
        challengeRepository.getPendingChallenges(
            onSuccess = { challenges ->
                activity?.runOnUiThread {
                    showChallenges(challenges)
                }
            },
            onError = {
                activity?.runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar retos pendientes",
                        Toast.LENGTH_SHORT
                    ).show()
                    showChallenges(emptyList())
                }
            }
        )
    }

    private fun showChallenges(challenges: List<PendingChallengeResponse>) {
        adapter.updateChallenges(challenges)

        if (challenges.isEmpty()) {
            recyclerChallenges.visibility = View.GONE
            textEmptyState.visibility = View.VISIBLE
        } else {
            recyclerChallenges.visibility = View.VISIBLE
            textEmptyState.visibility = View.GONE
        }
    }

    private fun acceptChallenge(challenge: PendingChallengeResponse) {

        userRepository.getUserProfile(
            userId = challenge.challengerId,
            onSuccess = { opponent ->
                if (!isAdded) return@getUserProfile

                val opponentInfo = GamePlayerInfo(
                    id = opponent.id,
                    username = opponent.username,
                    avatar = opponent.avatar,
                    pieceSkin = opponent.pieceSkin,
                    boardSkin = opponent.boardSkin,
                    winAnimation = opponent.winAnimation
                )

                requireActivity().runOnUiThread {

                    opponentId = opponent.id
                    opponentAvatar = opponent.avatar.takeIf { it > 0 } ?: 1
                    opponentPieceSkin = opponent.pieceSkin
                    opponentBoardSkin = opponent.boardSkin

                    ActiveGameManager.acceptChallenge(
                        opponentInfo = opponentInfo,
                        board = challenge.board,
                        startingTime = challenge.startingTime,
                        timeIncrement = challenge.timeIncrement
                    )

                    setupChallengeCallbacks()

                }
            },
            onError = {
                if (!isAdded) return@getUserProfile

                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar el perfil del oponente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    }

    private fun setupChallengeCallbacks() {
        ActiveGameManager.setCallbacks(
            onConnected = {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(),
                        "Reto aceptado. Conectando partida...",
                        Toast.LENGTH_SHORT).show()
                }
            },
            onMessageReceived = { event ->
                requireActivity().runOnUiThread {

                    when (event) {

                        is GameEvent.InitialState -> {

                            ActiveGameManager.initialStateConsumed = true
                            ActiveGameManager.markInGame()

                            Toast.makeText(requireContext(),
                                "La partida ha comenzado",
                                Toast.LENGTH_SHORT).show()

                            AppEvents.challengeReceived.tryEmit(Unit)
                            dismiss()

                            val intent = Intent(requireContext(), GameActivity::class.java).apply {
                                opponentId?.let { putExtra("OPPONENT_ID", it) }
                                putExtra("OPPONENT_AVATAR", opponentAvatar)
                                putExtra("OPPONENT_PIECE_SKIN", opponentPieceSkin)
                                putExtra("OPPONENT_BOARD_SKIN", opponentBoardSkin)
                            }
                            startActivity(intent)

                        }

                        is GameEvent.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "Error al aceptar reto: ${event.message}",
                                Toast.LENGTH_SHORT
                            ).show()

                            ActiveGameManager.resetAll()
                        }

                        GameEvent.ChallengeRejected -> {
                            Toast.makeText(
                                requireContext(),
                                "El reto ya no está disponible",
                                Toast.LENGTH_SHORT
                            ).show()

                            ActiveGameManager.resetAll()
                            loadPendingChallenges()
                            AppEvents.challengeReceived.tryEmit(Unit)
                        }

                        is GameEvent.ConnectionClosed -> {
                            loadPendingChallenges()
                        }

                        else -> {
                            // ignorar otros eventos
                        }
                    }
                }
            },
            onError = { error ->
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(),
                        "Error al aceptar reto: $error",
                        Toast.LENGTH_SHORT).show()
                }
            },
            onClosed = {
                requireActivity().runOnUiThread {
                    // feedback si se cierra la conexión¿
                }
            }
        )
    }

    private fun rejectChallenge(challenge: PendingChallengeResponse) {

        ActiveGameManager.setCallbacks(
            onConnected = {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(),
                        "Reto rechazado",
                        Toast.LENGTH_SHORT).show()
                }
            },
            onMessageReceived = { event ->
                requireActivity().runOnUiThread {

                    when (event) {

                        is GameEvent.ChallengeRejected -> {
                            Toast.makeText(requireContext(),
                                "Reto rechazado",
                                Toast.LENGTH_SHORT).show()

                            ActiveGameManager.resetAll()
                            loadPendingChallenges()
                            AppEvents.challengeReceived.tryEmit(Unit)
                        }

                        is GameEvent.Error -> {
                            Toast.makeText(requireContext(),
                                "Error al rechazar reto: ${event.message}",
                                Toast.LENGTH_SHORT).show()

                            ActiveGameManager.resetAll()
                        }

                        is GameEvent.ConnectionClosed -> {
                            loadPendingChallenges()
                        }

                        else -> {
                            // ignorar otros eventos
                        }

                    }
                }
            },
            onError = { error ->
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(),
                        "Error al rechazar reto: $error",
                        Toast.LENGTH_SHORT).show()
                }
            },
            onClosed = {
                requireActivity().runOnUiThread {
                    loadPendingChallenges()
                    AppEvents.challengeReceived.tryEmit(Unit)
                }
            }
        )

        ActiveGameManager.rejectChallenge(challengerUsername = challenge.challengerUsername)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // no cierro socket
        // si acepto y cierro dialog -> rompo partida lel
        // mirar cuando haya conexión con game
    }

}
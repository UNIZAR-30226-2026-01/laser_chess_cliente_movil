package com.gracehopper.laserchessapp.ui.notifications

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.data.model.game.PendingChallengeResponse
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.ChallengeRepository
import com.gracehopper.laserchessapp.ui.game.GameActivity

/**
 * Diálogo de notificaciones de retos de partidas amistosas
 */
class NotificationsDialogFragment : DialogFragment() {

    private lateinit var challengeRepository: ChallengeRepository
    private lateinit var buttonClose: ImageButton
    private lateinit var recyclerChallenges: RecyclerView
    private lateinit var textEmptyState: TextView

    private lateinit var adapter: PendingChallengesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        challengeRepository = ChallengeRepository(NetworkUtils.getApiService())
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

        buttonClose = view.findViewById(com.gracehopper.laserchessapp.R.id.buttonCloseNotifications)
        recyclerChallenges = view.findViewById(com.gracehopper.laserchessapp.R.id.recyclerChallenges)
        textEmptyState = view.findViewById(com.gracehopper.laserchessapp.R.id.textEmptyState)

        setupRecyclerView()
        setupListeners()
        loadPendingChallenges()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupRecyclerView() {
        adapter = PendingChallengesAdapter(
            challenges = emptyList(),
            onAcceptClicked = { challenge ->
                acceptChallenge(challenge)
            },
            onRejectClicked = { challenge ->
                // TODO Rechazar solicitud de partida amistosa
                Toast.makeText(
                    requireContext(),
                    "Rechazar reto de ${challenge.challengerUsername}",
                    Toast.LENGTH_SHORT
                ).show()
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

        ActiveGameManager.setCallbacks(
            onConnected = {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(),
                        "Reto aceptado. Conectando partida...",
                        Toast.LENGTH_SHORT).show()
                }
            },
            onMessageReceived = { message, extra ->
                requireActivity().runOnUiThread {

                    if (message == "INITIAL_STATE") {
                        ActiveGameManager.markInGame()

                        Toast.makeText(requireContext(),
                            "La partida ha comenzado",
                            Toast.LENGTH_SHORT).show()

                        dismiss()

                        val intent = Intent(requireContext(), GameActivity::class.java)
                        startActivity(intent)
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

        ActiveGameManager.acceptChallenge(challengerUsername = challenge.challengerUsername,
            board = challenge.board,
            startingTime = challenge.startingTime,
            timeIncrement = challenge.timeIncrement
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // no cierro socket
        // si acepto y cierro dialog -> rompo partida lel
        // mirar cuando haya conexión con game
    }

}
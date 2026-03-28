package com.gracehopper.laserchessapp.ui.notifications

import android.R
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
import com.gracehopper.laserchessapp.data.model.game.PendingChallengeResponse
import com.gracehopper.laserchessapp.data.repository.ChallengeRepository

class NotificationsDialogFragment : DialogFragment() {

    private lateinit var buttonClose: ImageButton
    private lateinit var recyclerChallenges: RecyclerView
    private lateinit var textEmptyState: TextView

    private lateinit var adapter: PendingChallengesAdapter

    private val challengeRepository = ChallengeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
        setStyle(STYLE_NO_TITLE, R.style.Theme_Translucent_NoTitleBar)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(com.gracehopper.laserchessapp.R.layout.dialog_notifications, container, false)
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

        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setDimAmount(0.55f)
        }
    }

    private fun setupRecyclerView() {
        adapter = PendingChallengesAdapter(
            challenges = emptyList(),
            onAcceptClicked = { challenge ->
                Toast.makeText(
                    requireContext(),
                    "Aceptar reto de ${challenge.challengerUsername}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onRejectClicked = { challenge ->
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

}
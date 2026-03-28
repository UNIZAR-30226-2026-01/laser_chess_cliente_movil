package com.gracehopper.laserchessapp.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.game.PendingChallengeResponse

class PendingChallengesAdapter(
    private var challenges: List<PendingChallengeResponse>,
    private val onAcceptClicked: (PendingChallengeResponse) -> Unit,
    private val onRejectClicked: (PendingChallengeResponse) -> Unit
) : RecyclerView.Adapter<PendingChallengesAdapter.PendingChallengeViewHolder>() {

    fun updateChallenges(newChallenges: List<PendingChallengeResponse>) {
        challenges = newChallenges
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingChallengeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_challenge, parent, false)
        return PendingChallengeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PendingChallengeViewHolder, position: Int) {
        holder.bind(challenges[position])
    }

    override fun getItemCount(): Int = challenges.size

    inner class PendingChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textChallengeTitle: TextView = itemView.findViewById(R.id.textChallengeTitle)
        private val textChallengeDetails: TextView = itemView.findViewById(R.id.textChallengeDetails)
        private val buttonAccept: Button = itemView.findViewById(R.id.buttonAcceptChallenge)
        private val buttonReject: Button = itemView.findViewById(R.id.buttonRejectChallenge)

        fun bind(challenge: PendingChallengeResponse) {
            textChallengeTitle.text = "${challenge.challengerUsername} te ha retado a una partida"
            textChallengeDetails.text =
                "Tablero ${challenge.board} · ${challenge.startingTime}s + ${challenge.timeIncrement}s"

            buttonAccept.setOnClickListener {
                onAcceptClicked(challenge)
            }

            buttonReject.setOnClickListener {
                onRejectClicked(challenge)
            }
        }
    }
}
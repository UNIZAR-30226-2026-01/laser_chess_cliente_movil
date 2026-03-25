package com.gracehopper.laserchessapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.data.model.game.InProgressMatchSummary
import com.gracehopper.laserchessapp.databinding.ItemInProgressBinding

class InProgressAdapter(private var games: List<InProgressMatchSummary>,
    private val onResumeClick: (InProgressMatchSummary) -> Unit)
    : RecyclerView.Adapter<InProgressAdapter.InProgressViewHolder>() {

    class InProgressViewHolder(val binding: ItemInProgressBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InProgressViewHolder {
        val binding = ItemInProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InProgressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InProgressViewHolder, position: Int) {
        val game = games[position]

        holder.binding.apply {
            textMyTime.text = game.myTime
            textOpponentUsername.text = game.opponentUsername
            textOpponentTime.text = game.opponentTime
            textTimeMode.text = game.timeMode.toString()
            textBoardType.text = game.boardType.toString()

            buttonResumeMatch.setOnClickListener { onResumeClick(game) }
        }
    }

    override fun getItemCount(): Int = games.size

    fun updateData(newGames: List<InProgressMatchSummary>) {
        games = newGames
        notifyDataSetChanged()
    }

}
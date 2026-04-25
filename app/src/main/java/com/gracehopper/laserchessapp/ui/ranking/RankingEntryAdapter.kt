package com.gracehopper.laserchessapp.ui.ranking

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.ranking.RankingEntry
import com.gracehopper.laserchessapp.databinding.ItemRankingEntryBinding
import com.gracehopper.laserchessapp.ui.utils.AvatarUtils

class RankingEntryAdapter(
    private var entries: List<RankingEntry>,
    private val onUserClicked: (RankingEntry) -> Unit
) : RecyclerView.Adapter<RankingEntryAdapter.RankingEntryViewHolder>() {

    class RankingEntryViewHolder(val binding: ItemRankingEntryBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingEntryViewHolder {

        val binding = ItemRankingEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RankingEntryViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RankingEntryViewHolder, position: Int) {
        val entry = entries[position]

        val context = holder.itemView.context

        with(holder.binding) {
            textRankingPosition.text = entry.position.toString()
            textRankingUsername.text = entry.username
            textRankingElo.text = entry.elo.toString()
            imageRankingAvatar.setImageResource(
                AvatarUtils.getAvatarDrawable(entry.avatar)
            )

            val highlightColor = when {
                entry.isCurrentUser -> R.color.LCBlue
                entry.position == 1 -> R.color.LCYellow
                entry.position == 2 -> R.color.silver
                entry.position == 3 -> R.color.bronze
                else -> R.color.LCWhite
            }

            val rowColor = ContextCompat.getColor(context, highlightColor)
            textRankingUsername.setTextColor(rowColor)
            textRankingPosition.setTextColor(rowColor)
            textRankingElo.setTextColor(rowColor)

            root.setOnClickListener {
                onUserClicked(entry)
            }
        }
    }

    override fun getItemCount(): Int = entries.size

    fun updateData(newEntries: List<RankingEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

}
package com.gracehopper.laserchessapp.ui.ranking

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.ranking.RankingEntry
import com.gracehopper.laserchessapp.databinding.ItemRankingEntryBinding
import com.gracehopper.laserchessapp.ui.utils.ItemUtils

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
                ItemUtils.getItemDrawable(entry.avatar)
            )


            when (entry.position) {
                1 -> { // ORO
                textRankingPosition.setBackgroundResource(R.drawable.bg_rank_position_frame)
                textRankingPosition.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.LCYellow))
                textRankingPosition.setTextColor(ContextCompat.getColor(context, R.color.LCYellow))
                }
                2 -> { // PLATA
                textRankingPosition.setBackgroundResource(R.drawable.bg_rank_position_frame)
                textRankingPosition.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.LCGray))
                textRankingPosition.setTextColor(ContextCompat.getColor(context, R.color.LCGray))
                }
                3 -> { // BRONCE
                textRankingPosition.setBackgroundResource(R.drawable.bg_rank_position_frame)
                textRankingPosition.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bronze))
                textRankingPosition.setTextColor(ContextCompat.getColor(context, R.color.bronze))
                }
                else -> { // DEL 4 EN ADELANTE (Sin círculo, blanco)
                textRankingPosition.background = null
                textRankingPosition.setTextColor(ContextCompat.getColor(context, R.color.LCWhite))
                }
            }

            if(entry.isCurrentUser){
                cardRankingEntry.setCardBackgroundColor(ContextCompat.getColor(context, R.color.S3))
                textRankingUsername.setTextColor(ContextCompat.getColor(context, R.color.white))
            }

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
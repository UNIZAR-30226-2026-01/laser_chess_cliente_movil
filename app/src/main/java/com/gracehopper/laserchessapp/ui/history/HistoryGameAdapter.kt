package com.gracehopper.laserchessapp.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.game.GameResume
import com.gracehopper.laserchessapp.data.model.user.AccountResponse
import com.gracehopper.laserchessapp.ui.utils.ItemUtils

class HistoryGameAdapter(
    private var games: List<GameResume>,
    private var userCache: Map<Long, AccountResponse>,
    private val onViewClick: (GameResume) -> Unit
) : RecyclerView.Adapter<HistoryGameAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textPlayer1Name: TextView = view.findViewById(R.id.textPlayer1Name)
        val textPlayer2Name: TextView = view.findViewById(R.id.textPlayer2Name)
        val textPlayer1Elo: TextView = view.findViewById(R.id.textPlayer1Elo)
        val textPlayer2Elo: TextView = view.findViewById(R.id.textPlayer2Elo)
        val textBoard: TextView = view.findViewById(R.id.textBoard)
        val textMatchType: TextView = view.findViewById(R.id.textMatchType)
        val textWinner: TextView = view.findViewById(R.id.textWinner)
        val textDate: TextView = view.findViewById(R.id.textDate)

        val avatar1: ShapeableImageView = view.findViewById(R.id.avatarPlayer1)
        val avatar2: ShapeableImageView = view.findViewById(R.id.avatarPlayer2)

        val btnViewGame: Button = view.findViewById(R.id.btnViewGame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_game, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = games[position]

        val player1 = userCache[game.p1Id]
        val player2 = userCache[game.p2Id]

        holder.textPlayer1Name.text = player1?.username ?: "Jugador ${game.p1Id}"
        holder.textPlayer2Name.text = player2?.username ?: "Jugador ${game.p2Id}"

        holder.textPlayer1Elo.text = game.p1Elo.toString()
        holder.textPlayer2Elo.text = game.p2Elo.toString()

        holder.textBoard.text = game.board.uppercase().ifEmpty { "—" }
        holder.textMatchType.text = game.matchType.uppercase().ifEmpty { "—" }

        holder.textWinner.text =
            userCache[game.winner.toLongOrNull()]?.username ?: game.winner

        holder.textDate.text = formatDate(game.date)

        val avatar1Id = player1?.avatar?.takeIf { it > 0 } ?: 1
        val avatar2Id = player2?.avatar?.takeIf { it > 0 } ?: 1

        holder.avatar1.setImageResource(
            ItemUtils.getItemDrawable(avatar1Id)
        )

        holder.avatar2.setImageResource(
            ItemUtils.getItemDrawable(avatar2Id)
        )

        holder.btnViewGame.setOnClickListener {
            onViewClick(game)
        }
    }

    override fun getItemCount(): Int = games.size

    fun updateData(newGames: List<GameResume>, newCache: Map<Long, AccountResponse>) {
        games = newGames
        userCache = newCache
        notifyDataSetChanged()
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val datePart = dateStr.substringBefore("T")
            val parts = datePart.split("-")
            if (parts.size >= 3) "${parts[2]}/${parts[1]}" else dateStr
        } catch (e: Exception) {
            dateStr
        }
    }
}
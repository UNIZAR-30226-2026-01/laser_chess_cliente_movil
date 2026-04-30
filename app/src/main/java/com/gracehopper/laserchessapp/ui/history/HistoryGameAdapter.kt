package com.gracehopper.laserchessapp.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.game.GameResume

/**
 * Adapter para mostrar el historial de partidas terminadas.
 */
class HistoryGameAdapter(
    private var games: List<GameResume>,
    private val usernameCache: Map<Long, String>
) : RecyclerView.Adapter<HistoryGameAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textPlayer1Name: TextView = view.findViewById(R.id.textPlayer1Name)
        val textPlayer2Name: TextView = view.findViewById(R.id.textPlayer2Name)
        val textPlayer1Elo: TextView  = view.findViewById(R.id.textPlayer1Elo)
        val textPlayer2Elo: TextView  = view.findViewById(R.id.textPlayer2Elo)
        val textBoard: TextView       = view.findViewById(R.id.textBoard)
        val textMatchType: TextView   = view.findViewById(R.id.textMatchType)
        val textWinner: TextView      = view.findViewById(R.id.textWinner)
        val textDate: TextView        = view.findViewById(R.id.textDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_game, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = games[position]

        holder.textPlayer1Name.text = usernameCache[game.p1_id] ?: "Jugador ${game.p1_id}"
        holder.textPlayer2Name.text = usernameCache[game.p2_id] ?: "Jugador ${game.p2_id}"
        holder.textPlayer1Elo.text  = game.p1_elo.toString()
        holder.textPlayer2Elo.text  = game.p2_elo.toString()
        holder.textBoard.text       = game.board.uppercase()
        holder.textMatchType.text   = game.match_type.uppercase()
        holder.textWinner.text      = usernameCache[game.winner.toLongOrNull()] ?: game.winner
        holder.textDate.text        = formatDate(game.date)
    }

    override fun getItemCount(): Int = games.size

    fun updateData(newGames: List<GameResume>, newCache: Map<Long, String>) {
        games = newGames
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
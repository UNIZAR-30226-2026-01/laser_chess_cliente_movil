package com.gracehopper.laserchessapp.ui.gameConfig

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.game.BoardOption

class BoardSelectionAdapter(private val boards: List<BoardOption>,
    private var selectedBoardId: Int?,
    private val onBoardSelected: (BoardOption) -> Unit)
    : RecyclerView.Adapter<BoardSelectionAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup,
        viewType: Int) : BoardViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_board_option, parent, false)
        return BoardViewHolder(view)

    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.bind(boards[position])
    }

    override fun getItemCount(): Int = boards.size

    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cardBoard: CardView = itemView.findViewById(R.id.cardBoardOption)
        private val imageBoard: ImageView = itemView.findViewById(R.id.imageBoardOption)
        private val textBoardName: TextView = itemView.findViewById(R.id.textBoardName)

        fun bind(board: BoardOption) {
            imageBoard.setImageResource(board.image)
            textBoardName.text = board.name

            cardBoard.alpha = if (board.id == selectedBoardId) 1f else 0.65f

            cardBoard.setOnClickListener {
                selectedBoardId = board.id
                onBoardSelected(board)
                notifyDataSetChanged()
            }
        }

    }

}
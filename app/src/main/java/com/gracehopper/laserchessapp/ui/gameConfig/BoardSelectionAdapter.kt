package com.gracehopper.laserchessapp.ui.gameConfig

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.model.game.BoardLayouts
import com.gracehopper.laserchessapp.data.model.game.BoardOption
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.gameLogic.board.BoardParser
import com.gracehopper.laserchessapp.ui.home.HomeBoardPreview

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
        private val composeBoardPreview: ComposeView = itemView.findViewById(R.id.composeBoardPreview)
        private val textBoardName: TextView = itemView.findViewById(R.id.textBoardName)

        init {
            composeBoardPreview.setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
        }

        @SuppressLint("NotifyDataSetChanged")
        fun bind(boardOption: BoardOption) {
            textBoardName.text = boardOption.name

            cardBoard.alpha = if (boardOption.id == selectedBoardId) 1f else 0.65f

            val pieceSkin = CurrentUserManager.getMyCurrentPieceSkin()
            val boardSkin = CurrentUserManager.getMyCurrentBoardSkin()

            composeBoardPreview.setContent {
                val board = remember(boardOption.name) {
                    Board(rows = 10, cols = 8).also {
                        BoardParser.boardFromCSV(it, BoardLayouts.getCsvForBoard(boardOption.name))
                    }
                }
                HomeBoardPreview(
                    board = board,
                    pieceSkin = pieceSkin,
                    boardSkin = boardSkin
                )
            }

            composeBoardPreview.setOnClickListener {
                selectedBoardId = boardOption.id
                onBoardSelected(boardOption)
                notifyDataSetChanged()
            }

            cardBoard.setOnClickListener {
                selectedBoardId = boardOption.id
                onBoardSelected(boardOption)
                notifyDataSetChanged()
            }
        }

    }

}
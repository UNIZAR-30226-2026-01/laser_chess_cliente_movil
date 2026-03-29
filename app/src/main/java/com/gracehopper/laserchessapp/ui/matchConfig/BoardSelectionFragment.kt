package com.gracehopper.laserchessapp.ui.matchConfig

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.game.BoardOption

class BoardSelectionFragment : Fragment() {

    private lateinit var recyclerBoards: RecyclerView
    private lateinit var adapter: BoardSelectionAdapter

    private val parentConfigDialog: MatchConfigDialogFragment?
        get() = parentFragment as? MatchConfigDialogFragment

    private val boards by lazy {
        listOf(
            BoardOption(1, "Ace", R.drawable.board_1),
            BoardOption(2, "Curiosity", R.drawable.board_2),
            BoardOption(3, "Grail", R.drawable.board_3),
            BoardOption(4, "Mercury", R.drawable.board_4),
            BoardOption(5, "Sophie", R.drawable.board_5)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_board_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerBoards = view.findViewById(R.id.recyclerBoards)

        adapter = BoardSelectionAdapter(
            boards = boards,
            selectedBoardId = null,
            onBoardSelected = { board ->
                parentConfigDialog?.updateSelectedBoard(board.id, board.name)
            }
        )

        recyclerBoards.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerBoards.adapter = adapter
    }

    companion object {
        fun newInstance(): BoardSelectionFragment {
            return BoardSelectionFragment()
        }
    }
}
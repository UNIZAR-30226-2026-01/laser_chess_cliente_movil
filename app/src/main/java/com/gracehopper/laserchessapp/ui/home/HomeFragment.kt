package com.gracehopper.laserchessapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.data.model.game.BoardLayouts
import com.gracehopper.laserchessapp.data.model.game.GameMode
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.gameLogic.board.BoardParser
import com.gracehopper.laserchessapp.ui.game.GameActivity

class HomeFragment : Fragment() {

    private var currentMode = GameMode.BOT
    private var expanded = false

    private lateinit var topMode: GameMode
    private lateinit var middleMode: GameMode

    private var selectedBoardName: String = "Ace"
    private var selectedBoardId: Int = 1
    private var boardComposeView: ComposeView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnPlay = view.findViewById<Button>(R.id.btnPlay)

        val btnGameMode = view.findViewById<ImageButton>(R.id.btnGameMode)
        val btnModeTop = view.findViewById<ImageButton>(R.id.btnModeTop)
        val btnModeMiddle = view.findViewById<ImageButton>(R.id.btnModeMiddle)
        val layoutGamePopup = view.findViewById<View>(R.id.layoutGamePopup)

        refreshMainButton(btnGameMode)

        btnGameMode.setOnClickListener {

            expanded = !expanded

            if (expanded) {
                showModes(layoutGamePopup, btnModeTop, btnModeMiddle)
            } else {
                hideModes(layoutGamePopup, btnGameMode)
            }
        }

        btnModeTop.setOnClickListener {
            currentMode = topMode
            hideModes(layoutGamePopup, btnGameMode)
        }

        btnModeMiddle.setOnClickListener {
            currentMode = middleMode
            hideModes(layoutGamePopup, btnGameMode)
        }

        btnPlay.setOnClickListener {

            when (currentMode) {

                // Cambiar luego por valores reales
                GameMode.BOT -> {

                    ActiveGameManager.createBotGame(
                        board = selectedBoardId,
                        startingTime = 300,
                        timeIncrement = 2,
                        level = 1
                    )

                    startActivity(Intent(requireContext(), GameActivity::class.java))
                }

                GameMode.RANKED -> {
                    // matchmaking ranked
                }

                GameMode.PUBLIC -> {
                    // matchmaking publico
                }
            }
        }

        setupBoardPreview(view)
        setupSelectors(view)

        return view
    }

    /**
     * Configura el ComposeView para mostrar el tablero seleccionado.
     */
    private fun setupBoardPreview(view: View) {
        val boardContainer = view.findViewById<ViewGroup>(R.id.boardContainer)

        view.findViewById<ImageView?>(R.id.imgBoardPlaceholder)?.visibility = View.GONE
        val composeView = ComposeView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        boardComposeView = composeView
        boardContainer.addView(composeView)
        renderBoard()
    }

    /**
     * Parsea el CSV del tablero seleccionado y lo muestra en el ComposeView.
     */
    private fun renderBoard() {
        boardComposeView?.setContent {
            val board = Board(rows = 10, cols = 8)
            BoardParser.boadFromCSV(board, BoardLayouts.getCsvForBoard(selectedBoardName))
            HomeBoardPreview(board = board)
        }
    }

    private fun setupSelectors(view: View) {

        val includeBoardSelector = view.findViewById<View>(R.id.includeBoardSelector)
        val txtBoardTitle =
            includeBoardSelector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgBoardIcon =
            includeBoardSelector.findViewById<ImageView>(R.id.imgSelectorIcon)
        val LCRed = ContextCompat.getColor(requireContext(), R.color.LCRed)

        txtBoardTitle.text = selectedBoardName
        imgBoardIcon.setImageResource(R.drawable.ic_tablero)
        imgBoardIcon.setColorFilter(LCRed)

        includeBoardSelector.setOnClickListener {
            showBoardBottomSheet(LCRed, txtBoardTitle)
        }

        val includeTimeSelector = view.findViewById<View>(R.id.includeTimeSelector)
        val txtTimeTitle =
            includeTimeSelector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgTimeIcon =
            includeTimeSelector.findViewById<ImageView>(R.id.imgSelectorIcon)
        val LCBlue = ContextCompat.getColor(requireContext(), R.color.LCBlue)

        txtTimeTitle.text = "Modo de tiempo"
        imgTimeIcon.setImageResource(R.drawable.ic_tiempo)
        imgTimeIcon.setColorFilter(LCBlue)

        includeTimeSelector.setOnClickListener {
            val gameModeOptions = listOf("Blitz", "Bullet", "Classic", "Extended")
            showBottomSheet("Seleccionar modo de tiempo", gameModeOptions, LCBlue, txtTimeTitle)
        }
    }

    /**
     * BottomSheet específico para selección de tablero.
     * Al elegir un tablero actualiza la previsualización.
     */
    private fun showBoardBottomSheet(colorTitulo: Int, targetTextView: TextView) {

        val bottomSheetDialog = BottomSheetDialog(
            requireContext(),
            R.style.TemaBottomSheetTransparente
        )

        val dialogView = layoutInflater.inflate(R.layout.dialog_selector_desplegable, null)

        val txtTitle = dialogView.findViewById<TextView>(R.id.txtDialogTitle)
        txtTitle.text = "Seleccionar tablero"
        txtTitle.setTextColor(colorTitulo)

        val container = dialogView.findViewById<LinearLayout>(R.id.layoutOptionsContainer)

        BoardLayouts.ALL_BOARD_NAMES.forEachIndexed { index, boardName ->
            val button = com.google.android.material.button.MaterialButton(requireContext()).apply {
                text = boardName
                setTextColor(ContextCompat.getColor(context, R.color.LCWhite))
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.S2)
                cornerRadius = 36

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }

                setOnClickListener {
                    selectedBoardName = boardName
                    selectedBoardId = index + 1
                    targetTextView.text = boardName
                    renderBoard()          // ← actualiza el tablero en Home
                    bottomSheetDialog.dismiss()
                }
            }
            container.addView(button)
        }

        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()
    }

    private fun showBottomSheet(
        titulo: String,
        opciones: List<String>,
        colorTitulo: Int,
        targetTextView: TextView
    ) {
        val bottomSheetDialog = BottomSheetDialog(
            requireContext(),
            R.style.TemaBottomSheetTransparente
        )

        val dialogView = layoutInflater.inflate(R.layout.dialog_selector_desplegable, null)

        val txtTitle = dialogView.findViewById<TextView>(R.id.txtDialogTitle)
        txtTitle.text = titulo
        txtTitle.setTextColor(colorTitulo)

        val container = dialogView.findViewById<LinearLayout>(R.id.layoutOptionsContainer)

        for (opcion in opciones) {
            val button = com.google.android.material.button.MaterialButton(requireContext()).apply {
                text = opcion
                setTextColor(ContextCompat.getColor(context, R.color.LCWhite))
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.S2)
                cornerRadius = 36

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }

                setOnClickListener {
                    targetTextView.text = opcion
                    bottomSheetDialog.dismiss()
                }
            }
            container.addView(button)
        }

        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()
    }

    private fun iconFor(mode: GameMode): Int {
        return when (mode) {
            GameMode.BOT     -> R.drawable.robot_2_48px
            GameMode.RANKED  -> R.drawable.ic_ranked_mode
            GameMode.PUBLIC  -> R.drawable.ic_casual_mode
        }
    }

    private fun refreshMainButton(btn: ImageButton) {
        btn.setImageResource(iconFor(currentMode))
    }

    private fun showModes(popup: View, btnTop: ImageButton, btnMiddle: ImageButton) {
        val others = GameMode.values().filter { it != currentMode }
        topMode = others[0]
        middleMode = others[1]
        btnTop.setImageResource(iconFor(topMode))
        btnMiddle.setImageResource(iconFor(middleMode))
        popup.visibility = View.VISIBLE
    }

    private fun hideModes(popup: View, btnMain: ImageButton) {
        expanded = false
        popup.visibility = View.GONE
        refreshMainButton(btnMain)
    }
}
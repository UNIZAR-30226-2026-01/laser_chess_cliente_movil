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
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.model.game.BoardLayouts
import com.gracehopper.laserchessapp.data.model.game.GameMode
import com.gracehopper.laserchessapp.data.model.user.TimeMode
import com.gracehopper.laserchessapp.data.model.user.TimeModeConfig
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.gameLogic.board.BoardParser
import com.gracehopper.laserchessapp.ui.game.GameActivity
import com.gracehopper.laserchessapp.ui.game.WaitingGameDialogFragment

class HomeFragment : Fragment() {

    private var currentMode = GameMode.BOT
    private var expanded = false

    private lateinit var topMode: GameMode
    private lateinit var middleMode: GameMode

    private var selectedBoardName: String = "Ace"
    private var selectedBoardId: Int = 0
    private var selectedTimeMode: TimeMode = TimeMode.BLITZ
    private var selectedTimeIncrement: Int = 0
    private var selectedAiLevel: Int = 1

    private var boardComposeView: ComposeView? = null
    private var txtTimeIncrementTitle: TextView? = null
    private var rankBadgeView: View? = null
    private var selectedTimeModeForElo: TimeMode = TimeMode.BLITZ

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
            checkAiMode()
        }

        btnModeMiddle.setOnClickListener {
            currentMode = middleMode
            hideModes(layoutGamePopup, btnGameMode)
            checkAiMode()
        }

        btnPlay.setOnClickListener {

            when (currentMode) {

                GameMode.BOT -> {
                    ActiveGameManager.createBotGame(
                        board = selectedBoardId,
                        startingTime = TimeModeConfig.getBaseTimeSeconds(selectedTimeMode),
                        timeIncrement = selectedTimeIncrement,
                        level = selectedAiLevel
                    )

                    startActivity(Intent(requireContext(), GameActivity::class.java))
                }

                GameMode.RANKED -> {
                    ActiveGameManager.joinMatchmaking(
                        board = selectedBoardId,
                        timeBase = TimeModeConfig.getBaseTimeSeconds(selectedTimeMode),
                        timeIncrement = selectedTimeIncrement,
                        ranked = true
                    )

                    WaitingGameDialogFragment().show(parentFragmentManager, "MatchmakingWait")
                }

                GameMode.PUBLIC -> {
                    ActiveGameManager.joinMatchmaking(
                        board = selectedBoardId,
                        timeBase = TimeModeConfig.getBaseTimeSeconds(selectedTimeMode),
                        timeIncrement = selectedTimeIncrement,
                        ranked = false
                    )

                    WaitingGameDialogFragment().show(parentFragmentManager, "MatchmakingWait")
                }
            }
        }

        setupBoardPreview(view)
        setupSelectors(view)
        checkAiMode()

        rankBadgeView = view.findViewById(R.id.includeRankBadge)
        observeElo()
        return view
    }

    private fun checkAiMode() {
        if (currentMode == GameMode.BOT){
            view?.findViewById<View>(R.id.includeAiLevelSelector)?.visibility = View.VISIBLE
        }else{
            view?.findViewById<View>(R.id.includeAiLevelSelector)?.visibility = View.GONE
        }
    }

    /**
     * Configura el ComposeView para mostrar el tablero seleccionado.
     */
    private fun setupBoardPreview(view: View) {
        val boardContainer = view.findViewById<ViewGroup>(R.id.boardContainerHome)

        view.findViewById<ImageView?>(R.id.imgBoardPlaceholderHome)?.visibility = View.GONE
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
        val pieceSkin = CurrentUserManager.getMyCurrentPieceSkin()
        val boardSkin = CurrentUserManager.getMyCurrentBoardSkin()

        boardComposeView?.setContent {
            val board = Board(rows = 10, cols = 8)
            BoardParser.boardFromCSV(board, BoardLayouts.getCsvForBoard(selectedBoardName))
            HomeBoardPreview(
                board = board,
                pieceSkin = pieceSkin,
                boardSkin = boardSkin)
        }
    }

    private fun setupSelectors(view: View) {
        val LCRed  = ContextCompat.getColor(requireContext(), R.color.LCRed)
        val LCBlue = ContextCompat.getColor(requireContext(), R.color.LCBlue)
        val LCGreen = ContextCompat.getColor(requireContext(), R.color.LCGreen)

        // Selector de tablero
        val includeBoardSelector = view.findViewById<View>(R.id.includeBoardSelector)
        val txtBoardTitle = includeBoardSelector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgBoardIcon  = includeBoardSelector.findViewById<ImageView>(R.id.imgSelectorIcon)
        txtBoardTitle.text = selectedBoardName
        imgBoardIcon.setImageResource(R.drawable.ic_tablero)
        imgBoardIcon.setColorFilter(LCRed)
        includeBoardSelector.setOnClickListener {
            showBoardBottomSheet(LCRed, txtBoardTitle)
        }

        // Selector de modo de tiempo
        val includeTimeSelector = view.findViewById<View>(R.id.includeTimeSelector)
        val txtTimeTitle = includeTimeSelector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgTimeIcon  = includeTimeSelector.findViewById<ImageView>(R.id.imgSelectorIcon)
        txtTimeTitle.text = TimeModeConfig.getName(selectedTimeMode)
        imgTimeIcon.setImageResource(R.drawable.ic_tiempo)
        imgTimeIcon.setColorFilter(LCBlue)
        includeTimeSelector.setOnClickListener {
            showTimeModeBottomSheet(LCGreen, txtTimeTitle)
        }

        // Selector de incremento de tiempo
        val includeIncrementSelector = view.findViewById<View>(R.id.includeIncrementSelector)
        val txtIncrementTitle = includeIncrementSelector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgIncrementIcon  = includeIncrementSelector.findViewById<ImageView>(R.id.imgSelectorIcon)
        txtTimeIncrementTitle = txtIncrementTitle
        txtIncrementTitle.text = "+${selectedTimeIncrement}s"
        imgIncrementIcon.setImageResource(R.drawable.ic_time_increment)
        imgIncrementIcon.setColorFilter(LCBlue)
        includeIncrementSelector.setOnClickListener {
            showIncrementBottomSheet(LCBlue, txtIncrementTitle)
        }

        // Selector de nivel de IA
        val includeAiLevelSelector = view.findViewById<View>(R.id.includeAiLevelSelector)
        val txtAiLevelTitle = includeAiLevelSelector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgAiLevelIcon  = includeAiLevelSelector.findViewById<ImageView>(R.id.imgSelectorIcon)
        txtAiLevelTitle.text = "${selectedAiLevel}"
        imgAiLevelIcon.setImageResource(R.drawable.ic_ai_level)
        imgAiLevelIcon.setColorFilter(LCRed)
        includeAiLevelSelector.setOnClickListener {
            showAiLevelBottomSheet(LCRed, txtAiLevelTitle)
        }

    }

    private fun showAiLevelBottomSheet(color: Int, targetView: TextView) {
        val dialog = buildBottomSheet("Nivel de dificultad de la IA", color) { container, dlg ->
            val AiLevels = arrayOf("LVL 1", "LVL 2", "LVL 3")
            AiLevels.forEachIndexed { index, level ->
                addButton(container, dlg, level) {
                    selectedAiLevel = index + 1
                    targetView.text = level[4] + ""
                }
            }
        }
        dialog.show()
    }

    /**
     * BottomSheet específico para selección de tablero.
     * Al elegir un tablero actualiza la previsualización.
     */
    private fun showBoardBottomSheet(color: Int, targetView: TextView) {
        val dialog = buildBottomSheet("Seleccionar tablero", color) { container, dlg ->
            BoardLayouts.ALL_BOARD_NAMES.forEachIndexed { index, boardName ->
                addButton(container, dlg, boardName) {
                    selectedBoardName = boardName
                    selectedBoardId = index
                    targetView.text = boardName
                    renderBoard()
                }
            }
        }
        dialog.show()
    }

    private fun showTimeModeBottomSheet(color: Int, targetView: TextView) {
        val dialog = buildBottomSheet("Modo de tiempo", color) { container, dlg ->
            TimeMode.values().filter { it != TimeMode.CUSTOM }.forEach { mode ->
                addButton(container, dlg, TimeModeConfig.getName(mode)) {
                    if (selectedTimeMode != mode) {
                        selectedTimeMode = mode
                        selectedTimeIncrement = TimeModeConfig.getAllowedIncrements(mode).first()
                        txtTimeIncrementTitle?.text = "+${selectedTimeIncrement}s"
                    }
                    targetView.text = TimeModeConfig.getName(mode)

                    val profile = CurrentUserManager.getMyCurrentProfile()
                    val elo = when (mode) {
                        TimeMode.BLITZ    -> profile?.ratings?.blitz    ?: 0
                        TimeMode.RAPID    -> profile?.ratings?.rapid    ?: 0
                        TimeMode.CLASSIC  -> profile?.ratings?.classic  ?: 0
                        TimeMode.EXTENDED -> profile?.ratings?.extended ?: 0
                        else              -> 0
                    }

                    rankBadgeView?.findViewById<com.google.android.material.progressindicator.CircularProgressIndicator>(
                        R.id.rankProgressBar
                    )?.progress = 0

                    updateRankBadge(elo)
                }
            }
        }
        dialog.show()
    }

    private fun showIncrementBottomSheet(color: Int, targetView: TextView) {
        val dialog = buildBottomSheet("Incremento", color) { container, dlg ->
            TimeModeConfig.getAllowedIncrements(selectedTimeMode).forEach { inc ->
                addButton(container, dlg, "+${inc}s") {
                    selectedTimeIncrement = inc
                    targetView.text = "+${inc}s"
                }
            }
        }
        dialog.show()
    }

    private fun buildBottomSheet(
        titulo: String,
        colorTitulo: Int,
        fillOptions: (container: LinearLayout, dialog: BottomSheetDialog) -> Unit
    ): BottomSheetDialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.TemaBottomSheetTransparente)
        val dialogView = layoutInflater.inflate(R.layout.dialog_selector_desplegable, null)

        dialogView.findViewById<TextView>(R.id.txtDialogTitle).apply {
            text = titulo
            setTextColor(colorTitulo)
        }

        val container = dialogView.findViewById<LinearLayout>(R.id.layoutOptionsContainer)
        fillOptions(container, dialog)

        dialog.setContentView(dialogView)
        return dialog
    }

    private fun addButton(container: LinearLayout, dialog: BottomSheetDialog, label: String, onClick: () -> Unit) {
        val button = com.google.android.material.button.MaterialButton(requireContext()).apply {
            text = label
            setTextColor(ContextCompat.getColor(context, R.color.LCWhite))
            backgroundTintList = ContextCompat.getColorStateList(context, R.color.S2)
            cornerRadius = 36
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }

            setOnClickListener {
                onClick()
                dialog.dismiss()
            }
        }
        container.addView(button)
    }

    private fun iconFor(mode: GameMode): Int {
        return when (mode) {
            GameMode.BOT -> R.drawable.ic_ai_mode
            GameMode.RANKED -> R.drawable.ic_ranked_mode
            GameMode.PUBLIC -> R.drawable.ic_casual_mode
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

    private fun observeElo() {
        CurrentUserManager.myProfile.observe(viewLifecycleOwner) { profile ->
            val elo = when (selectedTimeMode) {
                TimeMode.BLITZ    -> profile?.ratings?.blitz    ?: 0
                TimeMode.RAPID    -> profile?.ratings?.rapid    ?: 0
                TimeMode.CLASSIC  -> profile?.ratings?.classic  ?: 0
                TimeMode.EXTENDED -> profile?.ratings?.extended ?: 0
                else              -> profile?.ratings?.blitz    ?: 0
            }
            updateRankBadge(elo)
        }
    }

    private fun updateRankBadge(elo: Int) {
        val badgeView = rankBadgeView ?: return
        val rank    = RankUtils.getRankInfo(elo)
        val percent = RankUtils.getProgressPercent(elo)
        val nextMin = RankUtils.RANK_TABLE.find { it.min > elo }?.min ?: elo

        badgeView.findViewById<TextView>(R.id.txtLeagueName).text =
            "${rank.name.uppercase()} · $elo / $nextMin"

        val progressBar = badgeView.findViewById<com.google.android.material.progressindicator.CircularProgressIndicator>(
            R.id.rankProgressBar
        )
        progressBar.progress = 0
        progressBar.postDelayed({ progressBar.setProgressCompat(percent, true) }, 50)

        badgeView.findViewById<ImageView>(R.id.rankBadgeIcon)
            .setImageResource(rank.drawableRes)
    }
}
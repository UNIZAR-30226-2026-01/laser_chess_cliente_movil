package com.gracehopper.laserchessapp.ui.game

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.google.gson.Gson
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.model.game.BoardLayouts
import com.gracehopper.laserchessapp.data.model.game.GameResume
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.gameLogic.board.BoardParser
import com.gracehopper.laserchessapp.gameLogic.move.CoordsConverter
import com.gracehopper.laserchessapp.gameLogic.move.MoveParser

class GameReplayActivity : AppCompatActivity() {

    private lateinit var boardM: Board
    private lateinit var game: GameResume

    private var movimientos: List<String> = listOf()
    private var moveIndex = 0

    private lateinit var boardView: ComposeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game_replay)

        val json = getSharedPreferences("app", Context.MODE_PRIVATE)
            .getString("historyGame", null)

        if (json == null) {
            Log.e("REPLAY", "JSON null")
            finish()
            return
        }

        try {
            game = Gson().fromJson(json, GameResume::class.java)
        } catch (e: Exception) {
            Log.e("REPLAY", "Error parseando JSON", e)
            finish()
            return
        }

        boardM = Board(10, 8)

        val csv = BoardLayouts.getCsvForBoard(game.board)
        BoardParser.boadFromCSV(boardM, csv)

        movimientos = game.movement_history
            ?.split(";")
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        moveIndex = 0

        setupUI()
        renderBoard()
    }

    private fun setupUI() {
        boardView = findViewById(R.id.boardReplay)

        val btnNext = findViewById<ImageButton>(R.id.btnNext)
        val btnPrev = findViewById<ImageButton>(R.id.btnPrev)

        btnNext.setOnClickListener {
            nextMove()
        }

        btnPrev.setOnClickListener {
            previousMove()
        }

        val btnStart = findViewById<ImageButton>(R.id.btnStart)
        val btnEnd = findViewById<ImageButton>(R.id.btnEnd)

        btnStart.setOnClickListener {
            goToStart()
        }

        btnEnd.setOnClickListener {
            goToEnd()
        }

    }

    /**
     * Render tablero
     */
    private fun renderBoard() {
        boardView.setContent {
            GameScreen(
                board = boardM,
                isRedPlayer = true,
                isMyTurn = false,
                onPieceSelected = {},
                onMove = { _, _ -> },
                clearSelectionTrigger = moveIndex,
                laserPath = emptyList(),
                renderCoordinates = true,
                pieceSkin = CurrentUserManager.getMyCurrentPieceSkin(),
                boardSkin = CurrentUserManager.getMyCurrentBoardSkin()
            )
        }
    }

    /**
     *
     */
    private fun applyStateMove(moveStr: String) {
        val move = MoveParser.parseMove(moveStr)

        val fromPos = CoordsConverter.notationToPosition(move.from)
        val piece = boardM.getPiece(fromPos.first, fromPos.second)

        when (move.type) {
            'T' -> {
                val toPos = CoordsConverter.notationToPosition(move.to!!)
                val pieceTo = boardM.getPiece(toPos.first, toPos.second)

                boardM.setPiece(toPos.first, toPos.second, piece)
                boardM.setPiece(fromPos.first, fromPos.second, pieceTo)
            }
            'R' -> piece?.rotateRight()
            'L' -> piece?.rotateLeft()
        }

        move.destroyed?.let {
            val destroyedPos = CoordsConverter.notationToPosition(it)
            boardM.setPiece(destroyedPos.first, destroyedPos.second, null)
        }
    }

    /**
     * Reconstrucción completa 
     */
    private fun rebuildBoard() {
        boardM.clear()
        val csv = BoardLayouts.getCsvForBoard(game.board)
        BoardParser.boadFromCSV(boardM, csv)

        for (i in 0 until moveIndex) {
            applyStateMove(movimientos[i])
        }

        renderBoard()
    }

    /**
     * Siguiente jugada
     */
    private fun nextMove() {
        if (moveIndex < movimientos.size) {
            moveIndex++
            rebuildBoard()
        }
    }

    /**
     * Jugada anterior
     */
    private fun previousMove() {
        if (moveIndex > 0) {
            moveIndex--
            rebuildBoard()
        }
    }

    private fun goToStart() {
        moveIndex = 0
        rebuildBoard()
    }

    private fun goToEnd() {
        moveIndex = movimientos.size
        rebuildBoard()
    }
}
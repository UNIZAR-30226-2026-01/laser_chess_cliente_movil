package com.gracehopper.laserchessapp.ui.game

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.ui.game.board.Board
import com.gracehopper.laserchessapp.ui.game.pieces.Deflector

class GameActivity : AppCompatActivity() {

    private val rows = 10
    private val cols = 8
    private lateinit var boardM: Board
    private var clearTrigger by mutableStateOf(0)
    private var selectedPos: Pair<Int, Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val board = findViewById<ComposeView>(R.id.board)
        val controls = findViewById<LinearLayout>(R.id.rotationButtons)
        val btnLeft = findViewById<ImageButton>(R.id.btnRotLeft)
        val btnRight = findViewById<ImageButton>(R.id.btnRotRight)

        val btnExit = findViewById<ImageButton>(R.id.btnExit)

        boardM = Board(rows, cols)

        boardM.setPiece(2,3, Deflector(true))

        board.setContent {
            GameScreen(
                board = boardM,
                onPieceSelected = { pos ->
                    selectedPos = pos
                    controls.visibility = if (pos != null) View.VISIBLE else View.GONE
                },
                onMove = { from, to -> movePiece(from, to) },
                clearSelectionTrigger = clearTrigger
            )
        }

        btnExit.setOnClickListener {
            finish()
        }

        btnLeft.setOnClickListener {
            selectedPos?.let { (r, c) ->
                val piece = boardM.getPiece(r, c)

                piece?.rotation = piece?.rotation?.minus(90) ?: 0

                selectedPos = null
                clearTrigger++
                controls.visibility = View.GONE
            }
        }

        btnRight.setOnClickListener {
            selectedPos?.let { (r, c) ->
                val piece = boardM.getPiece(r, c)

                piece?.rotation = piece?.rotation?.plus(90) ?: 0

                selectedPos = null
                clearTrigger++
                controls.visibility = View.GONE
            }
        }

    }

    private fun movePiece(from: Pair<Int, Int>, to: Pair<Int, Int>) {

        val (r1, c1) = from
        val (r2, c2) = to

        val piece = boardM.getPiece(r1, c1)

        boardM.setPiece(r2, c2, piece)
        boardM.setPiece(r1, c1, null)

        selectedPos = null
        clearTrigger++
    }


}
package com.gracehopper.laserchessapp.ui.game

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.ui.game.board.Board
import com.gracehopper.laserchessapp.ui.game.pieces.Deflector

class GameActivity : AppCompatActivity() {

    private val rows = 10
    private val cols = 8
    private lateinit var boardM: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val board = findViewById<ComposeView>(R.id.board)

        boardM = Board(rows, cols)

        boardM.setPiece(2,3, Deflector(true))

        board.setContent {
            GameScreen(board = boardM)
        }

        val btnExit = findViewById<ImageButton>(R.id.btnExit)

        btnExit.setOnClickListener {
            finish()
        }
    }
}
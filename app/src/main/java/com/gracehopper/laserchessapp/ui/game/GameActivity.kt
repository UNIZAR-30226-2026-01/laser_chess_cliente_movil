package com.gracehopper.laserchessapp.ui.game

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.ui.game.board.Board
import com.gracehopper.laserchessapp.ui.game.pieces.Deflector

class GameActivity : AppCompatActivity() {

    private val rows = 10
    private val cols = 8

    private lateinit var boardM: Board
    private val cellsM = mutableListOf<FrameLayout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val board = findViewById<GridLayout>(R.id.boardGrid)

        boardM = Board(rows, cols)

        boardM.setPiece(2,3, Deflector(true))

        createBoard(board)

        val btnExit = findViewById<ImageButton>(R.id.btnExit)

        btnExit.setOnClickListener {
            finish()
        }
    }

    private fun createBoard(board: GridLayout) {

        for (row in 0 until rows) {
            for (col in 0 until cols) {

                val cell = FrameLayout(this)

                val cParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0

                    rowSpec = GridLayout.spec(row, 1, GridLayout.FILL, 1f)
                    columnSpec = GridLayout.spec(col, 1, GridLayout.FILL, 1f)
                }

                cell.layoutParams = cParams

                cell.setBackgroundResource(R.drawable.cell)

                cell.tag = Pair(row, col)
                cellsM.add(cell)

                cell.setOnClickListener {
                    val (r, c) = cell.tag as Pair<Int, Int>

                    val piece = boardM.getPiece(r,c)

                    if(piece != null){
                        val moves = piece.getValidMoves(r,c,boardM)
                    }
                }

                board.addView(cell)

            }
        }
    }

    private fun addPiece(board: GridLayout, row: Int, col: Int) {

        val idx = row * cols + col
        val cell = board.getChildAt(idx) as FrameLayout

        val piece = ImageView(this).apply {
            setImageResource(R.drawable.piece)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        cell.addView(piece)
    }


}
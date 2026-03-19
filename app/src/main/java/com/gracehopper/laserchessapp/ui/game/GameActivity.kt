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

    private var selectedPos: Pair<Int, Int>? = null
    private lateinit var boardM: Board
    private val cellsM = mutableListOf<FrameLayout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val board = findViewById<GridLayout>(R.id.boardGrid)

        boardM = Board(rows, cols)

        boardM.setPiece(2,3, Deflector(true))

        createBoard(board)
        drawPieces(board)

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

                    val selected = selectedPos

                    if (selected == null) {
                        val piece = boardM.getPiece(r, c)

                        if (piece != null) {
                            selectedPos = Pair(r, c)

                            clearHighlights()
                            val moves = piece.getValidMoves(r,c,boardM)
                            highlightMoves(moves)
                        }
                    } else {
                        val (r2,c2) = selected
                        val piece = boardM.getPiece(r2, c2)

                        if (piece != null) {
                            val moves = piece.getValidMoves(r2,c2,boardM)

                            if (moves.contains(Pair(r,c))) {
                                movePiece(r2,c2,r,c)
                            }
                        }

                        selectedPos = null
                        clearHighlights()
                    }
                }

                board.addView(cell)

            }
        }
    }

    private fun drawPieces(board: GridLayout) {
        for (row in 0 until rows) {
            for (col in 0 until cols) {

                val piece = boardM.getPiece(row,col)

                if (piece != null) {

                    val idx = row * cols + col
                    val cell = board.getChildAt(idx) as FrameLayout

                    val image = ImageView(this).apply {
                        setImageResource(piece.getImageRes())
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    }

                    cell.addView(image)

                }
            }
        }
    }

    private fun highlightMoves(moves: List<Pair<Int, Int>>) {

        for ((row,col) in moves) {
            val idx = row * cols + col
            val cell = cellsM[idx]

            cell.setBackgroundResource(R.drawable.cell_move)
        }
    }

    private fun clearHighlights() {
        for (cell in cellsM) {
            cell.setBackgroundResource(R.drawable.cell)
        }
    }

    private fun movePiece(originRow: Int, originCol: Int, toRow: Int, toCol: Int) {

        val piece = boardM.getPiece(originRow,originCol)

        boardM.setPiece(toRow,toCol,piece)
        boardM.setPiece(originRow,originCol,null)

        redrawBoard()
    }

    private fun redrawBoard() {
        val board = findViewById<GridLayout>(R.id.boardGrid)

        for (cell in cellsM) {
            cell.removeAllViews()
        }

        drawPieces(board)
    }

}
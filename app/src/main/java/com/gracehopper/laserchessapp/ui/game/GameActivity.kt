package com.gracehopper.laserchessapp.ui.game

import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.gracehopper.laserchessapp.R

class GameActivity : AppCompatActivity() {

    private val rows = 10
    private val cols = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val board = findViewById<GridLayout>(R.id.boardGrid)

        createBoard(board)

        val btnExit = findViewById<ImageButton>(R.id.btnExit)

        btnExit.setOnClickListener {
            finish()
        }
    }

    private fun createBoard(board: GridLayout) {

        for (row in 0 until rows) {
            for (col in 0 until cols) {

                val cell = View(this)

                val cParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0

                    rowSpec = GridLayout.spec(row, 1, GridLayout.FILL, 1f)
                    columnSpec = GridLayout.spec(col, 1, GridLayout.FILL, 1f)
                }

                cell.layoutParams = cParams

                cell.setBackgroundResource(R.drawable.cell)

                board.addView(cell)

            }
        }
    }


}
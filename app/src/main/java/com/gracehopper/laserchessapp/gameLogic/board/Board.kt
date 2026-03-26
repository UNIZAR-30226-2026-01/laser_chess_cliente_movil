package com.gracehopper.laserchessapp.gameLogic.board

import androidx.compose.runtime.mutableStateListOf
import com.gracehopper.laserchessapp.gameLogic.pieces.Piece

class Board(val rows: Int, val cols: Int) {

    private val grid = List(rows) {
        mutableStateListOf<Piece?>().apply {
            repeat(cols) { add(null) }
        }
    }

    fun getPiece(row: Int, col: Int): Piece? {
        return grid[row][col]
    }

    fun setPiece(row: Int, col: Int, piece: Piece?) {
        grid[row][col] = piece
    }
}
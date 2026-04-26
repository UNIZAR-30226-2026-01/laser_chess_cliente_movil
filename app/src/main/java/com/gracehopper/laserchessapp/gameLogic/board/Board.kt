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

    fun clear() {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                grid[row][col] = null
            }
        }
    }

    fun isForbiddenCell(row: Int, col: Int, isRedPiece: Boolean): Boolean {
        return if (isRedPiece) {
            row == 0 || (row == 8 && (col == 0 || col == 7))
        } else {
            row == 9 || (row == 1 && (col == 0 || col == 7))
        }
    }
}
package com.gracehopper.laserchessapp.ui.game.board

import com.gracehopper.laserchessapp.ui.game.pieces.Piece

class Board(val rows: Int, val cols: Int) {

    private val grid = Array(rows) { Array<Piece?>(cols) { null } }

    fun getPiece(row: Int, col: Int): Piece? {
        return grid[row][col]
    }

    fun setPiece(row: Int, col: Int, piece: Piece?) {
        grid[row][col] = piece
    }
}
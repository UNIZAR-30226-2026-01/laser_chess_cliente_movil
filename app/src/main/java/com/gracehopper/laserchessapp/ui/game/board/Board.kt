package com.gracehopper.laserchessapp.ui.game.board

import com.gracehopper.laserchessapp.ui.game.pieces.Piece

class Board {
    private val _pieces = mutableListOf<Piece>()
    val pieces get() = _pieces.toList()


}
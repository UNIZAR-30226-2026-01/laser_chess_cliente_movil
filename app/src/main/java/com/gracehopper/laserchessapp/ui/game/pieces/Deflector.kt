package com.gracehopper.laserchessapp.ui.game.pieces;

import androidx.compose.ui.unit.IntOffset;

class Deflector(
    override val color: Piece.Color,
    override var position: IntOffset,
): Piece {

    override fun getAvailableMoves(pieces: List<Piece>): Set<IntOffset> {
        val moves = mutableSetOf<IntOffset>()
        // TODO
        return moves
    }
}


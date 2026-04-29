package com.gracehopper.laserchessapp.ui.home

import androidx.compose.runtime.Composable
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.ui.game.GameScreen

/**
 * Vista previa del tablero en Home.
 */
@Composable
fun HomeBoardPreview(board: Board) {
    GameScreen(
        board = board,
        isRedPlayer = false,
        isMyTurn = false,
        onPieceSelected = {},
        onMove = { _, _ -> },
        clearSelectionTrigger = 0,
        laserPath = emptyList()
    )
}
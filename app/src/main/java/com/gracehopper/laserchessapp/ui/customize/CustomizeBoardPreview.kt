package com.gracehopper.laserchessapp.ui.customize

import androidx.compose.runtime.Composable
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.ui.game.GameScreen

/**
 * Vista previa del tablero en Customize.
 */
@Composable
fun CustomizeBoardPreview(
    board: Board,
    pieceSkin: Int,
    boardSkin: Int) {
    GameScreen(
        board = board,
        isRedPlayer = true,
        isMyTurn = false,
        renderCoordinates = false,
        onPieceSelected = {},
        onMove = { _, _ -> },
        clearSelectionTrigger = 0,
        laserPath = emptyList(),
        pieceSkin = pieceSkin,
        boardSkin = boardSkin
    )
}
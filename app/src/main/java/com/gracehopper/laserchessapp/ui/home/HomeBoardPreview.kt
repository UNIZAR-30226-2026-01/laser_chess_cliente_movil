package com.gracehopper.laserchessapp.ui.home

import androidx.compose.runtime.Composable
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.ui.game.GameScreen

/**
 * Vista previa del tablero en Home.
 */
@Composable
fun HomeBoardPreview(
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
        myPieceSkin = pieceSkin,
        myBoardSkin = boardSkin
    )
}
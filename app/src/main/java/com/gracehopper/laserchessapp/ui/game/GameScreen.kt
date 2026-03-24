package com.gracehopper.laserchessapp.ui.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gracehopper.laserchessapp.ui.game.board.Board

@Composable

fun GameScreen (board: Board, onPieceSelected: (Pair<Int, Int>?) -> Unit, onMove: (Pair<Int, Int>, Pair<Int, Int>) -> Unit, clearSelectionTrigger: Int){
    var highlightedMoves by remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }
    var selectedPos by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    LaunchedEffect(clearSelectionTrigger) {             // Limpiar cuando se active el trigger
        selectedPos = null
        highlightedMoves = emptyList()
    }

    Column {
        for (row in 0 until 10) {
            Row {
                for (col in 0 until 8) {

                    val piece = board.getPiece(row, col)
                    val isHighlighted = highlightedMoves.contains(Pair(row,col))

                    // Casilla
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White)
                            .border(1.dp, Color.Black)
                            .padding(1.dp)
                            .clickable{
                                val selected = selectedPos
                                val piece = board.getPiece(row, col)

                                if (selected == null) {             // Primer click
                                    if (piece != null) {
                                        selectedPos = Pair(row, col)
                                        highlightedMoves = piece.getValidMoves(row, col, board)

                                        onPieceSelected(selectedPos)
                                    }

                                } else {            // Segundo click (mover pieza)
                                    val (r2, c2) = selected
                                    val selectedPiece = board.getPiece(r2, c2)

                                    if (selectedPiece != null) {

                                        if (highlightedMoves.contains(Pair(row, col))) {            // mov. valido

                                            onMove(Pair(r2, c2), Pair(row, col))
                                        }
                                    }

                                    selectedPos = null
                                    highlightedMoves = emptyList()
                                    onPieceSelected(null)
                                }
                            }, contentAlignment = Alignment.Center
                    ) {

                        if (isHighlighted) {            // casilla de movimiento posible
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(Color(0xFFFF9800), shape = CircleShape)
                            )
                        }

                        if (piece != null) {            // Si hay una pieza en la casilla

                            val rotation by animateFloatAsState(
                                targetValue = piece.rotation.toFloat(),
                                animationSpec = tween(200)
                            )

                            Image(
                                painter = painterResource(id = piece.getImageRes()),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        rotationZ = rotation
                                    }
                            )
                        }
                    }
                }
            }
        }
    }

}
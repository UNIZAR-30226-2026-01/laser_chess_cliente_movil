package com.gracehopper.laserchessapp.ui.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gracehopper.laserchessapp.gameLogic.board.Board

@Composable

fun GameScreen (
    board: Board,
    isRedPlayer: Boolean,
    isMyTurn: Boolean,
    onPieceSelected: (Pair<Int, Int>?) -> Unit,
    onMove: (Pair<Int, Int>, Pair<Int, Int>) -> Unit,
    clearSelectionTrigger: Int){
    var highlightedMoves by remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }
    var selectedPos by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    LaunchedEffect(clearSelectionTrigger) {             // Limpiar cuando se active el trigger
        selectedPos = null
        highlightedMoves = emptyList()
    }

    val letters = listOf<Char>('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j')
    val numbers = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 8)

    val rowRange = if (isRedPlayer) (0 until 10) else (9 downTo 0)
    val colRange = if(isRedPlayer) (0 until 8) else (7 downTo 0)

    val visibleLetters = if (isRedPlayer) letters else letters.reversed()
    val visibleNumbers = if (isRedPlayer) numbers else numbers.reversed()

    Column {

        // Numeros de celda arriba (contorno)
        Row {
            Spacer(modifier = Modifier.weight(1f))
            for (num in visibleNumbers) {
                Box(
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = num.toString())
                }
            }
        }


        for ((rowIdx, row) in rowRange.withIndex()) {
            Row {

                // Letras del contorno
                Box(
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = visibleLetters[rowIdx].toString())
                }

                for (col in colRange) {

                    val piece = board.getPiece(row, col)
                    val isHighlighted = highlightedMoves.contains(Pair(row,col))

                    // Casilla
                    key(piece ?: "$row$col") {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(Color.White)
                                .border(1.dp, Color.Black)
                                .clickable{
                                    val selected = selectedPos
                                    val piece = board.getPiece(row, col)

                                    if (selected == null) {             // Primer click
                                        if (piece != null && piece.isRed == isRedPlayer && isMyTurn) {
                                            selectedPos = Pair(row, col)
                                            highlightedMoves = piece.getValidMoves(row, col, board)

                                            onPieceSelected(selectedPos)
                                        }

                                    } else {            // Segundo click (mover pieza)
                                        val (r2, c2) = selected
                                        val selectedPiece = board.getPiece(r2, c2)

                                        if (selectedPiece != null) {

                                            if (highlightedMoves.contains(Pair(row, col)) && selectedPiece.isRed == isRedPlayer && isMyTurn) {            // mov. valido

                                                onMove(Pair(r2, c2), Pair(row, col))
                                            }
                                        }

                                        selectedPos = null
                                        highlightedMoves = emptyList()
                                        onPieceSelected(null)
                                    }
                                }, contentAlignment = Alignment.Center
                        ) {
                            if (piece != null) {            // Si hay una pieza en la casilla

                                val rotation by animateFloatAsState(
                                    targetValue = piece.rotation.toFloat(),
                                    animationSpec = tween(200)
                                )

                                Image(
                                    painter = painterResource(id = piece.getImageRes(isRedPlayer)),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer {
                                            rotationZ = rotation
                                        }
                                )
                            }

                            if (isHighlighted) {            // casilla de movimiento posible
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(Color(0xFFFF9800), shape = CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}
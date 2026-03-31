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

    val recomposeTrigger = clearSelectionTrigger

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
                    key(row, col) {
                        val piece by remember(board, row, col, recomposeTrigger) {
                            derivedStateOf { board.getPiece(row, col) }
                        }
                        val isHighlighted = highlightedMoves.contains(Pair(row,col))

                        // Casilla
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(getCellColor(row, col, isRedPlayer))
                                .border(1.dp, Color.Black)
                                .clickable{
                                    val selected = selectedPos
                                    val clickedPiece = board.getPiece(row, col)

                                    if (selected == null) {             // Primer click
                                        if (clickedPiece != null && clickedPiece.isRed == isRedPlayer && isMyTurn) {
                                            selectedPos = Pair(row, col)
                                            highlightedMoves = clickedPiece.getValidMoves(row, col, board)

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
                            piece?.let { p ->
                                key(p) {
                                    val visualRotation =
                                        if (isRedPlayer) p.rotation + 180 else p.rotation

                                    val rotation by animateFloatAsState(
                                        targetValue = visualRotation.toFloat(),
                                        animationSpec = tween(200)
                                    )

                                    Image(
                                        painter = painterResource(id = p.getImageRes(isRedPlayer)),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer {
                                                rotationZ = rotation
                                            }
                                    )
                                }
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

fun getCellColor(row: Int, col: Int, isRedPlayer: Boolean): Color {

    return if (isRedPlayer) {
        when {
            row == 0 -> Color(0xFFFFCDD2) // fila a roja
            row == 8 && (col == 0 || col == 7) -> Color(0xFFFFCDD2) // i1 i8 rojas

            row == 9 -> Color(0xFFBBDEFB) // fila j azul
            row == 1 && (col == 0 || col == 7) -> Color(0xFFBBDEFB) // b1 b8 azul

            else -> Color.White
        }
    } else {
        // Soy azul interno
        when {
            row == 9 -> Color(0xFFFFCDD2) // fila j roja
            row == 1 && (col == 0 || col == 7) -> Color(0xFFFFCDD2) // b1 b8 rojas

            row == 0 -> Color(0xFFBBDEFB) // fila a azul
            row == 8 && (col == 0 || col == 7) -> Color(0xFFBBDEFB) // i1 i8 azul

            else -> Color.White
        }
    }
}
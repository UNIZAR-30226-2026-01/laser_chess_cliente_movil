package com.gracehopper.laserchessapp.ui.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.gracehopper.laserchessapp.gameLogic.board.Board

/**
 * Composable que representa el tablero de juego.
 *
 * Se encarga de:
 * - Dibujar el tablero y las piezas
 * - Gestionar la interacción del usuario (selección y movimiento)
 * - Mostrar movimientos válidos
 * - Renderizar la trayectoria del láser
 */
@Composable

fun GameScreen(
    board: Board,
    isRedPlayer: Boolean,
    isMyTurn: Boolean,
    renderCoordinates: Boolean,
    onPieceSelected: (Pair<Int, Int>?) -> Unit,
    onMove: (Pair<Int, Int>, Pair<Int, Int>) -> Unit,
    clearSelectionTrigger: Int,
    laserPath: List<Pair<Int, Int>>
) {
    var highlightedMoves by remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }
    var selectedPos by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val recomposeTrigger = clearSelectionTrigger

    /**
     * Limpiar selección cuando cambia el trigger
     */
    LaunchedEffect(clearSelectionTrigger) {             // Limpiar cuando se active el trigger
        selectedPos = null
        highlightedMoves = emptyList()
    }

    /**
     * Etiquetas del tablero
     */
    val letters = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j')
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8)

    /**
     * Orientación según jugador
     */
    val rowRange = if (isRedPlayer) (0 until 10) else (9 downTo 0)
    val colRange = if (isRedPlayer) (0 until 8) else (7 downTo 0)

    val visibleLetters = if (isRedPlayer) letters else letters.reversed()
    val visibleNumbers = if (isRedPlayer) numbers else numbers.reversed()

    Column(modifier = Modifier.fillMaxWidth()) {

        /**
         * Números superiores
         */
        if(renderCoordinates){
            Row {
                Spacer(modifier = Modifier.weight(1f))
                for (num in visibleNumbers) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = num.toString())
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {

            /**
             * Letras laterales
             */
            if (renderCoordinates) {
                Column(modifier = Modifier.weight(1f)) {
                    for (letter in visibleLetters) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = letter.toString(), color = Color.White)
                        }
                    }
                }
            }


        /**
         * Render del tablero
         */
            Column(
                //Marco alrededor del tablero de color S3
                modifier = Modifier
                    .weight(8f)
                    .border(BorderStroke(2.dp, Color(0xFF3B2865)))
            ) {
                for ((rowIdx, row) in rowRange.withIndex()) {
                    Row(modifier = Modifier.fillMaxWidth()) {

                        for (col in colRange) {
                            key(row, col) {
                                val piece = board.getPiece(row, col)
                                val isHighlighted = highlightedMoves.contains(Pair(row, col))

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .background(getCellColor(row, col, isRedPlayer))
                                        .border(getBorder(row, col, isRedPlayer))
                                        .clickable {
                                            val selected = selectedPos
                                            val clickedPiece = board.getPiece(row, col)

                                            // cuando seleccionas una pieza
                                            if (selected == null) {
                                                if (clickedPiece != null && clickedPiece.isRed == isRedPlayer && isMyTurn) {
                                                    selectedPos = Pair(row, col)
                                                    highlightedMoves = clickedPiece.getValidMoves(row, col, board)
                                                    onPieceSelected(selectedPos)
                                                }
                                            } else { //logica de selección de movimiento
                                                val (r2, c2) = selected
                                                val selectedPiece = board.getPiece(r2, c2)

                                                if (selectedPiece != null) {
                                                    if (highlightedMoves.contains(Pair(row, col)) && selectedPiece.isRed == isRedPlayer && isMyTurn) {
                                                        onMove(Pair(r2, c2), Pair(row, col))
                                                    }
                                                }
                                                selectedPos = null
                                                highlightedMoves = emptyList()
                                                onPieceSelected(null)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    piece?.let { p ->
                                        key(p) {
                                            val visualRotation = if (isRedPlayer) p.rotation + 180 else p.rotation
                                            val rotation by animateFloatAsState(targetValue = visualRotation.toFloat(), animationSpec = tween(200))
                                            Image(
                                                painter = painterResource(id = p.getImageRes(isRedPlayer)),
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize().graphicsLayer { rotationZ = rotation }
                                            )
                                        }
                                    }

                                    if (isHighlighted) {
                                        Box(modifier = Modifier.size(16.dp).background(Color(0xFFFF9800), shape = CircleShape))
                                    }

                                    val isLaser = laserPath.contains(Pair(row, col))
                                    if (isLaser) {
                                        Box(modifier = Modifier.fillMaxSize().background(Color.Red.copy(alpha = 0.4f)))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getCellColor(row: Int, col: Int, isRedPlayer: Boolean): Color {
    val S3 = Color(0xFF3B2865)
    val S1 = Color(0xFF1A122B)
    val LCRed_D = Color(0xFF86103C)
    val LCBlue_D = Color(0xFF1B418A)
    return if (isRedPlayer) {
        when {
            row == 0 -> LCRed_D// fila a roja
            row == 8 && (col == 0 || col == 7) -> LCRed_D // i1 i8 rojas

            row == 9 -> LCBlue_D  // fila j azul
            row == 1 && (col == 0 || col == 7) -> LCBlue_D // b1 b8 azul

            row % 2 == 0 && col % 2 == 0 || row % 2 != 0 && col % 2 != 0  -> S3

            else -> S1
        }
    } else {
        // Soy azul interno
        when {
            row == 9 -> LCRed_D // fila j roja
            row == 1 && (col == 0 || col == 7) -> LCRed_D // b1 b8 rojas

            row == 0 -> LCBlue_D // fila a azul
            row == 8 && (col == 0 || col == 7) -> LCBlue_D // i1 i8 azul
            row % 2 == 0 && col % 2 == 0 || row % 2 != 0 && col % 2 != 0  -> S3
            else -> S1
        }
    }
}

fun getBorder(row: Int, col: Int, isRedPlayer: Boolean): BorderStroke {
    val S3 = Color(0xFF3B2865)
    val redBorder = BorderStroke(1.dp, S3)
    val blueBorder = BorderStroke(1.dp, S3)
    val noBorderStroke = BorderStroke(0.dp, S3)
    return if (isRedPlayer) {
        when {
            row == 0 -> redBorder// fila a roja
            row == 8 && (col == 0 || col == 7) -> redBorder // i1 i8 rojas

            row == 9 -> blueBorder  // fila j azul
            row == 1 && (col == 0 || col == 7) -> blueBorder // b1 b8 azul

            else -> noBorderStroke
        }
    } else {
        // Soy azul interno
        when {
            row == 9 -> redBorder // fila j roja
            row == 1 && (col == 0 || col == 7) -> redBorder // b1 b8 rojas

            row == 0 -> blueBorder // fila a azul
            row == 8 && (col == 0 || col == 7) -> blueBorder // i1 i8 azul

            else -> noBorderStroke
        }
    }
}
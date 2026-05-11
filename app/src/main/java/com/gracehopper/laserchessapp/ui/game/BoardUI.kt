package com.gracehopper.laserchessapp.ui.game

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.ui.utils.ItemUtils

@Composable
fun GameScreen(
    board: Board,
    isRedPlayer: Boolean,
    isMyTurn: Boolean,
    renderCoordinates: Boolean,
    onPieceSelected: (Pair<Int, Int>?) -> Unit,
    onMove: (Pair<Int, Int>, Pair<Int, Int>) -> Unit,
    clearSelectionTrigger: Int,
    laserPath: List<Pair<Int, Int>>,
    laserIsRed: Boolean = false,
    myPieceSkin: Int,
    myBoardSkin: Int,
    opponentPieceSkin: Int = 1,
    opponentBoardSkin: Int = 4
) {
    var highlightedMoves by remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }
    var selectedPos by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    LaunchedEffect(clearSelectionTrigger) {
        selectedPos = null
        highlightedMoves = emptyList()
    }

    val letters = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j')
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8)

    val rowRange = if (isRedPlayer) (0 until 10) else (9 downTo 0)
    val colRange = if (isRedPlayer) (0 until 8) else (7 downTo 0)

    val visibleLetters = if (isRedPlayer) letters else letters.reversed()
    val visibleNumbers = if (isRedPlayer) numbers else numbers.reversed()

    // Colores del láser
    // Para el jugador local, sus piezas son azules visualmente, por tanto su láser es azul (laserIsRed == false)
    // Para el rival, sus piezas son rojas visualmente, por tanto su láser es rojo (laserIsRed == true)
    // Comparamos el color del láser con el bando interno (isRedPlayer) para determinar la visualización
    val isVisualRedLaser = (laserIsRed != isRedPlayer)

    val laserCore = if (isVisualRedLaser) Color(0xFFFF3333) else Color(0xFF3388FF)
    val laserStrong = if (isVisualRedLaser) Color(0xCCFF3C3C) else Color(0xCC3282FF)
    val laserMid = if (isVisualRedLaser) Color(0x80FF1E1E) else Color(0x801E64FF)
    val laserSoft = if (isVisualRedLaser) Color(0x40C80000) else Color(0x40003CC8)

    Column(modifier = Modifier.fillMaxWidth()) {

        /**
         * Números superiores
         */
        if (renderCoordinates) {
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
                            Text(text = letter.toString(), color = colorResource(R.color.LCWhite))
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(8f)) {

                // Tablero
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(2.dp, Color(0xFF3B2865)))
                ) {
                    for ((_, row) in rowRange.withIndex()) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (col in colRange) {
                                key(row, col) {
                                    val piece = board.getPiece(row, col)
                                    val isHighlighted = highlightedMoves.contains(Pair(row, col))

                                    val cellModifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .background(getCellColor(row, col, isRedPlayer))

                                    val finalModifier = if (isMyTurn) {
                                        cellModifier.clickable {
                                            val selected = selectedPos
                                            val clickedPiece = board.getPiece(row, col)

                                            if (selected == null) {
                                                if (clickedPiece != null && clickedPiece.isRed == isRedPlayer) {
                                                    selectedPos = Pair(row, col)
                                                    highlightedMoves =
                                                        clickedPiece.getValidMoves(
                                                            row,
                                                            col,
                                                            board
                                                        )
                                                    onPieceSelected(selectedPos)
                                                }
                                            } else {
                                                val (r2, c2) = selected
                                                val selectedPiece = board.getPiece(r2, c2)
                                                if (selectedPiece != null) {
                                                    if (highlightedMoves.contains(
                                                            Pair(
                                                                row,
                                                                col
                                                            )
                                                        ) && selectedPiece.isRed == isRedPlayer
                                                    ) {
                                                        onMove(Pair(r2, c2), Pair(row, col))
                                                    }
                                                }
                                                selectedPos = null
                                                highlightedMoves = emptyList()
                                                onPieceSelected(null)
                                            }
                                        }
                                    } else {
                                        cellModifier
                                    }

                                    Box(
                                        modifier = finalModifier,
                                        contentAlignment = Alignment.Center
                                    ) {

                                        val isRedRune =
                                            row == 0 || (row == 8 && (col == 0 || col == 7))

                                        val isBlueRune =
                                            row == 9 || (row == 1 && (col == 0 || col == 7))

                                        if (isRedRune || isBlueRune) {

                                            val runeRes = when {

                                                isRedRune -> ItemUtils.getRedRune(myBoardSkin)

                                                else -> ItemUtils.getBlueRune(myBoardSkin)
                                            }

                                            Image(
                                                painter = painterResource(runeRes),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxSize(0.72f)
                                                    .alpha(0.28f)
                                            )
                                        }

                                        piece?.let { p ->
                                            key(p, myPieceSkin, opponentPieceSkin) {
                                                val visualRotation = if (isRedPlayer) p.rotation
                                                else (p.rotation + 180) % 360
                                                val rotation by animateFloatAsState(
                                                    targetValue = visualRotation.toFloat(),
                                                    animationSpec = tween(200)
                                                )
                                                Image(
                                                    painter = painterResource(
                                                        id = p.getImageRes(
                                                            isRedPlayer,
                                                            myPieceSkin,
                                                            opponentPieceSkin
                                                        )
                                                    ),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .graphicsLayer { rotationZ = rotation }
                                                )
                                            }
                                        }

                                        if (isHighlighted) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(
                                                        Color(0xFFFF9800),
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Canvas
                if (laserPath.size >= 2) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val cellW = size.width / 8f
                        val cellH = size.height / 10f

                        fun cellCenter(row: Int, col: Int): Offset {
                            val displayCol = if (isRedPlayer) col else (7 - col)
                            val displayRow = if (isRedPlayer) row else (9 - row)
                            return Offset(
                                x = displayCol * cellW + cellW / 2f,
                                y = displayRow * cellH + cellH / 2f
                            )
                        }

                        val points = laserPath.map { (r, c) -> cellCenter(r, c) }

                        drawIntoCanvas { canvas ->
                            for (i in 0 until points.size - 1) {
                                val p1 = points[i]
                                val p2 = points[i + 1]

                                // Halo exterior con blur real
                                canvas.drawLine(p1, p2, Paint().apply {
                                    color = laserSoft
                                    strokeWidth = 2.dp.toPx()
                                    strokeCap = StrokeCap.Round
                                    asFrameworkPaint().maskFilter =
                                        android.graphics.BlurMaskFilter(
                                            20.dp.toPx(),
                                            android.graphics.BlurMaskFilter.Blur.NORMAL
                                        )
                                })
                                // Halo medio
                                canvas.drawLine(p1, p2, Paint().apply {
                                    color = laserMid
                                    strokeWidth = 2.dp.toPx()
                                    strokeCap = StrokeCap.Round
                                    asFrameworkPaint().maskFilter =
                                        android.graphics.BlurMaskFilter(
                                            10.dp.toPx(),
                                            android.graphics.BlurMaskFilter.Blur.NORMAL
                                        )
                                })
                                // Halo fuerte
                                canvas.drawLine(p1, p2, Paint().apply {
                                    color = laserStrong
                                    strokeWidth = 2.dp.toPx()
                                    strokeCap = StrokeCap.Round
                                    asFrameworkPaint().maskFilter =
                                        android.graphics.BlurMaskFilter(
                                            4.dp.toPx(),
                                            android.graphics.BlurMaskFilter.Blur.NORMAL
                                        )
                                })
                                // Núcleo sólido
                                canvas.drawLine(p1, p2, Paint().apply {
                                    color = laserCore
                                    strokeWidth = 2.dp.toPx()
                                    strokeCap = StrokeCap.Round
                                })
                                // Línea blanca central
                                canvas.drawLine(p1, p2, Paint().apply {
                                    color = Color(0xE6FFDCDC)
                                    strokeWidth = 0.8.dp.toPx()
                                    strokeCap = StrokeCap.Round
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getCellColor(row: Int, col: Int, isRedPlayer: Boolean): Color {
    val S1 = Color(0xFF1A122B)
    val S3 = Color(0xFF3B2865)

    return if ((row + col) % 2 == 0) S3 else S1
}
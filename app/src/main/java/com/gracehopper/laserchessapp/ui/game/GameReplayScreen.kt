package com.gracehopper.laserchessapp.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.gameLogic.pieces.Piece
import com.gracehopper.laserchessapp.gameLogic.pieces.PieceType

/**
 * Pantalla de repetición de partida.
 *
 * Muestra el estado del tablero en un momento determinado de la partida,
 * junto con la información de los jugadores y controles de navegación.
 */

private val ReplayBackground = Color(0xFF0F0B1E)
private val CardBackground = Color(0xFF1A162D)
private val BluePlayerColor = Color(0xFF2196F3)
private val RedPlayerColor = Color(0xFFF44336)
private val BoardDarkCell = Color(0xFF1E1433)
private val BoardLightCell = Color(0xFF2D1D4D)
private val ControlsBackground = Color(0xFF1A162D).copy(alpha = 0.8f)

@Composable
fun GameReplayScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    player1Time: String = "9:59",
    player2Time: String = "10:00",
    board: Board,
    onStepForward: () -> Unit = {},
    onStepBackward: () -> Unit = {},
    onFastForward: () -> Unit = {},
    onFastBackward: () -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = ReplayBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        contentDescription = stringResource(id = R.string.replay_back),
                        tint = Color.White
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PlayerInfoCard(
                    modifier = Modifier.weight(1f),
                    time = player1Time,
                    color = BluePlayerColor,
                    isLeft = true
                )
                PlayerInfoCard(
                    modifier = Modifier.weight(1f),
                    time = player2Time,
                    color = RedPlayerColor,
                    isLeft = false
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(8f / 10f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(BoardDarkCell)
            ) {
                ReplayBoardUI(board = board)
            }

            Spacer(modifier = Modifier.height(24.dp))
            ReplayControls(
                onStepForward = onStepForward,
                onStepBackward = onStepBackward,
                onFastForward = onFastForward,
                onFastBackward = onFastBackward
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PlayerInfoCard(
    modifier: Modifier = Modifier,
    time: String,
    color: Color,
    isLeft: Boolean
) {
    Box(
        modifier = modifier
            .height(72.dp)
            .border(2.dp, color, RoundedCornerShape(36.dp))
            .background(CardBackground, RoundedCornerShape(36.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isLeft) Arrangement.Start else Arrangement.End
        ) {
            if (isLeft) {
                AvatarWithBorder(color = color)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = time,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = time,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(12.dp))
                AvatarWithBorder(color = color)
            }
        }
    }
}

@Composable
fun AvatarWithBorder(color: Color) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .border(2.dp, color, CircleShape)
            .padding(2.dp)
            .clip(CircleShape)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_avatar),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ReplayBoardUI(board: Board) {
    Column {
        for (row in 0 until 10) {
            Row(modifier = Modifier.weight(1f)) {
                for (col in 0 until 8) {
                    val isLight = ((row + col) % 2) != 0
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(if (isLight) BoardLightCell else BoardDarkCell),
                        contentAlignment = Alignment.Center
                    ) {
                        val piece = board.getPiece(row, col)
                        piece?.let { p ->
                            val rotation = p.rotation.toFloat()
                            Image(
                                // TODO: ARREGLAR REPLAY
                                painter = painterResource(id = p.getImageRes(imInternalRed = true, 1)),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(0.8f)
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

@Composable
fun ReplayControls(
    onStepForward: () -> Unit,
    onStepBackward: () -> Unit,
    onFastForward: () -> Unit,
    onFastBackward: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(ControlsBackground, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ControlButton(
            iconRes = R.drawable.ic_arrow_left,
            onClick = onFastBackward,
            isDouble = true,
            contentDescription = stringResource(id = R.string.replay_fast_backward)
        )
        ControlButton(
            iconRes = R.drawable.ic_arrow_left,
            onClick = onStepBackward,
            contentDescription = stringResource(id = R.string.replay_step_backward)
        )
        ControlButton(
            iconRes = R.drawable.ic_arrow_right,
            onClick = onStepForward,
            contentDescription = stringResource(id = R.string.replay_step_forward)
        )
        ControlButton(
            iconRes = R.drawable.ic_arrow_right,
            onClick = onFastForward,
            isDouble = true,
            contentDescription = stringResource(id = R.string.replay_fast_forward)
        )
    }
}

@Composable
fun ControlButton(
    iconRes: Int,
    onClick: () -> Unit,
    isDouble: Boolean = false,
    contentDescription: String
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isDouble) {
                val offsetValue = if (iconRes == R.drawable.ic_arrow_left) (-4).dp else 4.dp
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = contentDescription,
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = offsetValue)
                )
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = contentDescription,
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = -offsetValue)
                )
            } else {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = contentDescription,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0B1E)
@Composable
fun GameReplayScreenPreview() {
    val board = Board(10, 8)
    board.setPiece(0, 3, Piece(true, PieceType.DEFLECTOR).apply { rotation = 90 })
    board.setPiece(0, 4, Piece(true, PieceType.DEFLECTOR).apply { rotation = 180 })
    board.setPiece(0, 7, Piece(true, PieceType.KING))
    board.setPiece(9, 0, Piece(false, PieceType.KING))
    
    GameReplayScreen(board = board)
}

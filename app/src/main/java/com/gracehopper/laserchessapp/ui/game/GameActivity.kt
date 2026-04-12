package com.gracehopper.laserchessapp.ui.game

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.data.repository.GameRepository
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.gameLogic.board.BoardParser
import com.gracehopper.laserchessapp.gameLogic.laser.LaserUtils
import com.gracehopper.laserchessapp.gameLogic.move.CoordsConverter
import com.gracehopper.laserchessapp.gameLogic.move.MoveParser
import com.gracehopper.laserchessapp.gameLogic.pieces.Piece
import com.gracehopper.laserchessapp.gameLogic.pieces.PieceType
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.OnBackPressedCallback


class GameActivity : AppCompatActivity() {

    companion object {
        var imInternalRed: Boolean = true
        var isMyTurn by mutableStateOf(true)
    }

    private var waitingForServerConfirmation = false

    private val testMode = false
    private val gameRepository = GameRepository()
    private val rows = 10
    private val cols = 8
    private lateinit var boardM: Board          // Modelo lógico del tablero
    private var clearTrigger by mutableIntStateOf(0)            // Trigger para avisar a la UI de limpiar selección
    private var selectedPos: Pair<Int, Int>? = null             // Posición de la pieza
    private lateinit var controls: LinearLayout
    private var pendingGameEnd: Pair<String, String?>? = null
    private var gameEnded = false
    lateinit var backCallback: OnBackPressedCallback
    var laserPath by mutableStateOf<List<Pair<Int, Int>>>(emptyList())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Bloqueamos el que se pueda salir con el boton de atrás
            }
        }
        onBackPressedDispatcher.addCallback(this, backCallback)

        val controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
        controller.hide(WindowInsetsCompat.Type.systemBars())


        setContentView(R.layout.activity_game)

        val board = findViewById<ComposeView>(R.id.board)
        controls = findViewById<LinearLayout>(R.id.rotationButtons)
        val btnLeft = findViewById<ImageButton>(R.id.btnRotLeft)
        val btnRight = findViewById<ImageButton>(R.id.btnRotRight)

        val btnExit = findViewById<ImageButton>(R.id.btnExit)

        boardM = Board(rows, cols)

        imInternalRed = ActiveGameManager.imRedPlayer
        isMyTurn = imInternalRed
        Log.d("PLAYER", "Soy rojo interno: $imInternalRed")
        Log.d("PLAYER", "CSV: ${ActiveGameManager.intialBoardCSV != null}")

        if (testMode) {
            loadTestBoard()
        } else {
            val csv = ActiveGameManager.intialBoardCSV
            if (csv != null) {
                BoardParser.boadFromCSV(boardM, csv)
            }
        }

        ActiveGameManager.setCallbacks(
            onMessageReceived = { content, extra ->
                runOnUiThread {
                    when (content) {
                        "P1_WINS", "P2_WINS" -> {
                            pendingGameEnd = Pair(content, extra)
                            gameEnded = true
                        }

                        else -> {
                            applyServerMove(content, extra)
                        }
                    }
                }
            },
            onClosed = {
                Log.d("WS", "WebSocket cerrado")
            },
            onError = { error ->
                runOnUiThread {
                    Log.e("WS", "Error: $error")

                    if (!gameEnded) {
                        Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        )

        // UI
        board.setContent {
            GameScreen(
                board = boardM,
                isRedPlayer = imInternalRed,
                isMyTurn = isMyTurn,
                onPieceSelected = { pos ->
                    selectedPos = pos

                    if (pos != null) {
                        val (r, c) = pos
                        val piece = boardM.getPiece(r, c)

                        if (piece != null && piece.canRotate()) {
                            controls.visibility = View.VISIBLE

                            btnLeft.visibility =
                                if (piece.canRotateLeft(imInternalRed)) View.VISIBLE else View.GONE

                            btnRight.visibility =
                                if (piece.canRotateRight(imInternalRed)) View.VISIBLE else View.GONE

                        } else {
                            controls.visibility = View.GONE
                        }

                    } else {
                        controls.visibility = View.GONE
                    }
                },
                onMove = { from, to -> movePiece(from, to) },
                clearSelectionTrigger = clearTrigger,
                laserPath = laserPath
            )
        }

        btnExit.setOnClickListener {
            finish()
        }

        // Rot. izq.
        btnLeft.setOnClickListener {
            selectedPos?.let { pos ->
                if (testMode) {
                    val piece = boardM.getPiece(pos.first, pos.second)
                    piece?.rotateLeft()
                } else {
                    gameRepository.sendRotateLeft(pos)
                    waitingForServerConfirmation = true
                    isMyTurn = false
                }

                selectedPos = null
                clearTrigger++
                controls.visibility = View.GONE
            }
        }

        // Rot. der.
        btnRight.setOnClickListener {
            selectedPos?.let { pos ->

                if (testMode) {
                    val piece = boardM.getPiece(pos.first, pos.second)
                    piece?.rotateRight()
                } else {
                    gameRepository.sendRotateRight(pos)
                    waitingForServerConfirmation = true
                    isMyTurn = false
                }

                selectedPos = null
                clearTrigger++
                controls.visibility = View.GONE
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        ActiveGameManager.clearCallbacks()
    }


    private fun movePiece(from: Pair<Int, Int>, to: Pair<Int, Int>) {

        if (testMode) {
            val (r1, c1) = from
            val (r2, c2) = to

            val pieceFrom = boardM.getPiece(r1, c1)
            val pieceTo = boardM.getPiece(r2, c2)

            if (pieceFrom != null && pieceFrom.type == PieceType.SWITCHER && pieceTo != null) {
                boardM.setPiece(r1, c1, pieceTo)
                boardM.setPiece(r2, c2, pieceFrom)
            } else {
                boardM.setPiece(r2, c2, pieceFrom)
                boardM.setPiece(r1, c1, null)
            }
        } else {
            gameRepository.sendMove(from, to)
            waitingForServerConfirmation = true
            isMyTurn = false
        }

        selectedPos = null
        clearTrigger++
    }

    private fun loadTestBoard() {

        // ROJAS
        boardM.setPiece(0, 0, Piece(true, PieceType.LASER))
        boardM.setPiece(1, 1, Piece(true, PieceType.KING))
        boardM.setPiece(2, 2, Piece(true, PieceType.DEFLECTOR))
        boardM.setPiece(3, 3, Piece(true, PieceType.DEFENDER))
        boardM.setPiece(4, 4, Piece(true, PieceType.SWITCHER))

        // AZULES
        boardM.setPiece(9, 7, Piece(false, PieceType.LASER))
        boardM.setPiece(8, 6, Piece(false, PieceType.KING))
        boardM.setPiece(7, 5, Piece(false, PieceType.DEFLECTOR))
        boardM.setPiece(6, 4, Piece(false, PieceType.DEFENDER))
        boardM.setPiece(5, 3, Piece(false, PieceType.SWITCHER))

        boardM.getPiece(2, 2)?.rotation = 90
        boardM.getPiece(7, 5)?.rotation = 180
    }

    private fun applyServerMove(moveStr: String, laserPathStr: String?) {
        val move = MoveParser.parseMove(moveStr)

        val fromPos = CoordsConverter.notationToPosition(move.from)
        val piece = boardM.getPiece(fromPos.first, fromPos.second)

        when (move.type) {
            'T' -> {
                val toPos = CoordsConverter.notationToPosition(move.to!!)
                val pieceTo = boardM.getPiece(toPos.first, toPos.second)

                boardM.setPiece(toPos.first, toPos.second, piece)
                boardM.setPiece(fromPos.first, fromPos.second, pieceTo)
            }

            'R' -> {
                piece?.rotateRight()
            }

            'L' -> {
                piece?.rotateLeft()
            }
        }

        laserPath = LaserUtils.parseLaserPath(laserPathStr)
        Log.d("LASER", "Laser path board coords: $laserPath")

        Handler(Looper.getMainLooper()).postDelayed({
            move.destroyed?.let {
                val destroyedPos = CoordsConverter.notationToPosition(it)
                boardM.setPiece(destroyedPos.first, destroyedPos.second, null)
            }

            laserPath = emptyList()

            if (waitingForServerConfirmation) {
                waitingForServerConfirmation = false
            } else {
                isMyTurn = true
            }

            controls.visibility = View.GONE
            clearTrigger++

            if (gameEnded && pendingGameEnd != null) {
                if (!isFinishing && !isDestroyed &&
                    supportFragmentManager.findFragmentByTag("GameResult") == null
                ) {

                    val (winner, cause) = pendingGameEnd!!
                    backCallback.isEnabled = false

                    val dialog = GameResultDialogFragment(winner, cause)
                    dialog.show(supportFragmentManager, "GameResult")
                }

                pendingGameEnd = null
                gameEnded = false
            }
        }, 1000)
    }
}
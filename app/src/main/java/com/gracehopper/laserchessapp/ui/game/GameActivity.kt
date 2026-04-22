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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.manager.GameTimerManager
import com.gracehopper.laserchessapp.data.model.game.GameEvent
import com.gracehopper.laserchessapp.ui.utils.TimeUtils.formatTime


/**
 * Activity principal de la partida.
 *
 * Se encarga de:
 * - Inicializar el tablero
 * - Gestionar la interacción del usuario
 * - Comunicarse con el backend
 * - Aplicar movimientos recibidos
 * - Mostrar el láser y resultados de partida
 */
class GameActivity : AppCompatActivity() {

    companion object {

        /**
         * Indica si el jugador interno es rojo.
         */
        var imInternalRed: Boolean = true

        /**
         * Indica si es el turno del jugador actual.
         */
        var isMyTurn by mutableStateOf(true)
    }

    private var waitingForServerConfirmation = false

    private val testMode = false
    private val gameRepository = GameRepository()
    private val rows = 10
    private val cols = 8
    private lateinit var boardM: Board          // Modelo lógico del tablero
    private var clearTrigger by mutableIntStateOf(0)    // Trigger para limpiar selección en UI
    private var selectedPos: Pair<Int, Int>? = null             // Posición seleccionada
    private lateinit var controls: LinearLayout
    private var pendingGameEnd: Pair<String, String?>? = null
    private var gameEnded = false
    lateinit var backCallback: OnBackPressedCallback

    /**
     * Trayectoria actual del láser para renderizar en UI.
     */
    var laserPath by mutableStateOf<List<Pair<Int, Int>>>(emptyList())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Bloquear botón atrás durante la partida
         */
        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // No se permite salir con botón atrás
            }
        }
        onBackPressedDispatcher.addCallback(this, backCallback)

        /**
         * Ocultar barras del sistema (pantalla completa)
         */
        val controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
        controller.hide(WindowInsetsCompat.Type.systemBars())


        setContentView(R.layout.activity_game)

        val namePlayer = findViewById<TextView>(R.id.namePlayer)
        val nameEnemy = findViewById<TextView>(R.id.nameEnemy)
        val timerPlayer = findViewById<TextView>(R.id.timePlayer)
        val timerEnemy = findViewById<TextView>(R.id.timeEnemy)

    // Usuario actual
        val myProfile = CurrentUserManager.getMyCurrentProfile()
        namePlayer.text = myProfile?.username ?: "Tú"

    // Rival
        val opponent = ActiveGameManager.currentOpponentUsername
        nameEnemy.text = opponent ?: "Rival"

        val board = findViewById<ComposeView>(R.id.board)
        controls = findViewById<LinearLayout>(R.id.rotationButtons)
        val btnLeft = findViewById<ImageButton>(R.id.btnRotLeft)
        val btnRight = findViewById<ImageButton>(R.id.btnRotRight)

        val btnExit = findViewById<ImageButton>(R.id.btnExit)

        boardM = Board(rows, cols)

        /**
         * Inicializar jugador y turno
         */
        imInternalRed = ActiveGameManager.imRedPlayer
        isMyTurn = imInternalRed

        val startingTime = ActiveGameManager.currentStartingTime ?: 300

        GameTimerManager.initTimers(startingTime)
        GameTimerManager.start()
        GameTimerManager.setMyTurn(isMyTurn)

        GameTimerManager.myTimer.observe(this) { timer ->
            timer?.let {
                timerPlayer.text = formatTime(it.timeLeftMillis)
            }
        }

        GameTimerManager.opponentTimer.observe(this) { timer ->
            timer?.let {
                timerEnemy.text = formatTime(it.timeLeftMillis)
            }
        }

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

        /**
         * Callbacks del WebSocket
         */
        ActiveGameManager.setCallbacks(
            onMessageReceived = { event ->
                runOnUiThread {

                    when (event) {

                        /**
                         * Movimiento normal
                         */
                        is GameEvent.Move -> {
                            val moveData = event.moveAndTime ?: return@runOnUiThread
                            applyServerMove(moveData)
                        }

                        /**
                         * Fin de partida
                         */
                        is GameEvent.End -> {
                            val winner = event.winner ?: return@runOnUiThread
                            val cause = event.victoryCause ?: return@runOnUiThread
                            pendingGameEnd = Pair(winner, cause)
                            gameEnded = true
                        }

                        // TODO: REVISAR NUEVOS MENSAJES (de aquí para abajo) ----------------------

                        is GameEvent.State -> {
                            // esto para reconstruir desde log, ns si se hará aquí
                        }

                        is GameEvent.PauseRequest -> {
                            // TODO: Diálogo de aceptar/rechazar pausa
                        }

                        is GameEvent.PauseReject -> {
                            // TODO: Diálogo de rechazo de pausa ??
                            Toast.makeText(this,
                                "Tu solicitud de pausa ha sido rechazada",
                                Toast.LENGTH_SHORT).show()
                        }

                        is GameEvent.Paused -> {
                            // TODO: Diálogo de pausa
                            Toast.makeText(this,
                                "La partida ha sido pausada",
                                Toast.LENGTH_SHORT).show()
                        }

                        is GameEvent.Error -> {
                            Log.e("GAME", "Error del servidor: ${event.message}")
                            Toast.makeText(
                                this,
                                event.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is GameEvent.OpponentDisconnected -> {
                            // igual quitar el toast, de momento para pruebas lo dejamos
                            Toast.makeText(
                                this,
                                "Tu oponente se ha desconectado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is GameEvent.OpponentReconnected -> {
                            // igual quitar el toast, de momento para pruebas lo dejamos
                            Toast.makeText(
                                this,
                                "Tu oponente se ha reconectado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is GameEvent.ConnectionClosed -> {
                            Log.d("WS", "Conexión cerrada: ${event.reason}")
                        }

                        else -> {
                            // ignorar otros eventos
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
                        Toast.makeText(this,
                            "Error de conexión",
                            Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        )

        /**
         * UI Compose
         */
        board.setContent {
            GameScreen(
                board = boardM,
                isRedPlayer = imInternalRed,
                isMyTurn = isMyTurn,
                /**
                 * Selección de pieza
                 */
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

        /**
         * Salir de la partida
         */
        btnExit.setOnClickListener {
            finish()
        }

        /**
         * Rotación izquierda
         */
        btnLeft.setOnClickListener {
            selectedPos?.let { pos ->
                if (testMode) {
                    val piece = boardM.getPiece(pos.first, pos.second)
                    piece?.rotateLeft()
                } else {
                    gameRepository.sendRotateLeft(pos)
                    waitingForServerConfirmation = true
                    isMyTurn = false
                    GameTimerManager.setMyTurn(false)
                }

                selectedPos = null
                clearTrigger++
                controls.visibility = View.GONE
            }
        }

        /**
         * Rotación derecha
         */
        btnRight.setOnClickListener {
            selectedPos?.let { pos ->

                if (testMode) {
                    val piece = boardM.getPiece(pos.first, pos.second)
                    piece?.rotateRight()
                } else {
                    gameRepository.sendRotateRight(pos)
                    waitingForServerConfirmation = true
                    isMyTurn = false
                    GameTimerManager.setMyTurn(false)
                }

                selectedPos = null
                clearTrigger++
                controls.visibility = View.GONE
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        /**
         * Limpiar callbacks al destruir la activity
         */
        ActiveGameManager.clearCallbacks()
        GameTimerManager.stop()
    }


    /**
     * Gestiona el movimiento de una pieza.
     */
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
            GameTimerManager.setMyTurn(false)
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

    /**
     * Aplica un movimiento recibido del servidor.
     */
    private fun applyServerMove(moveStr: String) {
        val move = MoveParser.parseMove(moveStr)
        val timeFromBackend = move.timer

        val iMoved = waitingForServerConfirmation

        if (iMoved) {
            GameTimerManager.syncTimers(
                myTime = timeFromBackend,
                opponentTime = GameTimerManager.opponentTimer.value?.timeLeftMillis ?: 0
            )
        } else {
            GameTimerManager.syncTimers(
                myTime = GameTimerManager.myTimer.value?.timeLeftMillis ?: 0,
                opponentTime = timeFromBackend
            )
        }

        if (iMoved) {
            waitingForServerConfirmation = false
        }

        isMyTurn = !iMoved
        GameTimerManager.setMyTurn(isMyTurn)


        val fromPos = CoordsConverter.notationToPosition(move.from)
        val piece = boardM.getPiece(fromPos.first, fromPos.second)

        when (move.type) {

            /**
             * Traslación
             */
            'T' -> {
                val toPos = CoordsConverter.notationToPosition(move.to!!)
                val pieceTo = boardM.getPiece(toPos.first, toPos.second)

                boardM.setPiece(toPos.first, toPos.second, piece)
                boardM.setPiece(fromPos.first, fromPos.second, pieceTo)
            }

            /**
             * Rotaciones
             */
            'R' -> {
                piece?.rotateRight()
            }

            'L' -> {
                piece?.rotateLeft()
            }
        }

        /**
         * Mostrar trayectoria del láser
         */
        laserPath = LaserUtils.parseLaserPath(move.laserPath)
        Log.d("LASER", "Laser path board coords: $laserPath")

        /**
         * Aplicar efectos tras 1 segundo (animación)
         */
        Handler(Looper.getMainLooper()).postDelayed({

            /**
             * Eliminar pieza destruida
             */
            move.destroyed?.let {
                val destroyedPos = CoordsConverter.notationToPosition(it)
                boardM.setPiece(destroyedPos.first, destroyedPos.second, null)
            }

            laserPath = emptyList()

            controls.visibility = View.GONE
            clearTrigger++

            /**
             * Mostrar resultado de partida
             */
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
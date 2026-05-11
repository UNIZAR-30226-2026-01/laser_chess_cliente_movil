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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.manager.GameTimerManager
import com.gracehopper.laserchessapp.data.model.game.GameEvent
import com.gracehopper.laserchessapp.data.model.game.GamePlayerInfo
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.UserRepository
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

    private var isMyTurn by mutableStateOf(true)
    private var waitingForServerConfirmation = false

    private val testMode = false
    private val gameRepository = GameRepository()
    private val rows = 10
    private val cols = 8
    private lateinit var boardM: Board          // Modelo lógico del tablero
    private var clearTrigger by mutableIntStateOf(0)    // Trigger para limpiar selección en UI
    private var selectedPos: Pair<Int, Int>? = null             // Posición seleccionada
    private lateinit var controls: LinearLayout
    private var gameEnded = false
    private var gameResultShown = false
    private var waitingEndAfterMove = false
    private var lastWinner: String? = null
    private var lastCause: String? = null
    private var pauseDialog: DialogFragment? = null
    var pauseRequested = false
    var laserIsRed by mutableStateOf(false)
    lateinit var backCallback: OnBackPressedCallback
    private var lastXpDiff: Int = 0
    private var lastMoneyDiff: Int = 0
    private var lastEloDiff: Int? = null
    private var rewardsReceived = false
    private var processingMove = false
    private val pendingMoves = ArrayDeque<String>()

    /**
     * Trayectoria actual del láser para renderizar en UI.
     */
    var laserPath by mutableStateOf<List<Pair<Int, Int>>>(emptyList())

    var opponentPieceSkinState by mutableIntStateOf(ActiveGameManager.getOpponentPieceSkin())
    var opponentBoardSkinState by mutableIntStateOf(ActiveGameManager.getOpponentBoardSkin())

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
        val avatarPlayerView = findViewById<android.widget.ImageView>(R.id.avatarPlayer)

        CurrentUserManager.myProfile.observe(this) { profile ->

            if (profile != null) {

                namePlayer.text = profile.username

                avatarPlayerView.setImageResource(
                    com.gracehopper.laserchessapp.ui.utils.ItemUtils.getItemDrawable(
                        profile.avatar.takeIf { it > 0 } ?: 1
                    )
                )
            }
        }

        val avatarEnemyView = findViewById<android.widget.ImageView>(R.id.avatarEnemy)

        val opponent = ActiveGameManager.getOpponentUsername()
        nameEnemy.text = opponent ?: "Rival"

        // Si el WaitingGameDialogFragment pasó las skins del rival en el Intent, usarlas ya
        val intentPieceSkin = intent.getIntExtra("OPPONENT_PIECE_SKIN", -1)
        val intentBoardSkin = intent.getIntExtra("OPPONENT_BOARD_SKIN", -1)
        if (intentPieceSkin != -1) opponentPieceSkinState = intentPieceSkin
        if (intentBoardSkin != -1) opponentBoardSkinState = intentBoardSkin

        if (opponent == null) {
            resolveOpponentName(nameEnemy)
        }

        val board = findViewById<ComposeView>(R.id.board)
        controls = findViewById<LinearLayout>(R.id.rotationButtons)
        val btnLeft = findViewById<ImageButton>(R.id.btnRotLeft)
        val btnRight = findViewById<ImageButton>(R.id.btnRotRight)

        val btnPause = findViewById<ImageButton>(R.id.btnPause)

        if (ActiveGameManager.isFriendlyGame) {
            btnPause.visibility = View.VISIBLE
        } else {
            btnPause.visibility = View.GONE
        }

        boardM = Board(rows, cols)

        /**
         * Inicializar jugador y turno
         */
        isMyTurn = ActiveGameManager.imRedPlayer

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

        Log.d("PLAYER", "Soy rojo interno: ${ActiveGameManager.imRedPlayer}")

        if (testMode) {
            loadTestBoard()
        } else {
            val csv = ActiveGameManager.intialBoardCSV
            if (csv != null) {
                Log.d("RECONNECT", "Cargando tablero desde CSV (${csv.length} chars)")
                BoardParser.boardFromCSV(boardM, csv)
            }
            // Si venimos de reconexión, el State llegó antes de que esta Activity
            // existiera. Aplicamos el log guardado ahora que el tablero está listo.
            val pending = ActiveGameManager.pendingStateLog
            if (pending != null) {
                Log.d("RECONNECT", "Aplicando state log: '$pending'")
                val moveCount = applyStateLog(pending)
                recalculateTurnAfterStateLog(moveCount)
                clearTrigger++
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
                         * Estado inicial de la partida
                         */
                        /**
                         * Estado inicial de la partida
                         */
                        is GameEvent.InitialState -> {
                            val csv = event.boardCsv
                            if (csv != null) {
                                Log.d("GAME", "Actualizando tablero desde InitialState")
                                boardM.clear()
                                BoardParser.boardFromCSV(boardM, csv)
                            }
                            
                            isMyTurn = ActiveGameManager.imRedPlayer
                            GameTimerManager.setMyTurn(isMyTurn)
                            changeCardsBasedOnTurn(isMyTurn)
                            clearTrigger++
                        }

                        /**
                         * Fin de partida
                         */
                        is GameEvent.End -> {

                            if (gameResultShown) return@runOnUiThread

                            val winner = event.winner ?: return@runOnUiThread
                            val cause = event.victoryCause

                            lastWinner = winner
                            lastCause = cause
                            gameEnded = true

                            // Sí viene tras movimiento, esperar animación
                            if (waitingForServerConfirmation) {
                                waitingEndAfterMove = true
                            } else {
                                tryShowGameResult()
                            }
                        }

                        // TODO: REVISAR NUEVOS MENSAJES (de aquí para abajo) ----------------------

                        is GameEvent.State -> {

                            val log = event.log ?: return@runOnUiThread

                            boardM.clear()

                            val csv = ActiveGameManager.intialBoardCSV
                            if (csv != null) {
                                BoardParser.boardFromCSV(boardM, csv)
                            }

                            val moveCount = applyStateLog(log)
                            recalculateTurnAfterStateLog(moveCount)
                            clearTrigger++
                        }

                        is GameEvent.PauseRequest -> {
                            // TODO: Diálogo de aceptar/rechazar pausa
                            val dialog = PauseRequestDialogFragment(
                                onAccept = {
                                    gameRepository.sendPause()
                                },
                                onReject = {
                                    gameRepository.sendPauseReject()
                                }
                            )

                            dialog.show(supportFragmentManager, "PauseDialog")
                        }

                        is GameEvent.PauseReject -> {
                            // TODO: Diálogo de rechazo de pausa ??
                            pauseDialog?.dismiss()
                            pauseDialog = null

                            supportFragmentManager.findFragmentByTag("PauseDialog")?.let {
                                (it as? DialogFragment)?.dismiss()
                            }

                            if (pauseRequested) {
                                Toast.makeText(
                                    this,
                                    "Tu solicitud de pausa ha sido rechazada",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "El rival ha cancelado la solicitud de pausa",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            pauseRequested = false
                        }

                        is GameEvent.Paused -> {
                            // TODO: Diálogo de pausa
                            pauseRequested = false

                            pauseDialog?.dismiss()
                            pauseDialog = null

                            GameTimerManager.stop()

                            Toast.makeText(
                                this,
                                "La partida ha sido pausada",
                                Toast.LENGTH_SHORT
                            ).show()
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

                        is GameEvent.MatchStart -> {
                            val opponentId = event.opponentId ?: return@runOnUiThread
                            val userRepo = UserRepository(NetworkUtils.getApiService())
                            userRepo.getUserProfile(
                                userId = opponentId,
                                onSuccess = { profile ->
                                    runOnUiThread {
                                        nameEnemy.text = profile.username
                                        avatarEnemyView?.setImageResource(
                                            com.gracehopper.laserchessapp.ui.utils.ItemUtils.getItemDrawable(
                                                profile.avatar.takeIf { it > 0 } ?: 1
                                            )
                                        )
                                        ActiveGameManager.setOpponentInfo(
                                            GamePlayerInfo(
                                                id = opponentId,
                                                username = profile.username,
                                                avatar = profile.avatar,
                                                pieceSkin = profile.pieceSkin,
                                                boardSkin = profile.boardSkin,
                                                winAnimation = profile.winAnimation
                                            )
                                        )
                                        // Actualizar los State para que Compose recomponga el tablero
                                        opponentPieceSkinState = profile.pieceSkin
                                        opponentBoardSkinState = profile.boardSkin
                                    }
                                },
                                onError = { /* mantener "Rival" si falla */ }
                            )
                        }

                        is GameEvent.Rewards -> {

                            rewardsReceived = true

                            lastXpDiff = event.xpDiff
                            lastMoneyDiff = event.moneyDiff

                            tryShowGameResult()
                        }

                        is GameEvent.EloUpdate -> {
                            lastEloDiff = event.eloDiff

                            tryShowGameResult()
                        }

                        is GameEvent.Reconnected -> {
                            Log.d("GAME", "Evento Reconnected recibido")
                        }

                        else -> {
                            // ignorar otros eventos
                        }

                    }
                }
            },
            onClosed = {
                Log.d("WS", "WebSocket cerrado")

                if (!gameEnded) {
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Conexión perdida",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            },
            onError = { error ->
                runOnUiThread {
                    Log.e("WS", "Error: $error")

                    if (!gameResultShown) {
                        Toast.makeText(
                            this,
                            "Error de conexión",
                            Toast.LENGTH_SHORT
                        ).show()
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
                isRedPlayer = ActiveGameManager.imRedPlayer,
                isMyTurn = isMyTurn,
                renderCoordinates = false,
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
                                if (piece.canRotateLeft(ActiveGameManager.imRedPlayer)) View.VISIBLE else View.GONE

                            btnRight.visibility =
                                if (piece.canRotateRight(ActiveGameManager.imRedPlayer)) View.VISIBLE else View.GONE

                        } else {
                            controls.visibility = View.GONE
                        }

                    } else {
                        controls.visibility = View.GONE
                    }
                },
                onMove = { from, to -> movePiece(from, to) },
                clearSelectionTrigger = clearTrigger,
                laserPath = laserPath,
                laserIsRed = laserIsRed,
                myPieceSkin = CurrentUserManager.getMyCurrentPieceSkin(),
                myBoardSkin = CurrentUserManager.getMyCurrentBoardSkin(),
                opponentPieceSkin = opponentPieceSkinState,
                opponentBoardSkin = opponentBoardSkinState
            )
        }

        /**
         * Solicitar pausar la partida
         */
        btnPause.setOnClickListener {
            if (!pauseRequested) {
                // pedir pausa
                pauseRequested = true
                gameRepository.sendPause()

                pauseDialog = PauseWaitingDialogFragment(
                    onCancel = {
                        pauseRequested = false
                        gameRepository.sendPauseReject()
                    }
                )

                pauseDialog?.show(supportFragmentManager, "PauseWaitingDialog")
            } else {
                // cancelar solicitud de pausa
                pauseRequested = false
                gameRepository.sendPauseReject()

                pauseDialog?.dismiss()
                pauseDialog = null
            }
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
                    changeCardsBasedOnTurn(false)
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
                    changeCardsBasedOnTurn(false)
                }

                selectedPos = null
                clearTrigger++
                controls.visibility = View.GONE
            }
        }

        changeCardsBasedOnTurn(isMyTurn)

    }

    /**
     * Resuelve el nombre del rival por HTTP cuando no está disponible en el manager.
     * Cubre dos casos: reconexión (reconnectingOpponentId) y matchmaking (OPPONENT_ID en el Intent).
     */
    private fun resolveOpponentName(nameEnemy: TextView) {
        val opponentId = ActiveGameManager.reconnectingOpponentId
            ?: intent.getLongExtra("OPPONENT_ID", -1L).takeIf { it != -1L }
            ?: return

        val avatarEnemyView = findViewById<android.widget.ImageView?>(R.id.avatarEnemy)

        val userRepo = UserRepository(NetworkUtils.getApiService())
        userRepo.getUserProfile(
            userId = opponentId,
            onSuccess = { profile ->
                runOnUiThread {
                    nameEnemy.text = profile.username
                    avatarEnemyView?.setImageResource(
                        com.gracehopper.laserchessapp.ui.utils.ItemUtils.getItemDrawable(
                            profile.avatar.takeIf { it > 0 } ?: 1
                        )
                    )
                    ActiveGameManager.setOpponentInfo(
                        GamePlayerInfo(
                            id = opponentId,
                            username = profile.username,
                            avatar = profile.avatar,
                            pieceSkin = profile.pieceSkin,
                            boardSkin = profile.boardSkin,
                            winAnimation = profile.winAnimation
                        )
                    )
                }
            },
            onError = { /* mantener "Rival" */ }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        GameTimerManager.stop()
        ActiveGameManager.resetAll()
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
            changeCardsBasedOnTurn(false)
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

        if (processingMove) {

            Log.e(
                "TURN_DEBUG",
                "MOVE ENCOLADO mientras otro sigue animando: $moveStr"
            )

            pendingMoves.addLast(moveStr)
            return
        }

        processingMove = true

        val move = MoveParser.parseMove(moveStr)
        val timeFromBackend = move.timer

        val fromPos = CoordsConverter.notationToPosition(move.from)
        val piece = boardM.getPiece(fromPos.first, fromPos.second)

        // Determinar si el movimiento es del jugador actual o del rival
        val isThisMyMove = piece?.isRed == ActiveGameManager.imRedPlayer

        Log.d(
            "TURN_DEBUG",
            "START move=$moveStr isThisMyMove=$isThisMyMove waiting=$waitingForServerConfirmation isMyTurn=$isMyTurn"
        )

        if (isThisMyMove) {
            waitingForServerConfirmation = false
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

        // Determinar el color del láser. 
        // Si la pieza existe (rotación o traslación), usamos su color.
        // Si no (por ejemplo, pieza destruida que ya no está), inferimos el color por quién ha movido.
        laserIsRed = if (piece != null) {
            piece.isRed
        } else {
            if (isThisMyMove) ActiveGameManager.imRedPlayer else !ActiveGameManager.imRedPlayer
        }

        Log.d("LASER_DEBUG", "Move by player. pieceIsRed=${piece?.isRed} imRed=${ActiveGameManager.imRedPlayer} isThisMyMove=$isThisMyMove -> laserIsRed=$laserIsRed")

        laserPath = LaserUtils.parseLaserPath(move.laserPath)

        Handler(Looper.getMainLooper()).postDelayed({

            move.destroyed?.let {
                val destroyedPos = CoordsConverter.notationToPosition(it)
                boardM.setPiece(destroyedPos.first, destroyedPos.second, null)
            }

            if (isThisMyMove) {
                waitingEndAfterMove = false
            }

            Log.d(
                "TURN_DEBUG",
                "END move=$moveStr setTurn=${!isThisMyMove}"
            )

            isMyTurn = !isThisMyMove
            GameTimerManager.setMyTurn(isMyTurn)

            if (waitingEndAfterMove && gameEnded && !gameResultShown) {
                tryShowGameResult()
            }

            laserPath = emptyList()

            controls.visibility = View.GONE
            clearTrigger++

            processingMove = false

            changeCardsBasedOnTurn(isMyTurn)

            if (pendingMoves.isNotEmpty()) {

                val nextMove = pendingMoves.removeFirst()

                Log.d(
                    "TURN_DEBUG",
                    "PROCESS NEXT MOVE: $nextMove"
                )

                applyServerMove(nextMove)
            }

        }, 1000)
    }

    private fun changeCardsBasedOnTurn(turn: Boolean){
        val myProfileCard = findViewById<ConstraintLayout>(R.id.own_card)
        val opProfileCard = findViewById<ConstraintLayout>(R.id.oponent_card)

        if(turn){
            myProfileCard.setBackgroundResource(R.drawable.bg_avatar_blue_border)
            opProfileCard.setBackgroundResource(R.drawable.bg_avatar_no_border)
        }else{
            myProfileCard.setBackgroundResource(R.drawable.bg_avatar_no_border)
            opProfileCard.setBackgroundResource(R.drawable.bg_avatar_red_border)
        }
    }
    private fun applyStateLog(log: String): Int {

        if (log.isBlank()) {
            Log.d("RECONNECT", "applyStateLog: log vacío, nada que aplicar")
            return 0
        }

        val moves = log.split(";").filter { it.isNotBlank() }

        Log.d("RECONNECT", "applyStateLog: ${moves.size} movimientos a aplicar")

        for (moveStr in moves) {
            applyStateMove(moveStr)
        }

        return moves.size
    }

    /**
     * Calcula si es el turno del jugador actual.
     */
    private fun recalculateTurnAfterStateLog(moveCount: Int) {
        val isRedTurn = moveCount % 2 == 0
        isMyTurn = (ActiveGameManager.imRedPlayer == isRedTurn)
        GameTimerManager.setMyTurn(isMyTurn)
        changeCardsBasedOnTurn(isMyTurn)
        Log.d(
            "RECONNECT",
            "recalculateTurn: moveCount=$moveCount isRedTurn=$isRedTurn imRed=${ActiveGameManager.imRedPlayer} → isMyTurn=$isMyTurn"
        )
    }

    private fun applyStateMove(moveStr: String) {

        Log.d("RECONNECT", "applyStateMove: '$moveStr'")
        val move = MoveParser.parseMove(moveStr)

        val fromPos = CoordsConverter.notationToPosition(move.from)
        val piece = boardM.getPiece(fromPos.first, fromPos.second)
        Log.d("RECONNECT", "  from=${move.from} pos=$fromPos pieza=$piece tipo=${move.type}")

        when (move.type) {

            'T' -> {
                val toPos = CoordsConverter.notationToPosition(move.to!!)
                val pieceTo = boardM.getPiece(toPos.first, toPos.second)

                boardM.setPiece(toPos.first, toPos.second, piece)
                boardM.setPiece(fromPos.first, fromPos.second, pieceTo)
            }

            'R' -> piece?.rotateRight()
            'L' -> piece?.rotateLeft()
        }

        // eliminar pieza destruida
        move.destroyed?.let {
            val destroyedPos = CoordsConverter.notationToPosition(it)
            boardM.setPiece(destroyedPos.first, destroyedPos.second, null)
        }
    }

    private fun tryShowGameResult() {

        if (!gameEnded || gameResultShown) {
            return
        }

        if (!rewardsReceived) {
            return
        }

        if (ActiveGameManager.isMatchmakingGame && lastEloDiff == null) {
            return
        }

        showGameResult()
    }

    private fun showGameResult() {
        if (gameResultShown) return

        val winner = lastWinner ?: return

        gameResultShown = true
        GameTimerManager.stop()
        backCallback.isEnabled = false

        val userRepository = UserRepository(NetworkUtils.getApiService())

        userRepository.getMyProfile(
            onSuccess = { profile ->
                CurrentUserManager.setMyProfile(profile)
            },
            onError = {
                Log.e("PROFILE", "No se pudo actualizar el perfil tras la partida")
            }
        )

        val dialog = GameResultDialogFragment(
            winner = winner,
            cause = lastCause,
            xpDiff = lastXpDiff,
            moneyDiff = lastMoneyDiff,
            eloDiff = lastEloDiff
        )

        dialog.show(supportFragmentManager, "GameResult")
    }

}
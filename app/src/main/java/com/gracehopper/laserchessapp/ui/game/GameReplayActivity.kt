package com.gracehopper.laserchessapp.ui.game

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.gson.Gson
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.model.game.BoardLayouts
import com.gracehopper.laserchessapp.data.model.game.GameResume
import com.gracehopper.laserchessapp.data.remote.ApiService
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.model.user.AccountResponse
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.gameLogic.board.BoardParser
import com.gracehopper.laserchessapp.gameLogic.laser.LaserUtils
import com.gracehopper.laserchessapp.gameLogic.move.CoordsConverter
import com.gracehopper.laserchessapp.gameLogic.move.MoveParser
import com.gracehopper.laserchessapp.ui.utils.ItemUtils
import com.gracehopper.laserchessapp.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GameReplayActivity : AppCompatActivity() {

    private lateinit var boardM: Board
    private lateinit var game: GameResume

    private var movimientos: List<String> = listOf()
    private var moveIndex = 0

    private lateinit var boardView: ComposeView

    private var imRedPlayer: Boolean = true

    // Skin del rival
    private var opponentPieceSkin: Int = 1

    // Láser del movimiento actual
    private var currentLaserPath: List<Pair<Int, Int>> = emptyList()
    private var currentLaserIsRed: Boolean = true

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { finish() }
        })

        // Pantalla completa
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
        controller.hide(WindowInsetsCompat.Type.systemBars())

        setContentView(R.layout.activity_game_replay)

        apiService = NetworkUtils.getApiService()

        val json = getSharedPreferences("app", Context.MODE_PRIVATE)
            .getString("historyGame", null)

        if (json == null) {
            Log.e("REPLAY", "JSON null")
            finish()
            return
        }

        try {
            game = Gson().fromJson(json, GameResume::class.java)
        } catch (e: Exception) {
            Log.e("REPLAY", "Error parseando JSON", e)
            finish()
            return
        }

        // Determinar si el usuario local es el jugador rojo (p1) o azul (p2)
        val myId = TokenManager.getUserId()
        imRedPlayer = (game.p1Id == myId)

        val opponentId = if (imRedPlayer) game.p2Id else game.p1Id

        boardM = Board(10, 8)

        val csv = BoardLayouts.getCsvForBoard(game.board)
        BoardParser.boardFromCSV(boardM, csv)

        movimientos = game.movementHistory
            .split(";")
            .filter { it.isNotBlank() }

        moveIndex = 0

        setupUI()
        bindLocalPlayer()
        loadOpponentInfo(opponentId)

        updateLaserForCurrentIndex()
        renderBoard()
        updateProfileCard(imRedPlayer)
    }

    /**
     * Rellena el bloque inferior con los datos del jugador local (ya disponibles en memoria).
     */
    private fun bindLocalPlayer() {
        val myProfile = CurrentUserManager.getMyCurrentProfile()
        val avatarView = findViewById<ImageView>(R.id.avatarPlayer)

        val avatarId = myProfile?.avatar?.takeIf { it > 0 } ?: 1
        avatarView.setImageResource(ItemUtils.getItemDrawable(avatarId))
        avatarView.background = getDrawable(R.drawable.bg_avatar_blue_border)
    }

    /**
     * Carga nombre, avatar y skin de piezas del rival vía API.
     */
    private fun loadOpponentInfo(opponentId: Long) {
        apiService.getAccount(opponentId).enqueue(object : Callback<AccountResponse> {
            override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                response.body()?.let { account ->
                    opponentPieceSkin = account.pieceSkin.takeIf { it in 1..3 } ?: 1
                    runOnUiThread {
                        val avatarView = findViewById<ImageView>(R.id.avatarEnemy)
                        val avatarId = account.avatar.takeIf { it > 0 } ?: 1
                        avatarView.setImageResource(ItemUtils.getItemDrawable(avatarId))
                        renderBoard()
                    }
                }
            }
            override fun onFailure(call: Call<AccountResponse>, t: Throwable) { }
        })
    }

    private fun setupUI() {
        boardView = findViewById(R.id.boardReplay)

        val btnNext  = findViewById<ImageButton>(R.id.btnNext)
        val btnPrev  = findViewById<ImageButton>(R.id.btnPrev)
        val btnStart = findViewById<ImageButton>(R.id.btnStart)
        val btnEnd   = findViewById<ImageButton>(R.id.btnEnd)
        val btnBack  = findViewById<ImageButton>(R.id.btnBack)

        btnNext.setOnClickListener  { nextMove() }
        btnPrev.setOnClickListener  { previousMove() }
        btnStart.setOnClickListener { goToStart() }
        btnEnd.setOnClickListener   { goToEnd() }
        btnBack.setOnClickListener  { finish() }
    }

    /**
     * Actualiza el láser que corresponde al estado actual del historial.
     *
     * Si moveIndex > 0, mostramos el láser del último movimiento aplicado
     * (índice moveIndex-1). Si estamos en el inicio, no hay láser.
     */
    private fun updateLaserForCurrentIndex() {
        if (moveIndex == 0 || movimientos.isEmpty()) {
            currentLaserPath = emptyList()
            return
        }

        try {
            val lastMoveStr = movimientos[moveIndex - 1]
            val move = MoveParser.parseMove(lastMoveStr)

            currentLaserPath = LaserUtils.parseLaserPath(move.laserPath)

            val moveActorIsRed = ((moveIndex - 1) % 2 == 0)
            currentLaserIsRed = moveActorIsRed
        } catch (e: Exception) {
            Log.e("REPLAY", "Error parseando láser", e)
            currentLaserPath = emptyList()
        }
    }

    /**
     * Renderiza el tablero con la perspectiva correcta del usuario local,
     * las skins propias y del rival, y el láser del movimiento actual.
     */
    private fun renderBoard() {
        // Actualizar timers en la UI si el movimiento contiene timer
        updateTimerDisplay()
        updateProfileCard(isMyTurn())

        boardView.setContent {
            GameScreen(
                board = boardM,
                isRedPlayer = imRedPlayer,
                isMyTurn = false,
                onPieceSelected = {},
                onMove = { _, _ -> },
                clearSelectionTrigger = moveIndex,
                laserPath = currentLaserPath,
                laserIsRed = currentLaserIsRed,
                renderCoordinates = false,
                myPieceSkin = CurrentUserManager.getMyCurrentPieceSkin(),
                myBoardSkin = CurrentUserManager.getMyCurrentBoardSkin(),
                opponentPieceSkin = opponentPieceSkin
            )
        }
    }

    /**
     * Actualiza los textos de timer según el movimiento actual.
     * El timer almacenado en el movimiento es el tiempo restante del jugador
     * que acaba de mover (en milisegundos).
     */
    private fun updateTimerDisplay() {

        val timerPlayer = findViewById<TextView>(R.id.timePlayer) ?: return
        val timerEnemy  = findViewById<TextView>(R.id.timeEnemy) ?: return

        // Tiempo inicial de ambos jugadores
        var redTime = game.timeBase.toLong()
        var blueTime = game.timeBase.toLong()

        // Recorremos TODOS los movimientos hasta el estado actual
        for (i in 0 until moveIndex) {

            try {

                val move = MoveParser.parseMove(movimientos[i])

                // El movimiento guarda el tiempo restante
                val remainingTime = move.timer

                val moverIsRed = (i % 2 == 0)

                if (moverIsRed) {
                    redTime = remainingTime
                } else {
                    blueTime = remainingTime
                }

            } catch (_: Exception) {
            }
        }

        // Convertir según perspectiva local
        val myTime = if (imRedPlayer) redTime else blueTime
        val enemyTime = if (imRedPlayer) blueTime else redTime

        timerPlayer.text = formatReplayTime(myTime)
        timerEnemy.text = formatReplayTime(enemyTime)
    }

    /* devuelve true si es el turno del jugador loggeado */
    private fun isMyTurn(): Boolean{
        return ((moveIndex - 1) % 2 == 0) == imRedPlayer
    }

    private fun updateProfileCard(myTurn: Boolean){
        val myProfile = findViewById<LinearLayout>(R.id.pillPlayer)
        val opProfile = findViewById<LinearLayout>(R.id.pillEnemy)
        if(myTurn){
            myProfile.setBackgroundResource(R.drawable.bg_avatar_blue_border)
            opProfile.setBackgroundResource(R.drawable.bg_avatar_no_border)
        }else{
            myProfile.setBackgroundResource(R.drawable.bg_avatar_no_border)
            opProfile.setBackgroundResource(R.drawable.bg_avatar_red_border)
        }
    }

    private fun formatReplayTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }

    private fun applyStateMove(moveStr: String) {
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
            'R' -> piece?.rotateRight()
            'L' -> piece?.rotateLeft()
        }

        move.destroyed?.let {
            val destroyedPos = CoordsConverter.notationToPosition(it)
            boardM.setPiece(destroyedPos.first, destroyedPos.second, null)
        }
    }

    /**
     * Reconstruye el tablero desde el inicio hasta moveIndex y actualiza el láser.
     */
    private fun rebuildBoard() {
        boardM.clear()
        val csv = BoardLayouts.getCsvForBoard(game.board)
        BoardParser.boardFromCSV(boardM, csv)

        for (i in 0 until moveIndex) {
            applyStateMove(movimientos[i])
        }

        updateLaserForCurrentIndex()
        renderBoard()
    }

    private fun nextMove() {
        if (moveIndex < movimientos.size) {
            moveIndex++
            rebuildBoard()
        }
    }

    private fun previousMove() {
        if (moveIndex > 0) {
            moveIndex--
            rebuildBoard()
        }
    }

    private fun goToStart() {
        moveIndex = 0
        rebuildBoard()
    }

    private fun goToEnd() {
        moveIndex = movimientos.size
        rebuildBoard()
    }
}
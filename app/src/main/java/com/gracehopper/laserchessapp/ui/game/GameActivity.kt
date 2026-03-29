package com.gracehopper.laserchessapp.ui.game

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.databinding.ActivityGameBinding
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.gameLogic.pieces.Piece
import com.gracehopper.laserchessapp.gameLogic.pieces.PieceType


class GameActivity : AppCompatActivity() {

    companion object {
        var imInternalRed: Boolean = true
        var isMyTurn: Boolean = true
    }

    private val rows = 10
    private val cols = 8
    private lateinit var boardM: Board          // Modelo lógico del tablero
    private var clearTrigger by mutableIntStateOf(0)            // Trigger para avisar a la UI de limpiar selección
    private var selectedPos: Pair<Int, Int>? = null             // Posición de la pieza

    // Bind temporal
    private lateinit var binding: ActivityGameBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
        controller.hide(WindowInsetsCompat.Type.systemBars())

        /* Bind temporal
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.connect.setOnClickListener {
            val apiKey =
        }*/


        setContentView(R.layout.activity_game)

        val board = findViewById<ComposeView>(R.id.board)
        val controls = findViewById<LinearLayout>(R.id.rotationButtons)
        val btnLeft = findViewById<ImageButton>(R.id.btnRotLeft)
        val btnRight = findViewById<ImageButton>(R.id.btnRotRight)

        val btnExit = findViewById<ImageButton>(R.id.btnExit)

        boardM = Board(rows, cols)

        // Piezas prueba
        boardM.setPiece(2,3, Piece(false, PieceType.DEFLECTOR))
        boardM.setPiece(3,3, Piece(true, PieceType.SWITCHER))


        // UI
        board.setContent {
            GameScreen(
                board = boardM,
                isRedPlayer = imInternalRed,
                isMyTurn = isMyTurn,
                onPieceSelected = { pos ->          // Al seleccionar una pieza
                    selectedPos = pos
                    controls.visibility = if (pos != null) View.VISIBLE else View.GONE          // Aparecen ctrls de rot
                },
                onMove = { from, to -> movePiece(from, to) },
                clearSelectionTrigger = clearTrigger
            )
        }

        btnExit.setOnClickListener {
            finish()
        }

        // Rot. izq.
        btnLeft.setOnClickListener {
            selectedPos?.let { (r, c) ->
                val piece = boardM.getPiece(r, c)

                piece?.rotateLeft()

                selectedPos = null
                clearTrigger++
                controls.visibility = View.GONE
            }
        }

        // Rot. der.
        btnRight.setOnClickListener {
            selectedPos?.let { (r, c) ->
                val piece = boardM.getPiece(r, c)

                piece?.rotateRight()

                selectedPos = null
                clearTrigger++
                controls.visibility = View.GONE
            }
        }

    }

    private fun movePiece(from: Pair<Int, Int>, to: Pair<Int, Int>) {

        val (r1, c1) = from
        val (r2, c2) = to

        val pieceFrom = boardM.getPiece(r1, c1)
        val pieceTo = boardM.getPiece(r2, c2)

        if (pieceFrom != null && pieceFrom.type == PieceType.SWITCHER && pieceTo != null) {
            boardM.setPiece(r1,c1, pieceTo)
            boardM.setPiece(r2, c2, pieceFrom)
        } else {
            boardM.setPiece(r2, c2, pieceFrom)
            boardM.setPiece(r1, c1, null)
        }

        selectedPos = null
        clearTrigger++
    }


}
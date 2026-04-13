package com.gracehopper.laserchessapp.ui.game

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.ui.main.MainActivity

/**
 * Fragmento de diálogo que muestra el resultado de la partida.
 *
 * Se encarga de:
 * - Mostrar si el jugador ha ganado o perdido
 * - Indicar la causa de la victoria
 * - Permitir salir de la partida o futura revancha
 */
class GameResultDialogFragment(
    private val winner: String,
    private val cause: String?
) : DialogFragment() {

    private lateinit var textResult: TextView
    private lateinit var textCause: TextView
    private lateinit var buttonRematch: Button
    private lateinit var buttonExit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * El diálogo no se puede cancelar (obligatorio interactuar)
         */
        isCancelable = false
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_game_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textResult = view.findViewById(R.id.textResult)
        textCause = view.findViewById(R.id.textCause)
        buttonRematch = view.findViewById(R.id.buttonRematch)
        buttonExit = view.findViewById(R.id.buttonExit)

        val iAmRed = GameActivity.imInternalRed

        /**
         * Determinar resultado desde la perspectiva del jugador
         */
        val resultText = when (winner) {
            "P1_WINS" -> if (iAmRed) "VICTORIA" else "DERROTA"
            else -> if (iAmRed) "DERROTA" else "VICTORIA"
        }

        /**
         * Determinar causa de la victoria
         */
        val causeText = when (cause) {
            "LASER" -> "Victoria por láser"
            "TIME" -> "Victoria por tiempo"
            else -> ""
        }

        textResult.text = resultText
        textCause.text = causeText

        /**
         * Botón de revancha (pendiente de implementar)
         */
        buttonRematch.setOnClickListener {
            //TODO: Revancha
        }

        /**
         * Salir de la partida y volver al menú principal
         */
        buttonExit.setOnClickListener {
            ActiveGameManager.closeConnection()
            ActiveGameManager.resetAll()

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        /**
         * Configuración visual del diálogo
         */
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setDimAmount(0.6f)

            val params = attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.CENTER
            attributes = params
        }
    }
}
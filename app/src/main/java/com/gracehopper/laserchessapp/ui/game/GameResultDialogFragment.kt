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
    private val cause: String?,
    private val xpDiff: Int,
    private val moneyDiff: Int,
    private val eloDiff: Int?
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * El diálogo no se puede cancelar
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
        val textResult  = view.findViewById<TextView>(R.id.textResult)
        val textRewards = view.findViewById<TextView>(R.id.textRewards)
        val textElo     = view.findViewById<TextView>(R.id.textElo)
        val buttonExit  = view.findViewById<Button>(R.id.buttonExit)

        val iWon = (winner == "P1_WINS") == ActiveGameManager.imRedPlayer

        textResult.text = if (iWon) "¡Has ganado!" else "¡Has perdido!"
        textResult.setTextColor(
            if (iWon) requireContext().getColor(R.color.LCWhite)
            else      requireContext().getColor(R.color.LCWhite)
        )

        textRewards.text = "Has ganado $xpDiff XP y $moneyDiff monedas"

        if (eloDiff != null) {
            textElo.visibility = View.VISIBLE
            val sign = if (eloDiff >= 0) "+" else ""
            textElo.text = "Elo: $sign$eloDiff"
        } else {
            textElo.visibility = View.GONE
        }

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
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setDimAmount(0.6f)
            val p = attributes
            p.width   = ViewGroup.LayoutParams.MATCH_PARENT
            p.height  = ViewGroup.LayoutParams.WRAP_CONTENT
            p.gravity = Gravity.CENTER
            attributes = p
        }
    }
}
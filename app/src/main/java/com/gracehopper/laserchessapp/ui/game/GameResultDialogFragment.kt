package com.gracehopper.laserchessapp.ui.game

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val textResult  = view.findViewById<TextView>(R.id.textResult)
        val textRewards = view.findViewById<TextView>(R.id.textRewards)
        val textElo     = view.findViewById<TextView>(R.id.textElo)
        val buttonExit  = view.findViewById<Button>(R.id.buttonExit)
        val imageResult = view.findViewById<ImageView>(R.id.imageResult)

        val iWon = (winner == "P1_WINS") == ActiveGameManager.imRedPlayer

        textResult.text = if (iWon) "¡Has ganado!" else "¡Has perdido!"
        textResult.setTextColor(
            if (iWon) requireContext().getColor(R.color.LCWhite)
            else      requireContext().getColor(R.color.LCWhite)
        )

        textRewards.text = "Has ganado $xpDiff XP y $moneyDiff monedas"

        // Cargar animación GIF según lo equipado
        val winAnimation = CurrentUserManager.getMyCurrentWinAnimation()
        val gifResId = when (winAnimation) {
            7 -> if (iWon) R.drawable.classic_win else R.drawable.classic_loose
            8 -> if (iWon) R.drawable.soretro_win else R.drawable.soretro_loose
            9 -> if (iWon) R.drawable.cats_win    else R.drawable.cats_loose
            else -> if (iWon) R.drawable.classic_win else R.drawable.classic_loose
        }

        Glide.with(this)
            .asGif()
            .load(gifResId)
            .into(imageResult)

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
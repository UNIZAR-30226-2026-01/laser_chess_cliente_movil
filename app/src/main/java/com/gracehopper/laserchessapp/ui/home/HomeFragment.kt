package com.gracehopper.laserchessapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.data.model.game.GameMode
import com.gracehopper.laserchessapp.ui.game.GameActivity

class HomeFragment : Fragment() {

    private var currentMode = GameMode.BOT
    private var expanded = false

    private lateinit var topMode: GameMode
    private lateinit var middleMode: GameMode

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnPlay = view.findViewById<Button>(R.id.btnPlay)

        val btnGameMode = view.findViewById<ImageButton>(R.id.btnGameMode)
        val btnModeTop = view.findViewById<ImageButton>(R.id.btnModeTop)
        val btnModeMiddle = view.findViewById<ImageButton>(R.id.btnModeMiddle)
        val layoutGamePopup = view.findViewById<View>(R.id.layoutGamePopup)

        refreshMainButton(btnGameMode)

        btnGameMode.setOnClickListener {

            expanded = !expanded

            if (expanded) {
                showModes(layoutGamePopup, btnModeTop, btnModeMiddle)
            } else {
                hideModes(layoutGamePopup, btnGameMode)
            }
        }

        btnModeTop.setOnClickListener {
            currentMode = topMode
            hideModes(layoutGamePopup, btnGameMode)
        }

        btnModeMiddle.setOnClickListener {
            currentMode = middleMode
            hideModes(layoutGamePopup, btnGameMode)
        }

        btnPlay.setOnClickListener {

            when (currentMode) {

                // Cambiar luego por valores reales
                GameMode.BOT -> {

                    ActiveGameManager.createBotGame(
                        board = 1,
                        startingTime = 300,
                        timeIncrement = 2,
                        level = 1
                    )

                    val intent =
                        Intent(requireContext(), GameActivity::class.java)

                    startActivity(intent)
                }

                GameMode.RANKED -> {
                    // matchmaking ranked
                }

                GameMode.PUBLIC -> {
                    // matchmaking publico
                }
            }
        }

        setupSelectors(view)

        return view
    }

    private fun setupSelectors(view: View) {

        val includeBoardSelector = view.findViewById<View>(R.id.includeBoardSelector)
        val txtBoardTitle =
            includeBoardSelector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgBoardIcon =
            includeBoardSelector.findViewById<ImageView>(R.id.imgSelectorIcon)
        val LCRed = ContextCompat.getColor(requireContext(), R.color.LCRed)

        txtBoardTitle.text = "Tablero"
        imgBoardIcon.setImageResource(R.drawable.ic_tablero)
        imgBoardIcon.setColorFilter(LCRed)

        includeBoardSelector.setOnClickListener {
            val boardOptions = listOf("Ace", "Curiosity", "Grail", "Mercury", "Sophie")
            showBottomSheet("Seleccionar tablero", boardOptions, LCRed, txtBoardTitle)
        }

        val includeTimeSelector = view.findViewById<View>(R.id.includeTimeSelector)
        val txtTimeTitle =
            includeTimeSelector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgTimeIcon =
            includeTimeSelector.findViewById<ImageView>(R.id.imgSelectorIcon)
        val LCBlue = ContextCompat.getColor(requireContext(), R.color.LCBlue)

        txtTimeTitle.text = "Modo de tiempo"
        imgTimeIcon.setImageResource(R.drawable.ic_tiempo)
        imgTimeIcon.setColorFilter(LCBlue)

        includeTimeSelector.setOnClickListener {
            val gameModeOptions = listOf("Blitz", "Bullet", "Classic", "Extended")
            showBottomSheet("Seleccionar modo de tiempo", gameModeOptions, LCBlue, txtTimeTitle)
        }
    }

    private fun showBottomSheet(titulo: String, opciones: List<String>, colorTitulo: Int, targetTextView: TextView) {

        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.TemaBottomSheetTransparente)

        val dialogView =
            layoutInflater.inflate(R.layout.dialog_selector_desplegable, null)

        val txtTitle =
            dialogView.findViewById<TextView>(R.id.txtDialogTitle)

        txtTitle.text = titulo
        txtTitle.setTextColor(colorTitulo)

        val container = dialogView.findViewById<LinearLayout>(R.id.layoutOptionsContainer)

        // Cargamos en el selector las opciones que queramos
        // Se puede poner q se seleccionen imágenes en vez de botones de texto
        // pero por ahora nos vale con esto
        for (opcion in opciones) {
            val button = com.google.android.material.button.MaterialButton(requireContext()).apply {
                text = opcion
                setTextColor(ContextCompat.getColor(context, R.color.LCWhite))
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.S2)
                cornerRadius = 36

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }

                setOnClickListener {
                    // TODO: Aquí guardaremos la opción elegida en el futuro
                    targetTextView.text = opcion
                    bottomSheetDialog.dismiss()
                }
            }

            //ñadimos el botón recién creado al contenedor
            container.addView(button)
        }

        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()
    }

    private fun iconFor(mode: GameMode): Int {
        return when (mode) {

            GameMode.BOT ->
                R.drawable.robot_2_48px

            GameMode.RANKED ->
                R.drawable.ic_ranked_mode

            GameMode.PUBLIC ->
                R.drawable.ic_casual_mode
        }
    }

    private fun refreshMainButton(btn: ImageButton) {
        btn.setImageResource(iconFor(currentMode))
    }

    private fun showModes(
        popup: View,
        btnTop: ImageButton,
        btnMiddle: ImageButton
    ) {

        val others = GameMode.values().filter {
            it != currentMode
        }

        topMode = others[0]
        middleMode = others[1]

        btnTop.setImageResource(iconFor(topMode))
        btnMiddle.setImageResource(iconFor(middleMode))

        popup.visibility = View.VISIBLE
    }

    private fun hideModes(
        popup: View,
        btnMain: ImageButton
    ) {
        expanded = false
        popup.visibility = View.GONE
        refreshMainButton(btnMain)
    }
}
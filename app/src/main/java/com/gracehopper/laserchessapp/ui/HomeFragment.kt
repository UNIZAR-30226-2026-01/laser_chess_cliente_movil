package com.gracehopper.laserchessapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.ui.game.GameActivity

// quitar hardcode despues
enum class GameMode {
    BOT,
    RANKED,
    PUBLIC
}

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

                GameMode.BOT -> {
                    val intent = Intent(requireContext(), GameActivity::class.java)
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

        txtBoardTitle.text = "Tablero"
        imgBoardIcon.setImageResource(R.drawable.ic_tablero)
        imgBoardIcon.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.LCRed)
        )

        includeBoardSelector.setOnClickListener {
            showBottomSheet("Seleccionar tablero")
        }

        val includeTimeSelector = view.findViewById<View>(R.id.includeTimeSelector)
        val txtTimeTitle =
            includeTimeSelector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgTimeIcon =
            includeTimeSelector.findViewById<ImageView>(R.id.imgSelectorIcon)

        txtTimeTitle.text = "Modo de tiempo"
        imgTimeIcon.setImageResource(R.drawable.ic_tiempo)
        imgTimeIcon.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.LCBlue)
        )

        includeTimeSelector.setOnClickListener {
            showBottomSheet("Seleccionar tiempo")
        }
    }

    private fun showBottomSheet(titulo: String) {

        val bottomSheetDialog = BottomSheetDialog(requireContext())

        val dialogView =
            layoutInflater.inflate(R.layout.dialog_selector_desplegable, null)

        val txtTitle =
            dialogView.findViewById<TextView>(R.id.txtDialogTitle)

        txtTitle.text = titulo

        val btnOption1 =
            dialogView.findViewById<Button>(R.id.btnOption1)

        btnOption1.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()
    }

    private fun iconFor(mode: GameMode): Int {
        return when (mode) {

            GameMode.BOT ->
                R.drawable.robot_2_48px

            GameMode.RANKED ->
                R.drawable.ic_ranked

            GameMode.PUBLIC ->
                R.drawable.ic_tiempo
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
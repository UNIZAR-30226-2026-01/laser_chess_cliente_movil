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
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.ui.game.GameActivity
import com.gracehopper.laserchessapp.ui.notifications.NotificationsDialogFragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnPlay = view.findViewById<Button>(R.id.btnPlay)
        btnPlay.setOnClickListener {
            val intent = Intent(requireContext(), GameActivity::class.java)
            startActivity(intent)
        }
        
        setupSelectors(view)
        
        return view
    }

    private fun setupSelectors(view: View) {
        var includeBoardSelector = view.findViewById<View>(R.id.includeBoardSelector)
        val txtBoardTitle = includeBoardSelector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgBoardIcon = includeBoardSelector.findViewById<ImageView>(R.id.imgSelectorIcon)

        txtBoardTitle.text = "Tablero"
        imgBoardIcon.setImageResource(R.drawable.ic_tablero)
        imgBoardIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.LCRed))
        includeBoardSelector.setOnClickListener {

        }

        var includeTimeSelector = view.findViewById<View>(R.id.includeTimeSelector)
        val txtTimeTitle = includeTimeSelector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgTimeIcon = includeTimeSelector.findViewById<ImageView>(R.id.imgSelectorIcon)
        txtTimeTitle.text = "Modo de tiempo"
        imgTimeIcon.setImageResource(R.drawable.ic_tiempo)
        imgTimeIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.LCBlue))
        includeBoardSelector.setOnClickListener {

        }
    }

}
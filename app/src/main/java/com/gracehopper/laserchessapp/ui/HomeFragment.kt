package com.gracehopper.laserchessapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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

        val btnSettings = view.findViewById<ImageButton>(R.id.btnSettings)
        val btnPlay = view.findViewById<Button>(R.id.btnPlay)
        val btnNotification = view.findViewById<ImageButton>(R.id.btnNotification)

        btnSettings.setOnClickListener {
            val dialog = SettingsDialogFragment()
            dialog.show(parentFragmentManager, "SettingsDialog")
        }

        btnPlay.setOnClickListener {
            val intent = Intent(requireContext(), GameActivity::class.java)
            startActivity(intent)
        }

        btnNotification.setOnClickListener {
            val dialog = NotificationsDialogFragment()
            dialog.show(parentFragmentManager, "NotificationsDialog")
        }

        return view
    }
}
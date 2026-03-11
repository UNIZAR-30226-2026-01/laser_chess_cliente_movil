package com.gracehopper.laserchessapp.ui.game

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.gracehopper.laserchessapp.R

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val btnExit = findViewById<ImageButton>(R.id.btnExit)

        btnExit.setOnClickListener {
            finish()
        }
    }
}
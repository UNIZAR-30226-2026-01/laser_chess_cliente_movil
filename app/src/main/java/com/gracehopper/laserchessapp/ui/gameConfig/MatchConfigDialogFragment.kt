package com.gracehopper.laserchessapp.ui.gameConfig

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.data.model.game.GameConfig
import com.gracehopper.laserchessapp.data.model.user.TimeMode
import com.gracehopper.laserchessapp.ui.game.WaitingGameDialogFragment

class MatchConfigDialogFragment(
    private val challengedUsername: String
) : DialogFragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var buttonClose: ImageButton
    private lateinit var buttonBack: Button
    private lateinit var buttonNext: Button
    private lateinit var buttonConfirm: Button

    private lateinit var pagerAdapter: MatchConfigPagerAdapter

    private val gameConfig = GameConfig()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_game_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager = view.findViewById(R.id.viewPagerMatchConfig)
        buttonClose = view.findViewById(R.id.buttonCloseMatchConfig)
        buttonBack = view.findViewById(R.id.buttonBackStep)
        buttonNext = view.findViewById(R.id.buttonNextStep)
        buttonConfirm = view.findViewById(R.id.buttonConfirmMatchConfig)

        pagerAdapter = MatchConfigPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        setupListeners()
        updateButtons()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setDimAmount(0.55f)
        }
    }

    fun updateSelectedBoard(boardId: Int, boardName: String) {
        gameConfig.boardId = boardId
        gameConfig.boardName = boardName
    }

    fun updateTimeConfig(
        mode: TimeMode,
        startingTimeSeconds: Int,
        incrementSeconds: Int,
        isCustom: Boolean
    ) {
        gameConfig.mode = mode
        gameConfig.startingTimeSeconds = startingTimeSeconds
        gameConfig.incrementSeconds = incrementSeconds
        gameConfig.isCustom = isCustom
    }

    private fun setupListeners() {
        buttonClose.setOnClickListener { dismiss() }

        buttonBack.setOnClickListener {
            if (viewPager.currentItem > 0) {
                viewPager.currentItem -= 1
            }
        }

        buttonNext.setOnClickListener {
            if (isCurrentStepValid() && viewPager.currentItem < 1) {
                viewPager.currentItem += 1
            }
        }

        buttonConfirm.setOnClickListener {
            if (!isCurrentStepValid()) return@setOnClickListener
            if (gameConfig.boardId == null) return@setOnClickListener

            ActiveGameManager.setCallbacks(
                onConnected = {
                    requireActivity().runOnUiThread {
                        dismiss()
                        WaitingGameDialogFragment().show(
                            parentFragmentManager,
                            "WaitingGameDialog"
                        )
                    }
                },
                onError = { error ->
                    requireActivity().runOnUiThread {
                        // aquí tu toast si quieres
                    }
                },
                onMessageReceived = { message, extra ->
                    requireActivity().runOnUiThread {
                    }
                },
                onClosed = {}
            )

            ActiveGameManager.createChallenge(
                challengedUsername = challengedUsername,
                board = gameConfig.boardId!!,
                startingTime = gameConfig.startingTimeSeconds,
                timeIncrement = gameConfig.incrementSeconds
            )
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateButtons()
            }
        })
    }

    private fun updateButtons() {
        val firstPage = viewPager.currentItem == 0
        val lastPage = viewPager.currentItem == 1

        buttonBack.visibility = if (firstPage) View.INVISIBLE else View.VISIBLE
        buttonNext.visibility = if (lastPage) View.GONE else View.VISIBLE
        buttonConfirm.visibility = if (lastPage) View.VISIBLE else View.GONE
    }

    private fun isCurrentStepValid(): Boolean {
        return when (viewPager.currentItem) {
            0 -> gameConfig.boardId != null
            1 -> gameConfig.startingTimeSeconds > 0 && gameConfig.incrementSeconds >= 0
            else -> false
        }
    }
}
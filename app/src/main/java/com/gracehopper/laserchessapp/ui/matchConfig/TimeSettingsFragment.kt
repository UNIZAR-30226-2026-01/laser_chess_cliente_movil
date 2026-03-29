package com.gracehopper.laserchessapp.ui.matchConfig

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.user.TimeMode
import com.gracehopper.laserchessapp.data.model.user.TimeModeConfig

class TimeSettingsFragment : Fragment() {

    private lateinit var spinnerMode: Spinner
    private lateinit var spinnerIncrement: Spinner
    private lateinit var checkboxCustom: CheckBox
    private lateinit var editCustomTimeMinutes: EditText
    private lateinit var editCustomIncrementSeconds: EditText

    private val parentConfigDialog: MatchConfigDialogFragment?
        get() = parentFragment as? MatchConfigDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_time_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        spinnerMode = view.findViewById(R.id.spinnerTimeMode)
        spinnerIncrement = view.findViewById(R.id.spinnerIncrement)
        checkboxCustom = view.findViewById(R.id.checkboxCustomConfig)
        editCustomTimeMinutes = view.findViewById(R.id.editCustomTimeMinutes)
        editCustomIncrementSeconds = view.findViewById(R.id.editCustomIncrementSeconds)

        setupModeSpinner()
        setupCustomConfig()
        pushCurrentConfigToParent()
    }

    private fun setupModeSpinner() {
        val modeNames = TimeMode.entries.map { TimeModeConfig.getName(it) }

        spinnerMode.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            modeNames
        )

        spinnerMode.setSelection(0)
        updateIncrementSpinner(TimeMode.BLITZ)
    }

    private fun updateIncrementSpinner(mode: TimeMode) {
        val increments = TimeModeConfig.getAllowedIncrements(mode).map { "${it}s" }

        spinnerIncrement.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            increments
        )

        spinnerIncrement.setSelection(0)
        pushCurrentConfigToParent()
    }

    private fun setupCustomConfig() {
        checkboxCustom.setOnCheckedChangeListener { _, isChecked ->
            spinnerMode.isEnabled = !isChecked
            spinnerIncrement.isEnabled = !isChecked

            editCustomTimeMinutes.isEnabled = isChecked
            editCustomIncrementSeconds.isEnabled = isChecked

            if (!isChecked) {
                val selectedMode = TimeMode.entries[spinnerMode.selectedItemPosition]
                updateIncrementSpinner(selectedMode)
            }

            pushCurrentConfigToParent()
        }

        spinnerMode.onItemSelectedListener = SimpleItemSelectedListener { position ->
            val selectedMode = TimeMode.entries[position]
            updateIncrementSpinner(selectedMode)
        }

        spinnerIncrement.onItemSelectedListener = SimpleItemSelectedListener {
            pushCurrentConfigToParent()
        }
    }

    override fun onPause() {
        super.onPause()
        pushCurrentConfigToParent()
    }

    private fun pushCurrentConfigToParent() {
        if (checkboxCustom.isChecked) {
            val customMinutes = editCustomTimeMinutes.text.toString().toIntOrNull() ?: 5
            val customIncrement = editCustomIncrementSeconds.text.toString().toIntOrNull() ?: 0

            val validMinutes = customMinutes.coerceIn(1, 180)
            val validIncrement = customIncrement.coerceIn(0, 60)

            parentConfigDialog?.updateTimeConfig(
                mode = TimeMode.entries[spinnerMode.selectedItemPosition],
                startingTimeSeconds = validMinutes * 60,
                incrementSeconds = validIncrement,
                isCustom = true
            )
        } else {
            val selectedMode = TimeMode.entries[spinnerMode.selectedItemPosition]
            val increments = TimeModeConfig.getAllowedIncrements(selectedMode)
            val incrementPosition = spinnerIncrement.selectedItemPosition.coerceIn(increments.indices)

            parentConfigDialog?.updateTimeConfig(
                mode = selectedMode,
                startingTimeSeconds = TimeModeConfig.getBaseTimeSeconds(selectedMode),
                incrementSeconds = increments[incrementPosition],
                isCustom = false
            )
        }
    }

    companion object {
        fun newInstance(): TimeSettingsFragment {
            return TimeSettingsFragment()
        }
    }
}
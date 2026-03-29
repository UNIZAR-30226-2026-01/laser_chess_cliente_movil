package com.gracehopper.laserchessapp.ui.matchConfig

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

    private val selectableModes = listOf(
        TimeMode.BLITZ,
        TimeMode.RAPID,
        TimeMode.CLASSIC,
        TimeMode.EXTENDED
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
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
        setupCustomTextWatchers()
        pushCurrentConfigToParent()
    }

    private fun setupModeSpinner() {
        val modeNames = selectableModes.map { TimeModeConfig.getName(it) }

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
            val selectedMode = selectableModes[position]
            updateIncrementSpinner(selectedMode)
        }

        spinnerIncrement.onItemSelectedListener = SimpleItemSelectedListener {
            pushCurrentConfigToParent()
        }
    }

    private fun setupCustomTextWatchers() {

        val watcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int,
                                           count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int,
                                       before: Int, count: Int) {
                if (checkboxCustom.isChecked) {
                    pushCurrentConfigToParent()
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit

        }

        editCustomTimeMinutes.addTextChangedListener(watcher)
        editCustomIncrementSeconds.addTextChangedListener(watcher)

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
                mode = TimeMode.CUSTOM,
                startingTimeSeconds = validMinutes * 60,
                incrementSeconds = validIncrement,
                isCustom = true
            )

        } else {

            val selectedMode = selectableModes[spinnerMode.selectedItemPosition]
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
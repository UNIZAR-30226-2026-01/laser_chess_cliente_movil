package com.gracehopper.laserchessapp.ui.matchConfig

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MatchConfigPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BoardSelectionFragment.newInstance()
            1 -> TimeSettingsFragment.newInstance()
            else -> throw IllegalArgumentException("Página no válida")
        }
    }
}
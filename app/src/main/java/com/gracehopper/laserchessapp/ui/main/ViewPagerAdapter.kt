package com.gracehopper.laserchessapp.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gracehopper.laserchessapp.ui.ranking.RankingFragment
import com.gracehopper.laserchessapp.ui.HomeFragment
import com.gracehopper.laserchessapp.ui.CustomizeFragment
import com.gracehopper.laserchessapp.ui.social.SocialFragment
import com.gracehopper.laserchessapp.ui.ShopFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 5
    }
    
    /**
     * Devuelve el fragmento correspondiente a cada pestaña
     */
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ShopFragment()
            1 -> CustomizeFragment()
            2 -> HomeFragment()
            3 -> SocialFragment()
            4 -> RankingFragment()
            else -> HomeFragment()
        }
    }
}
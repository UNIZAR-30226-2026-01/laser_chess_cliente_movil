package com.gracehopper.laserchessapp.ui.home

import com.gracehopper.laserchessapp.R

data class RankInfo(val min: Int, val max: Int, val name: String, val drawableRes: Int)

object RankUtils {

    val RANK_TABLE = listOf(
        RankInfo(0,    999,  "Fotón I",     R.drawable.ic_rank_foton),
        RankInfo(1000, 1049, "Fotón II",    R.drawable.ic_rank_foton),
        RankInfo(1050, 1099, "Quark I",     R.drawable.ic_rank_quark),
        RankInfo(1100, 1149, "Quark II",    R.drawable.ic_rank_quark),
        RankInfo(1150, 1199, "Quark III",   R.drawable.ic_rank_quark),
        RankInfo(1200, 1249, "Electron I",  R.drawable.ic_rank_electron),
        RankInfo(1250, 1299, "Electron II", R.drawable.ic_rank_electron),
        RankInfo(1300, 1349, "Electron III",R.drawable.ic_rank_electron),
        RankInfo(1350, 1399, "Electron IV", R.drawable.ic_rank_electron),
        RankInfo(1400, 1449, "Protón I",    R.drawable.ic_rank_proton),
        RankInfo(1450, 1499, "Protón II",   R.drawable.ic_rank_proton),
        RankInfo(1500, 1549, "Protón III",  R.drawable.ic_rank_proton),
        RankInfo(1550, 1599, "Protón IV",   R.drawable.ic_rank_proton),
        RankInfo(1600, 1649, "Neutrón I",   R.drawable.ic_rank_neutron),
        RankInfo(1650, 1699, "Neutrón II",  R.drawable.ic_rank_neutron),
        RankInfo(1700, 1749, "Neutrón III", R.drawable.ic_rank_neutron),
        RankInfo(1750, 1799, "Neutrón IV",  R.drawable.ic_rank_neutron),
        RankInfo(1800, 1849, "Átomo I",     R.drawable.ic_rank_atom),
        RankInfo(1850, 1899, "Átomo II",    R.drawable.ic_rank_atom),
        RankInfo(1900, 1949, "Átomo III",   R.drawable.ic_rank_atom),
        RankInfo(1950, 1999, "Átomo IV",    R.drawable.ic_rank_atom),
        RankInfo(2000, Int.MAX_VALUE, "Átomo V", R.drawable.ic_rank_atom),
    )

    fun getRankInfo(elo: Int): RankInfo =
        RANK_TABLE.find { elo >= it.min && elo <= it.max } ?: RANK_TABLE.last()

    fun getProgressPercent(elo: Int): Int {
        val rank = getRankInfo(elo)
        if (rank.max == Int.MAX_VALUE) return 100
        val nextMin = RANK_TABLE.find { it.min > elo }?.min ?: rank.min
        return ((elo - rank.min).toFloat() / (nextMin - rank.min) * 100).toInt().coerceIn(0, 100)
    }
}
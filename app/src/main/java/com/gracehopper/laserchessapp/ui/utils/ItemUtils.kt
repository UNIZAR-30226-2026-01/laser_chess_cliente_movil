package com.gracehopper.laserchessapp.ui.utils

import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.shop.ItemMetadata
import com.gracehopper.laserchessapp.data.model.shop.ItemType

/**
 * Objeto utilitario para obtener recursos de ítems
 */
object ItemUtils {

    fun getItemDrawable(itemId: Int): Int {
        return getItemMetadata(itemId).drawableRes
    }

    fun getItemName(itemId: Int): String {
        return getItemMetadata(itemId).name
    }

    private fun getItemMetadata(itemId: Int): ItemMetadata {

        return when (itemId) {

            // PIECE_SKIN
            1 -> ItemMetadata("Classic", R.drawable.kin_b_classic)
            2 -> ItemMetadata("Soretro", R.drawable.kin_r_cats) // TODO Cambiar cuando funke soretro
            3 -> ItemMetadata("Cats", R.drawable.kin_b_cats)

            // BOARD_SKIN
            4 -> ItemMetadata("Classic", R.drawable.bg_classic)
            5 -> ItemMetadata("Soretro", R.drawable.bg_soretro)
            6 -> ItemMetadata("Cats", R.drawable.bg_cats)

            // WIN_ANIMATION TODO: CAMBIAR CUANDO HAYA LOL
            7 -> ItemMetadata("Classic", R.drawable.kin_r_cats)
            8 -> ItemMetadata("Soretro", R.drawable.kin_r_cats)
            9 -> ItemMetadata("Cats", R.drawable.kin_r_cats)

            // AVATAR TODO: CAMBIAR NOMBRES CUANDO SE DECIDAN
            10 -> ItemMetadata("robotito1", R.drawable.bot1)
            11 -> ItemMetadata("robotito2", R.drawable.bot2)
            12 -> ItemMetadata("robotito3", R.drawable.bot3)
            13 -> ItemMetadata("robotito4", R.drawable.bot4)
            14 -> ItemMetadata("robotito5", R.drawable.bot5)
            15 -> ItemMetadata("robotito6", R.drawable.bot6)
            16 -> ItemMetadata("robotito7", R.drawable.bot7)
            17 -> ItemMetadata("robotito8", R.drawable.bot8)
            18 -> ItemMetadata("robotito9", R.drawable.bot9)
            19 -> ItemMetadata("robotito10", R.drawable.bot10)
            20 -> ItemMetadata("robotito11", R.drawable.bot11)
            21 -> ItemMetadata("mividaentera", R.drawable.bot12)

            else -> throw IllegalArgumentException("Invalid item ID: $itemId")
        }

    }

    fun getPiecesPackDrawables(itemId: Int): List<Int> {
        return when (itemId) {

            1 -> listOf(
                R.drawable.kin_b_classic,
                R.drawable.esc_b_classic,
                R.drawable.def_b_classic,
                R.drawable.swi_b_classic,
                R.drawable.las_b_classic
            )

            2 -> listOf(
                R.drawable.kin_r_cats,
                R.drawable.esc_r_cats,
                R.drawable.def_r_cats,
                R.drawable.swi_r_cats,
                R.drawable.las_r_cats
            )

            3 -> listOf(
                R.drawable.kin_b_cats,
                R.drawable.esc_b_cats,
                R.drawable.def_b_cats,
                R.drawable.swi_b_cats,
                R.drawable.las_b_cats
            )

            else -> throw IllegalArgumentException("Invalid piece_skin ID: $itemId")

        }
    }

}
package com.gracehopper.laserchessapp.ui.utils

import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.shop.ItemMetadata

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
            2 -> ItemMetadata("Soretro", R.drawable.kin_b_soretro)
            3 -> ItemMetadata("Cats", R.drawable.kin_b_cats)

            // BOARD_SKIN
            4 -> ItemMetadata("Classic", R.drawable.bg_classic)
            5 -> ItemMetadata("Soretro", R.drawable.bg_soretro)
            6 -> ItemMetadata("Cats", R.drawable.bg_cats)

            // WIN_ANIMATION
            7 -> ItemMetadata("Classic", R.drawable.classic_win)
            8 -> ItemMetadata("Soretro", R.drawable.soretro_win)
            9 -> ItemMetadata("Cats", R.drawable.cats_win)

            // AVATAR
            10 -> ItemMetadata("grace", R.drawable.bot1)
            11 -> ItemMetadata("mimi", R.drawable.bot2)
            12 -> ItemMetadata("bob", R.drawable.bot3)
            13 -> ItemMetadata("malamar", R.drawable.bot4)
            14 -> ItemMetadata("sónar", R.drawable.bot5)
            15 -> ItemMetadata("bibble", R.drawable.bot6)
            16 -> ItemMetadata("euridice", R.drawable.bot7)
            17 -> ItemMetadata("davíh", R.drawable.bot8)
            18 -> ItemMetadata("mia", R.drawable.bot9)
            19 -> ItemMetadata("polix", R.drawable.bot10)
            20 -> ItemMetadata("mudkip", R.drawable.bot11)
            21 -> ItemMetadata("carolai", R.drawable.bot12)

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
                R.drawable.kin_b_soretro,
                R.drawable.esc_b_soretro,
                R.drawable.def_b_soretro,
                R.drawable.swi_b_soretro,
                R.drawable.las_b_soretro
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

    fun getOpponentPiecesPackDrawables(itemId: Int): List<Int> {

        return when (itemId) {

            1 -> listOf(
                R.drawable.kin_r_classic,
                R.drawable.esc_r_classic,
                R.drawable.def_r_classic,
                R.drawable.swi_r_classic,
                R.drawable.las_r_classic
            )

            2 -> listOf(
                R.drawable.kin_r_soretro,
                R.drawable.esc_r_soretro,
                R.drawable.def_r_soretro,
                R.drawable.swi_r_soretro,
                R.drawable.las_r_soretro
            )

            3 -> listOf(
                R.drawable.kin_r_cats,
                R.drawable.esc_r_cats,
                R.drawable.def_r_cats,
                R.drawable.swi_r_cats,
                R.drawable.las_r_cats
            )

            else -> listOf(
                R.drawable.kin_r_classic,
                R.drawable.esc_r_classic,
                R.drawable.def_r_classic,
                R.drawable.swi_r_classic,
                R.drawable.las_r_classic
            )

        }

    }

    fun getBlueRune(boardSkinId: Int): Int {

        return when (boardSkinId) {

            4 -> R.drawable.rune_b_classic
            5 -> R.drawable.rune_b_soretro
            6 -> R.drawable.rune_b_cats

            else -> R.drawable.rune_b_classic
        }
    }

    fun getRedRune(boardSkinId: Int): Int {

        return when (boardSkinId) {

            4 -> R.drawable.rune_r_classic
            5 -> R.drawable.rune_r_soretro
            6 -> R.drawable.rune_cats_r

            else -> R.drawable.rune_r_classic
        }
    }


}
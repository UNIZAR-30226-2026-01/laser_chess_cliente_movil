package com.gracehopper.laserchessapp.ui.utils

import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.shop.ItemType

/**
 * Objeto utilitario para obtener recursos de ítems
 */
object ItemUtils {

    fun getItemDrawable(itemType: ItemType, itemId: Int): Int {
        return when (itemType) {
            ItemType.PIECE_SKIN -> getPieceSkinDrawable(itemId)
            ItemType.BOARD_SKIN -> getBoardSkinDrawable(itemId)
            ItemType.WIN_ANIMATION -> getWinAnimationDrawable(itemId)
        }
    }

    fun getPieceSkinDrawable(skinId: Int): Int {
        return when (skinId) {
            1 -> R.drawable.piece_skin_1
            2 -> R.drawable.piece_skin_2
            3 -> R.drawable.piece_skin_3
            4 -> R.drawable.piece_skin_4
            5 -> R.drawable.piece_skin_5
            else -> R.drawable.piece_skin_1
        }
    }

    fun getBoardSkinDrawable(skinId: Int): Int {
        return when (skinId) {
            1 -> R.drawable.board_skin_1
            2 -> R.drawable.board_skin_2
            3 -> R.drawable.board_skin_3
            4 -> R.drawable.board_skin_4
            5 -> R.drawable.board_skin_5
            else -> R.drawable.board_skin_1
        }
    }

    fun getWinAnimationDrawable(animationId: Int): Int {
        return when (animationId) {
            1 -> R.drawable.win_animation_1
            2 -> R.drawable.win_animation_2
            3 -> R.drawable.win_animation_3
            4 -> R.drawable.win_animation_4
            5 -> R.drawable.win_animation_5
            else -> R.drawable.win_animation_1
        }
    }

    fun getItemTypeDisplayName(itemType: ItemType): String {
        return when (itemType) {
            ItemType.PIECE_SKIN -> "Piezas"
            ItemType.BOARD_SKIN -> "Tablero"
            ItemType.WIN_ANIMATION -> "Animación"
        }
    }

}
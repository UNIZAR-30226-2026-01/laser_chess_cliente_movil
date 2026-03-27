package com.gracehopper.laserchessapp.ui.utils

import com.gracehopper.laserchessapp.R

object AvatarUtils {

    fun getAvatarDrawable(avatarId: Int): Int {
        return when (avatarId) {
            1 -> R.drawable.avatar_1
            2 -> R.drawable.avatar_2
            3 -> R.drawable.avatar_3
            4 -> R.drawable.avatar_4
            else -> R.drawable.ic_avatar
        }
    }

}
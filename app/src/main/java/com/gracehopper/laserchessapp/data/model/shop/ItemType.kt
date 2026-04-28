package com.gracehopper.laserchessapp.data.model.shop

import com.google.gson.annotations.SerializedName

enum class ItemType {
    @SerializedName("BOARD_SKIN")
    BOARD_SKIN,

    @SerializedName("PIECE_SKIN")
    PIECE_SKIN,

    @SerializedName("WIN_ANIMATION")
    WIN_ANIMATION
}
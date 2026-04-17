package com.gracehopper.laserchessapp.data.model.game

import com.google.gson.annotations.SerializedName

data class WSServerMessage (
    @SerializedName("Type") val type: GameMessageType,
    @SerializedName("Content") val content: String? = null,
    @SerializedName("Extra") val extra: String? = null
)
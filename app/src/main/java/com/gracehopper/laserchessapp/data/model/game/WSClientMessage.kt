package com.gracehopper.laserchessapp.data.model.game

import com.google.gson.annotations.SerializedName

data class WSClientMessage (
    @SerializedName("Type") val type: String,
    @SerializedName("Content") val content: String
)
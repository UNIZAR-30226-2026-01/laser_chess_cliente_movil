package com.gracehopper.laserchessapp.data.model.ranking

import com.google.gson.annotations.SerializedName

data class AllRatingsResponse (
    @SerializedName("user_id") val userId: String,
    @SerializedName("blitz") val blitz: Int,
    @SerializedName("rapid") val rapid: Int,
    @SerializedName("classic") val classic: Int,
    @SerializedName("extended") val extended: Int
)
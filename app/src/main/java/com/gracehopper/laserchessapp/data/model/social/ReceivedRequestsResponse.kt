package com.gracehopper.laserchessapp.data.model.social

import com.google.gson.annotations.SerializedName

data class ReceivedRequestsResponse (
    @SerializedName("count") val count : Int
)
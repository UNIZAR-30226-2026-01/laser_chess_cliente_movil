package com.gracehopper.laserchessapp.data.model.shop

import com.google.gson.annotations.SerializedName

data class BuyItemRequest(
    @SerializedName("item_id") val itemId: Int
)
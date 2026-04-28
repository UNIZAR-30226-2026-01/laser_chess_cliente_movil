package com.gracehopper.laserchessapp.data.model.shop

import com.google.gson.annotations.SerializedName

data class ShopItem(
    @SerializedName("item_id")
    val itemId: Int,

    val price: Int,

    @SerializedName("level_requisite")
    val levelRequisite: Int,

    @SerializedName("item_type")
    val itemType: ItemType,

    @SerializedName("is_default")
    val isDefault: Boolean
)
package com.gracehopper.laserchessapp.data.model.shop

data class ShopProduct(
    val itemId: Int,
    val name: String,
    val itemType: ItemType,
    val price: Int,
    val levelRequisite: Int = 0,
    val isDefault: Boolean = false
)
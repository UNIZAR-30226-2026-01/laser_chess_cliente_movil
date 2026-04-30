package com.gracehopper.laserchessapp.data.repository

import android.util.Log
import com.gracehopper.laserchessapp.data.model.shop.BuyItemRequest
import com.gracehopper.laserchessapp.data.model.shop.ShopItem
import com.gracehopper.laserchessapp.data.model.shop.ShopProduct
import com.gracehopper.laserchessapp.data.remote.ApiService
import com.gracehopper.laserchessapp.ui.utils.ItemUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemRepository(
    private val apiService: ApiService
) {

    fun getShopProducts(
        onSuccess: (List<ShopProduct>) -> Unit,
        onError: (String) -> Unit
    ) {

        getInventory(
            onSuccess = { inventory ->

                val ownedIds = inventory.map { it.itemId }.toSet()

                getAllShopItems(
                    onSuccess = { items ->

                        val products = items.map { item ->
                            ShopProduct(
                                itemId = item.itemId,
                                name = ItemUtils.getItemName(item.itemId),
                                itemType = item.itemType,
                                price = item.price,
                                levelRequisite = item.levelRequisite,
                                isDefault = item.isDefault,
                                isOwned = ownedIds.contains(item.itemId)
                            )
                        }

                        onSuccess(products)

                    },
                    onError = onError
                )
            },
            onError = onError
        )

    }

    fun getAllShopItems(
        onSuccess: (List<ShopItem>) -> Unit,
        onError: (String) -> Unit
    ) {

        apiService.getAllShopItems().enqueue(
            object : Callback<List<ShopItem>> {

                override fun onResponse(
                    call: Call<List<ShopItem>>,
                    response: Response<List<ShopItem>>
                ) {

                    Log.d("ITEM_REPO", "GET /api/item/all HTTP=${response.code()}")
                    Log.d("ITEM_REPO", "Body parseado=${response.body()}")
                    Log.d("ITEM_REPO", "ErrorBody=${response.errorBody()?.string()}")

                    if (response.isSuccessful) {
                        onSuccess(response.body().orEmpty())

                        response.body()?.forEachIndexed { index, item ->
                            Log.d(
                                "ITEM_REPO",
                                "[$index] id=${item.itemId}, type=${item.itemType}, price=${item.price}, level=${item.levelRequisite}, default=${item.isDefault}"
                            )
                        }

                    } else {
                        onError("Error cargando tienda: ${response.code()}")
                    }

                }

                override fun onFailure(call: Call<List<ShopItem>>, t: Throwable) {
                    onError(t.message ?: "Error de conexión")
                }
            }
        )

    }

    fun getInventory(
        onSuccess: (List<ShopItem>) -> Unit,
        onError: (String) -> Unit
    ) {

        apiService.getInventory().enqueue(object : Callback<List<ShopItem>> {

            override fun onResponse(
                call: Call<List<ShopItem>>,
                response: Response<List<ShopItem>>
            ) {

                if (response.isSuccessful) {
                    onSuccess(response.body().orEmpty())
                } else {
                    onError("Error cargando inventario: ${response.code()}")
                }

            }

            override fun onFailure(call: Call<List<ShopItem>?>, t: Throwable) {
                onError(t.message ?: "Error de conexión")
            }

        })

    }

    fun getItem(
        itemId: Int,
        onSuccess: (ShopItem) -> Unit,
        onError: (String) -> Unit
    ) {

        apiService.getItem(itemId).enqueue(object : Callback<ShopItem> {

            override fun onResponse(call: Call<ShopItem?>, response: Response<ShopItem?>) {
                val item = response.body()

                if (response.isSuccessful && item != null) {
                    onSuccess(item)
                } else {
                    onError("Error cargando item: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ShopItem>, t: Throwable) {
                onError(t.message ?: "Error de conexión")
            }

        })

    }

    fun buyItem(itemId: Int,
                onSuccess: () -> Unit,
                onError: (String) -> Unit) {

        apiService.buyItem(BuyItemRequest(itemId)).enqueue(object : Callback<Unit> {

            override fun onResponse(call: Call<Unit>,
                                    response: Response<Unit>) {

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error comprando item: ${response.code()}")
                }

            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError(t.message ?: "Error de conexión")
            }

        })

    }

}
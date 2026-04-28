package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.shop.BuyItemRequest
import com.gracehopper.laserchessapp.data.model.shop.ShopItem
import com.gracehopper.laserchessapp.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemRepository(
    private val apiService: ApiService
) {

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

                    if (response.isSuccessful) {
                        onSuccess(response.body().orEmpty())
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
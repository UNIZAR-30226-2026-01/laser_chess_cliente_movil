package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.shop.ItemType
import com.gracehopper.laserchessapp.data.model.shop.ShopItem
import com.gracehopper.laserchessapp.data.remote.ApiService
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Call

class ItemRepositoryTest {

    /**
     * TEST 1: ÉXITO
     *
     * Comprueba:
     * - respuesta correcta
     * -> llama a onSuccess
     */
    @Test
    fun getAllShopItems_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<ShopItem>>>()

        val repository = ItemRepository(apiService)

        val response = listOf(
            ShopItem(1, 100, 10, ItemType.BOARD_SKIN, false)
        )

        whenever(apiService.getAllShopItems()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<retrofit2.Callback<List<ShopItem>>>(0)
            callback.onResponse(call, retrofit2.Response.success(response))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var result: List<ShopItem>? = null

        repository.getAllShopItems(
            onSuccess = {
                successCalled = true
                result = it
            },
            onError = {}
        )

        assertTrue(successCalled)
        assertEquals(response, result)

    }

    // TODO: Terminar test

}
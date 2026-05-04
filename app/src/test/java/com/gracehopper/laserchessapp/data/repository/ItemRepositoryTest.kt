package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.shop.ItemType
import com.gracehopper.laserchessapp.data.model.shop.ShopItem
import com.gracehopper.laserchessapp.data.remote.ApiService
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemRepositoryTest {

    private fun sampleItem(
        id: Int,
        price: Int = 100,
        level: Int = 0,
        type: ItemType = ItemType.BOARD_SKIN,
        default: Boolean = false
    ) = ShopItem(
        itemId = id,
        price = price,
        levelRequisite = level,
        itemType = type,
        isDefault = default
    )

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

        val response = listOf(sampleItem(1), sampleItem(2))

        whenever(apiService.getAllShopItems()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<ShopItem>>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCalled = false
        var result: List<ShopItem>? = null

        repository.getAllShopItems(
            onSuccess = {
                successCalled = true
                result = it
            },
            onError = {
                errorCalled = true
            }
        )

        assertTrue(successCalled)
        assertFalse(errorCalled)
        assertEquals(response, result)
    }

    /**
     * TEST 2: ÉXITO CON BODY NULO
     *
     * Comprueba:
     * - respuesta con body nulo
     * -> llama a onSuccess con una lista vacía
     */
    @Test
    fun getAllShopItems_body_null_onSuccess_emptyList() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<ShopItem>>>()

        val repository = ItemRepository(apiService)

        whenever(apiService.getAllShopItems()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<ShopItem>>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.whenever(call).enqueue(any())

        var result: List<ShopItem>? = null

        repository.getAllShopItems(
            onSuccess = {
                result = it
            },
            onError = {}
        )

        assertEquals(emptyList<ShopItem>(), result)

    }

    /**
     * TEST 3: ERROR 500 DEL SERVIDOR
     *
     * Comprueba:
     * - error del servidor
     * -> llama a onError
     */
    @Test
    fun getAllShopItems_error_500_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<ShopItem>>>()

        val repository = ItemRepository(apiService)

        whenever(apiService.getAllShopItems()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<ShopItem>>>(0)
            callback.onResponse(call, Response.error(500, "Server error".toResponseBody(null)))
            null
        }.whenever(call).enqueue(any())

        var error: String? = null

        repository.getAllShopItems(
            onSuccess = {},
            onError = {
                error = it
            }
        )

        assertEquals("Error cargando tienda: 500", error)

    }

    /**
     * TEST 4: ERROR POR FALLO DE RED
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError
     */
    @Test
    fun getAllShopItems_fallo_red_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<ShopItem>>>()

        val repository = ItemRepository(apiService)

        whenever(apiService.getAllShopItems()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<ShopItem>>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var error: String? = null

        repository.getAllShopItems(
            onSuccess = {},
            onError = {
                error = it
            }
        )

        assertEquals("Network error", error)

    }


}
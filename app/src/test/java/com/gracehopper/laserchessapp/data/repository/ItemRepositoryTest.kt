package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.shop.BuyItemRequest
import com.gracehopper.laserchessapp.data.model.shop.ItemType
import com.gracehopper.laserchessapp.data.model.shop.ShopItem
import com.gracehopper.laserchessapp.data.model.shop.ShopProduct
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
     * TEST 1: ÉXITO AL RECUPERAR TODOS LOS ITEMS DE LA TIENDA
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
     * TEST 2: ÉXITO CON BODY NULO AL RECUPERAR TODOS LOS ITEMS DE LA TIENDA
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
     * TEST 3: ERROR 500 DEL SERVIDOR AL RECUPERAR TODOS LOS ITEMS DE LA TIENDA
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
     * TEST 4: ERROR POR FALLO DE RED AL RECUPERAR TODOS LOS ITEMS DE LA TIENDA
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

    /**
     * TEST 5: ÉXITO AL RECUPERAR EL INVENTARIO
     */
    @Test
    fun getInventory_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<ShopItem>>>()

        val repository = ItemRepository(apiService)

        val inventory = listOf(sampleItem(1))

        whenever(apiService.getInventory()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<ShopItem>>>(0)
            callback.onResponse(call, Response.success(inventory))
            null
        }.whenever(call).enqueue(any())

        var result: List<ShopItem>? = null

        repository.getInventory(
            onSuccess = {
                result = it
            },
            onError = {}
        )

        assertEquals(inventory, result)

    }

    /**
     * TEST 6: ÉXITO CON BODY NULO AL RECUPERAR EL INVENTARIO
     *
     * Comprueba:
     * - respuesta con body nulo
     * -> llama a onSuccess con una lista vacía
     */
    @Test
    fun getInventory_body_null_onSuccess_emptyList() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<ShopItem>>>()

        val repository = ItemRepository(apiService)

        whenever(apiService.getInventory()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<ShopItem>>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.whenever(call).enqueue(any())

        var result: List<ShopItem>? = null

        repository.getInventory(
            onSuccess = {
                result = it
            },
            onError = {}
        )

        assertEquals(emptyList<ShopItem>(), result)

    }

    /**
     * TEST 7: ERROR DEL SERVIDOR AL RECUPERAR EL INVENTARIO
     *
     * Comprueba:
     * - error del servidor
     * -> llama a onError con el código 500
     */
    @Test
    fun getInventory_error_500_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<ShopItem>>>()

        val repository = ItemRepository(apiService)

        whenever(apiService.getInventory()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<ShopItem>>>(0)
            callback.onResponse(call, Response.error(500, "Server error".toResponseBody(null)))
            null
        }.whenever(call).enqueue(any())

        var error: String? = null

        repository.getInventory(
            onSuccess = {},
            onError = {
                error = it
            }
        )

        assertEquals("Error cargando inventario: 500", error)

    }

    /**
     * TEST 8: ERROR POR FALLO DE RED AL RECUPERAR EL INVENTARIO
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError
     */
    @Test
    fun getInventory_fallo_red_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<ShopItem>>>()

        val repository = ItemRepository(apiService)

        whenever(apiService.getInventory()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<ShopItem>>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var error: String? = null

        repository.getInventory(
            onSuccess = {},
            onError = {
                error = it
            }
        )

        assertEquals("Network error", error)

    }

    /**
     * TEST 9: ÉXITO AL RECUPERAR UN ITEM
     *
     * Comprueba:
     * - respuesta correcta
     * -> llama a onSuccess
     */
    @Test
    fun getItem_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<ShopItem>>()

        val repository = ItemRepository(apiService)

        val item = sampleItem(1)

        whenever(apiService.getItem(1)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<ShopItem>>(0)
            callback.onResponse(call, Response.success(item))
            null
        }.whenever(call).enqueue(any())

        var result: ShopItem? = null

        repository.getItem(1,
            onSuccess = {
                result = it
            },
            onError = {}
        )

        assertEquals(item, result)

    }

    /**
     * TEST 10: ERROR, RESPUESTA CON BODY NULO AL RECUPERAR UN ITEM
     *
     * Comprueba:
     * - respuesta con body nulo
     * -> llama a onError
     */
    @Test
    fun getItem_body_null_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<ShopItem>>()

        val repository = ItemRepository(apiService)

        whenever(apiService.getItem(1)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<ShopItem>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.whenever(call).enqueue(any())

        var error: String? = null

        repository.getItem(1,
            onSuccess = {},
            onError = {
                error = it
            }
        )

        assertEquals("Error cargando item: 200", error)

    }

    /**
     * TEST 11: ERROR DEL SERVIDOR AL RECUPERAR UN ITEM
     *
     * Comprueba:
     * - error del servidor
     * -> llama a onError
     */
    @Test
    fun getItem_error_500_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<ShopItem>>()

        val repository = ItemRepository(apiService)

        whenever(apiService.getItem(1)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<ShopItem>>(0)
            callback.onResponse(call, Response.error(500, "Server error".toResponseBody(null)))
            null
        }.whenever(call).enqueue(any())

        var error: String? = null

        repository.getItem(1,
            onSuccess = {},
            onError = {
                error = it
            }
        )

        assertEquals("Error cargando item: 500", error)

    }

    /**
     * TEST 12: ERROR POR FALLO DE RED AL RECUPERAR UN ITEM
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError
     */
    @Test
    fun getItem_fallo_red_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<ShopItem>>()

        val repository = ItemRepository(apiService)

        whenever(apiService.getItem(1)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<ShopItem>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var error: String? = null

        repository.getItem(1,
            onSuccess = {},
            onError = {
                error = it
            }
        )

        assertEquals("Network error", error)

    }

    /**
     * TEST 13: ÉXITO AL COMPRAR UN ITEM
     *
     * Comprueba:
     * - respuesta correcta
     * -> llama a onSuccess
     */
    @Test
    fun buyItem_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = ItemRepository(apiService)

        whenever(apiService.buyItem(any<BuyItemRequest>())).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.success(Unit))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCalled = false

        repository.buyItem(
            itemId = 1,
            onSuccess = { successCalled = true },
            onError = { errorCalled = true }
        )

        assertTrue(successCalled)
        assertFalse(errorCalled)

    }

    /**
     * TEST 14: ERROR DEL SERVIDOR AL COMPRAR UN ITEM
     *
     * Comprueba:
     * - error del servidor
     * -> llama a onError con el código 400
     */
    @Test
    fun buyItem_error_400_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = ItemRepository(apiService)

        whenever(apiService.buyItem(any<BuyItemRequest>())).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.error(400, "Bad request".toResponseBody(null)))
            null
        }.whenever(call).enqueue(any())

        var error: String? = null

        repository.buyItem(
            itemId = 1,
            onSuccess = {},
            onError = { error = it }
        )

        assertEquals("Error comprando item: 400", error)

    }

    /**
     * TEST 15: ERROR POR FALLO DE RED AL COMPRAR UN ITEM
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError
     */
    @Test
    fun buyItem_fallo_red_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = ItemRepository(apiService)

        whenever(apiService.buyItem(any<BuyItemRequest>())).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var error: String? = null

        repository.buyItem(
            itemId = 1,
            onSuccess = {},
            onError = { error = it }
        )

        assertEquals("Network error", error)

    }

    /**
     * TEST 16: ÉXITO AL RECUPERAR LOS PRODUCTOS DETALLADOS DE LA TIENDA
     *
     * Comprueba:
     * - respuesta correcta
     * -> llama a onSuccess
     */
    @Test
    fun getShopProducts_exito_marca_owned_y_levelLocked() {

        val apiService = mock<ApiService>()
        val inventoryCall = mock<Call<List<ShopItem>>>()
        val shopCall = mock<Call<List<ShopItem>>>()

        val repository = ItemRepository(apiService)

        val inventory = listOf(
            sampleItem(id = 1, level = 0)
        )

        val shopItems = listOf(
            sampleItem(id = 1, price = 100, level = 0),
            sampleItem(id = 2, price = 200, level = 1)
        )

        whenever(apiService.getInventory()).thenReturn(inventoryCall)
        whenever(apiService.getAllShopItems()).thenReturn(shopCall)

        doAnswer {
            val callback = it.getArgument<Callback<List<ShopItem>>>(0)
            callback.onResponse(inventoryCall, Response.success(inventory))
            null
        }.whenever(inventoryCall).enqueue(any())

        doAnswer {
            val callback = it.getArgument<Callback<List<ShopItem>>>(0)
            callback.onResponse(shopCall, Response.success(shopItems))
            null
        }.whenever(shopCall).enqueue(any())

        var successCalled = false
        var errorCalled = false
        var products: List<ShopProduct> = emptyList()

        repository.getShopProducts(
            onSuccess = {
                successCalled = true
                products = it
            },
            onError = {
                errorCalled = true
            }
        )

        assertTrue(successCalled)
        assertFalse(errorCalled)
        assertEquals(2, products.size)

        assertEquals(1, products[0].itemId)
        assertTrue(products[0].isOwned)
        assertFalse(products[0].isLevelLocked)

        assertEquals(2, products[1].itemId)
        assertFalse(products[1].isOwned)

        // En los tests CurrentUserManager.getMyCurrentLevel() devuelve null, el repo usa 0
        // iitem con levelRequisite = 1 queda bloqueado
        assertTrue(products[1].isLevelLocked)

    }

    /**
     * TEST 17: ERROR AL RECUPERAR EL INVENTARIO
     *
     * Comprueba:
     * - error del servidor al recuperar el inventario
     * -> llama a onError con el código 500
     */
    @Test
    fun getShopProducts_error_inventory_500_onError() {

        val apiService = mock<ApiService>()
        val inventoryCall = mock<Call<List<ShopItem>>>()

        val repository = ItemRepository(apiService)

        whenever(apiService.getInventory()).thenReturn(inventoryCall)

        doAnswer {
            val callback = it.getArgument<Callback<List<ShopItem>>>(0)
            callback.onResponse(
                inventoryCall,
                Response.error(500, "Server error".toResponseBody(null))
            )
            null
        }.whenever(inventoryCall).enqueue(any())

        var successCalled = false
        var error: String? = null

        repository.getShopProducts(
            onSuccess = { successCalled = true },
            onError = { error = it }
        )

        assertFalse(successCalled)
        assertEquals("Error cargando inventario: 500", error)

    }

    /**
     * TEST 18: ERROR AL RECUPERAR LOS PRODUCTOS DE LA TIENDA
     *
     * Comprueba:
     * - error del servidor al recuperar los productos de la tienda
     * -> llama a onError con el código 500
     */
    @Test
    fun getShopProducts_error_shopItems_500_onError() {
        val apiService = mock<ApiService>()
        val inventoryCall = mock<Call<List<ShopItem>>>()
        val shopCall = mock<Call<List<ShopItem>>>()

        val repository = ItemRepository(apiService)

        val inventory = listOf(sampleItem(1))

        whenever(apiService.getInventory()).thenReturn(inventoryCall)
        whenever(apiService.getAllShopItems()).thenReturn(shopCall)

        doAnswer {
            val callback = it.getArgument<Callback<List<ShopItem>>>(0)
            callback.onResponse(inventoryCall, Response.success(inventory))
            null
        }.whenever(inventoryCall).enqueue(any())

        doAnswer {
            val callback = it.getArgument<Callback<List<ShopItem>>>(0)
            callback.onResponse(shopCall, Response.error(500, "Server error".toResponseBody(null)))
            null
        }.whenever(shopCall).enqueue(any())

        var successCalled = false
        var error: String? = null

        repository.getShopProducts(
            onSuccess = { successCalled = true },
            onError = { error = it }
        )

        assertFalse(successCalled)
        assertEquals("Error cargando tienda: 500", error)

    }

}
package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.notifications.RegisterDeviceRequest
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

/**
 * Tests unitarios para DeviceRepository.
 */
class DeviceRepositoryTest {

    /**
     * TEST 1: ÉXITO AL REGISTRAR UN DISPOSITIVO
     *
     * Comprueba:
     * - éxito al registrar un dispositivo
     * -> llama a onSuccess
     */
    @Test
    fun registerDevice_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = DeviceRepository(apiService)

        whenever(apiService.registerDevice(RegisterDeviceRequest("token123"))).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.success(Unit))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.registerDevice(
            token = "token123",
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertTrue(successCalled)
        assertNull(errorCode)

    }

    /**
     * TEST 2: ERROR 400 AL REGISTRAR UN DISPOSITIVO
     *
     * Comprueba:
     * - error 400 al registrar un dispositivo
     * -> llama a onError(400)
     */
    @Test
    fun registerDevice_error_400_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = DeviceRepository(apiService)

        whenever(apiService.registerDevice(RegisterDeviceRequest("token123"))).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.error(400, "Bad request".toResponseBody()))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.registerDevice(
            token = "token123",
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertEquals(400, errorCode)

    }

    /**
     * TEST 3: FALLO DE RED AL REGISTRAR UN DISPOSITIVO
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError(null)
     */
    @Test
    fun registerDevice_fallo_red_onError_null() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = DeviceRepository(apiService)

        whenever(apiService.registerDevice(RegisterDeviceRequest("token123"))).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.registerDevice(
            token = "token123",
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertNull(errorCode)
    }

    /**
     * TEST 4: ÉXITO AL ELIMINAR UN DISPOSITIVO
     *
     * Comprueba:
     * - éxito al eliminar un dispositivo
     * -> llama a onSuccess
     */
    @Test
    fun deleteDevice_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = DeviceRepository(apiService)

        whenever(apiService.deleteDevice(RegisterDeviceRequest("token123"))).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.success(Unit))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.deleteDevice(
            token = "token123",
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertTrue(successCalled)
        assertNull(errorCode)

    }

    /**
     * TEST 5: ERROR 500 AL ELIMINAR UN DISPOSITIVO
     *
     * Comprueba:
     * - error 500 al eliminar un dispositivo
     * -> llama a onError(500)
     */
    @Test
    fun deleteDevice_error_500_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = DeviceRepository(apiService)

        whenever(apiService.deleteDevice(RegisterDeviceRequest("token123"))).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.error(500, "Server error".toResponseBody()))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.deleteDevice(
            token = "token123",
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertEquals(500, errorCode)

    }

    /**
     * TEST 6: FALLO DE RED AL ELIMINAR UN DISPOSITIVO
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError(null)
     */
    @Test
    fun deleteDevice_fallo_red_onError_null() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = DeviceRepository(apiService)

        whenever(apiService.deleteDevice(RegisterDeviceRequest("token123"))).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.deleteDevice(
            token = "token123",
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertNull(errorCode)

    }

}
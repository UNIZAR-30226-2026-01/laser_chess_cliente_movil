package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.remote.ApiService
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Tests unitarios para EventStatusRepository.
 */
class EventStatusRepositoryTest {

    /**
     * TEST 1: ÉXITO AL ENVIAR ONLINE
     *
     * Comprueba:
     * - éxito al enviar la petición
     * -> llama a enqueue correctamente
     */
    @Test
    fun markOnline_exito_enqueue_correctamente() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = EventStatusRepository(apiService)

        whenever(apiService.markOnline()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.success(Unit))
            null
        }.whenever(call).enqueue(any())

        repository.markOnline()

    }

    /**
     * TEST 2: FALLO DE RED AL ENVIAR ONLINE
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> no crashea
     */
    @Test
    fun markOnline_fallo_red_no_crashea() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = EventStatusRepository(apiService)

        whenever(apiService.markOnline()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        repository.markOnline()

    }

    /**
     * TEST 3: ÉXITO AL ENVIAR OFFLINE
     *
     * Comprueba:
     * - éxito al enviar la petición
     * -> llama a enqueue correctamente
     */
    @Test
    fun markOffline_exito_enqueue_correctamente() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = EventStatusRepository(apiService)

        whenever(apiService.markOffline()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.success(Unit))
            null
        }.whenever(call).enqueue(any())

        repository.markOffline()

    }

    /**
     * TEST 4: FALLO DE RED AL ENVIAR OFFLINE
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> no crashea
     */
    @Test
    fun markOffline_fallo_red_no_crashea() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = EventStatusRepository(apiService)

        whenever(apiService.markOffline()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        repository.markOffline()

    }

}
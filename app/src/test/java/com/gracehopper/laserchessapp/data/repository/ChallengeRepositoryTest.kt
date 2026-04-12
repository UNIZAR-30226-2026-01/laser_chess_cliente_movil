package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.game.PendingChallengeResponse
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
 * Tests unitarios para ChallengeRepository.
 */
class ChallengeRepositoryTest {

    /**
     * TEST 1: ÉXITO
     *
     * Comprueba:
     * - respuesta correcta
     * -> llama a onSuccess con la lista de solicitudes de partidas amistosas
     */
    @Test
    fun getPendingChallenges_exito_onSuccess() {

        // creo mocks
        val apiService = mock<ApiService>()
        val call = mock<Call<List<PendingChallengeResponse>>>()

        // creo repo con el apiService falso
        val repository = ChallengeRepository(apiService)

        // no hay request
        val response = listOf<PendingChallengeResponse>()

        // cuando se llame a apiService.getPendingChallenges -> devolver call falso
        whenever(apiService.getPendingChallenges()).thenReturn(call)

        // simulo que la API responde correctamente
        doAnswer {
            val callback = it.getArgument<Callback<List<PendingChallengeResponse>>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedList: List<PendingChallengeResponse>? = null
        var errorCalled = false

        // ejecuto getPendingChallenges
        repository.getPendingChallenges(
            onSuccess = {
                successCalled = true
                receivedList = it
            },
            onError = {
                errorCalled = true
            }
        )

        // verifico resultados
        assertTrue(successCalled)
        assertFalse(errorCalled)
        assertEquals(response, receivedList)

    }

    /**
     * TEST 2: BODY NULO
     *
     * Comprueba:
     * - éxito pero body nulo
     * -> llama a onSuccess con lista vacía
     */
    @Test
    fun getPendingChallenges_body_null_onSuccess_emptyList() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<PendingChallengeResponse>>>()

        val repository = ChallengeRepository(apiService)

        whenever(apiService.getPendingChallenges()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<PendingChallengeResponse>>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedList: List<PendingChallengeResponse>? = null
        var errorCalled = false

        repository.getPendingChallenges(
            onSuccess = {
                successCalled = true
                receivedList = it
            },
            onError = {
                errorCalled = true
            }
        )

        assertTrue(successCalled)
        assertFalse(errorCalled)
        assertEquals(emptyList<PendingChallengeResponse>(), receivedList)

    }

    /**
     * TEST 3: ERROR 500
     *
     * Comprueba:
     * - error 500
     * -> llama a onError(500)
     */
    @Test
    fun getPendingChallenges_error_500_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<PendingChallengeResponse>>>()

        val repository = ChallengeRepository(apiService)

        whenever(apiService.getPendingChallenges()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<PendingChallengeResponse>>>(0)
            callback.onResponse(call,
                Response.error(500, "Server error".toResponseBody(null))
            )
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.getPendingChallenges(
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCode = it
            }
        )

        assertFalse(successCalled)
        assertEquals(500, errorCode)

    }

    /**
     * TEST 4: FALLO DE RED
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError(null)
     */
    @Test
    fun getPendingChallenges_fallo_red_onError_null() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<PendingChallengeResponse>>>()

        val repository = ChallengeRepository(apiService)

        whenever(apiService.getPendingChallenges()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<PendingChallengeResponse>>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.getPendingChallenges(
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCode = it
            }
        )

        assertFalse(successCalled)
        assertNull(errorCode)

    }

}
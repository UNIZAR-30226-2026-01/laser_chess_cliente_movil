package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.game.ChallengeCountResponse
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

    /**
     * TEST 5: ÉXITO AL RECUPERAR EL NÚMERO DE CHALLENGES
     *
     * Comprueba:
     * - cuenta correcta
     * -> llama a onSuccess con la cuenta
     */
    @Test
    fun getChallengeCount_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<ChallengeCountResponse>>()

        val repository = ChallengeRepository(apiService)

        val response = ChallengeCountResponse(count = 5)

        whenever(apiService.getChallengeCount()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<ChallengeCountResponse>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.whenever(call).enqueue(any())

        var successValue: Int? = null
        var errorCode: Int? = null

        repository.getChallengeCount(
            onSuccess = { successValue = it },
            onError = { errorCode = it }
        )

        assertEquals(5, successValue)
        assertNull(errorCode)

    }

    /**
     * TEST 6: ÉXITO AL RECUPERAR EL NÚMERO DE CHALLENGES (BODY NULO)
     *
     * Comprueba:
     * - respuesta con body nulo
     * -> llama a onSuccess con 0
     */
    @Test
    fun getChallengeCount_exito_body_nulo_onSuccess_0() {

        val apiService = mock<ApiService>()
        val call = mock<Call<ChallengeCountResponse>>()

        val repository = ChallengeRepository(apiService)

        whenever(apiService.getChallengeCount()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<ChallengeCountResponse>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.whenever(call).enqueue(any())

        var successValue: Int? = null
        var errorCode: Int? = null

        repository.getChallengeCount(
            onSuccess = { successValue = it },
            onError = { errorCode = it }
        )

        assertEquals(0, successValue)
        assertNull(errorCode)

    }

    /**
     * TEST 7: ERROR 500 AL RECUPERAR EL NÚMERO DE CHALLENGES
     *
     * Comprueba:
     * - error 500
     * -> llama a onError(500)
     */
    @Test
    fun getChallengeCount_error_500_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<ChallengeCountResponse>>()

        val repository = ChallengeRepository(apiService)

        whenever(apiService.getChallengeCount()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<ChallengeCountResponse>>(0)
            callback.onResponse(call, Response.error(500, "Server error".toResponseBody()))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.getChallengeCount(
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertEquals(500, errorCode)

    }

    /**
     * TEST 8: FALLO DE RED AL RECUPERAR EL NÚMERO DE CHALLENGES
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError(null)
     */
    @Test
    fun getChallengeCount_fallo_red_onError_null() {

        val apiService = mock<ApiService>()
        val call = mock<Call<ChallengeCountResponse>>()

        val repository = ChallengeRepository(apiService)

        whenever(apiService.getChallengeCount()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<ChallengeCountResponse>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.getChallengeCount(
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertNull(errorCode)

    }

}
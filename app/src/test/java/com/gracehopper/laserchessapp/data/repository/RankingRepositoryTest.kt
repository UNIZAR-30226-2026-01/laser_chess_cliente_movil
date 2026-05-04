package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.ranking.RankingEntry
import com.gracehopper.laserchessapp.data.model.ranking.RankingEntryResponse
import com.gracehopper.laserchessapp.data.model.user.TimeMode
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

class RankingRepositoryTest {

    /**
     * TEST 1: ÉXITO
     *
     * Comprueba:
     * - respuesta correcta
     * -> llama a onSuccess
     */
    @Test
    fun getRankById_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Long>>()

        val repository = RankingRepository(apiService)

        whenever(apiService.getRankById("blitz", 1L)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Long>>(0)
            callback.onResponse(call, Response.success(5L))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedRank: Long? = null
        var errorCalled = false

        repository.getRankById(
            eloType = TimeMode.BLITZ,
            userId = 1L,
            onSuccess = {
                successCalled = true
                receivedRank = it
            },
            onError = {
                errorCalled = true
            }
        )

        assertTrue(successCalled)
        assertFalse(errorCalled)
        assertEquals(5L, receivedRank)
    }

    /**
     * TEST 2: ERROR AL RECUPERAR EL RANKING
     *
     * Comprueba:
     * - respuesta con body nulo
     * -> llama a onError
     */
    @Test
    fun getRankById_body_null_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Long>>()

        val repository = RankingRepository(apiService)

        whenever(apiService.getRankById("blitz", 1L)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Long>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCalled = false

        repository.getRankById(
            eloType = TimeMode.BLITZ,
            userId = 1L,
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCalled = true
            }
        )

        assertFalse(successCalled)
        assertTrue(errorCalled)

    }

    /**
     * TEST 3: ERROR DEL SERVIDOR AL RECUPERAR EL RANKING
     *
     * Comprueba:
     * - error del servidor
     * -> llama a onError
     */
    @Test
    fun getRankById_error_500_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Long>>()

        val repository = RankingRepository(apiService)

        whenever(apiService.getRankById("blitz", 1L)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Long>>(0)
            callback.onResponse(call, Response.error(500, "Server error".toResponseBody(null)))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCalled = false

        repository.getRankById(
            eloType = TimeMode.BLITZ,
            userId = 1L,
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCalled = true
            }
        )

        assertFalse(successCalled)
        assertTrue(errorCalled)

    }

    /**
     * TEST 4: FALLO RED AL RECUPERAR EL RANKING
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError
     */
    @Test
    fun getRankById_fallo_red_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Long>>()

        val repository = RankingRepository(apiService)

        whenever(apiService.getRankById("blitz", 1L)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Long>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCalled = false

        repository.getRankById(
            eloType = TimeMode.BLITZ,
            userId = 1L,
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCalled = true
            }
        )

        assertFalse(successCalled)
        assertTrue(errorCalled)

    }

    /**
     * TEST 5: ÉXITO AL RECUPERAR EL TOP RANKING
     *
     * Comprueba:
     * - respuesta correcta
     * -> llama a onSuccess
     */
    @Test
    fun getTopRankUsers_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<RankingEntryResponse>>>()

        val repository = RankingRepository(apiService)

        val response = listOf(
            RankingEntryResponse(
                id = 1L,
                username = "user1",
                avatar = 1,
                rating = 1500
            ),
            RankingEntryResponse(
                id = 2L,
                username = "user2",
                avatar = 2,
                rating = 1400
            )
        )

        whenever(apiService.getTopRankUsers("blitz")).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<RankingEntryResponse>>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedRanking = emptyList<RankingEntry>()
        var errorCode: Int? = 999

        repository.getTopRankUsers(
            eloType = TimeMode.BLITZ,
            onSuccess = {
                successCalled = true
                receivedRanking = it
            },
            onError = {
                errorCode = it
            }
        )

        assertTrue(successCalled)
        assertEquals(999, errorCode)
        assertEquals(2, receivedRanking.size)

        assertEquals(1L, receivedRanking[0].id)
        assertEquals("user1", receivedRanking[0].username)
        assertEquals(1500, receivedRanking[0].elo)
        assertEquals(1, receivedRanking[0].position)

        assertEquals(2L, receivedRanking[1].id)
        assertEquals("user2", receivedRanking[1].username)
        assertEquals(1400, receivedRanking[1].elo)
        assertEquals(2, receivedRanking[1].position)

    }

    /**
     * TEST 6: ERROR BODY NULL AL RECUPERAR EL TOP RANKING
     *
     * Comprueba:
     * - respuesta con body nulo
     * -> llama a onError con el código 200
     */
    @Test
    fun getTopRankUsers_body_null_onError_200() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<RankingEntryResponse>>>()

        val repository = RankingRepository(apiService)

        whenever(apiService.getTopRankUsers("blitz")).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<RankingEntryResponse>>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.getTopRankUsers(
            eloType = TimeMode.BLITZ,
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCode = it
            }
        )

        assertFalse(successCalled)
        assertEquals(200, errorCode)

    }

    /**
     * TEST 7: ERROR DEL SERVIDOR AL RECUPERAR EL TOP RANKING
     *
     * Comprueba:
     * - error del servidor
     * -> llama a onError con el código 500
     */
    @Test
    fun getTopRankUsers_error_500_onError_500() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<RankingEntryResponse>>>()

        val repository = RankingRepository(apiService)

        whenever(apiService.getTopRankUsers("blitz")).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<RankingEntryResponse>>>(0)
            callback.onResponse(call, Response.error(500, "Server error".toResponseBody(null)))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.getTopRankUsers(
            eloType = TimeMode.BLITZ,
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

    @Test
    fun getTopRankUsers_fallo_red_onError_null() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<RankingEntryResponse>>>()

        val repository = RankingRepository(apiService)

        whenever(apiService.getTopRankUsers("blitz")).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<RankingEntryResponse>>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.getTopRankUsers(
            eloType = TimeMode.BLITZ,
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
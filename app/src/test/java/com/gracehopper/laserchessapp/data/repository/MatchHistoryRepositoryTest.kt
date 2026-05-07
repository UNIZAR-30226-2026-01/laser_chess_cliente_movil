package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.game.BoardType
import com.gracehopper.laserchessapp.data.model.game.InProgressGameSummary
import com.gracehopper.laserchessapp.data.model.game.PausedMatchResponse
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

class MatchHistoryRepositoryTest {

    /**
     * TEST 1: GET PAUSED MATCHES CON ÉXITO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * - las partidas se mapean correctamente
     * -> llama a onSuccess con la lista transformada
     */
    @Test
    fun getPausedMatches_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<PausedMatchResponse>>>()

        val repository = MatchHistoryRepository(apiService)

        val response = listOf(
            PausedMatchResponse(
                matchId = 1,
                p1Id = 10,
                p2Id = 20,
                p1Username = "player1",
                p2Username = "player2",
                p2Elo = 1200,
                matchType = "RANKED",
                board = "ACE",
                timeBase = 300000,
                timeIncrement = 2
            )
        )

        whenever(apiService.getPausedMatches(10)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<PausedMatchResponse>>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCalled = false
        var receivedList: List<InProgressGameSummary>? = null

        repository.getPausedMatches(
            userId = 10,
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

        assertNotNull(receivedList)
        assertEquals(1, receivedList!!.size)

        val match = receivedList[0]

        assertEquals("1", match.id)
        assertEquals("player2", match.opponentUsername)
    }

    /**
     * TEST 2: GET PAUSED MATCHES CON BODY NULO
     *
     * Comprueba:
     * - la API devuelve éxito
     * - body nulo
     * -> llama a onSuccess con lista vacía
     */
    @Test
    fun getPausedMatches_bodyNull_onSuccess_emptyList() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<PausedMatchResponse>>>()

        val repository = MatchHistoryRepository(apiService)

        whenever(apiService.getPausedMatches(10)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<PausedMatchResponse>>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCalled = false
        var receivedList: List<InProgressGameSummary>? = null

        repository.getPausedMatches(
            userId = 10,
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
        assertEquals(emptyList<InProgressGameSummary>(), receivedList)
    }

    /**
     * TEST 3: GET PAUSED MATCHES ERROR HTTP
     *
     * Comprueba:
     * - la API devuelve error HTTP
     * -> llama a onError con el código
     */
    @Test
    fun getPausedMatches_httpError_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<PausedMatchResponse>>>()

        val repository = MatchHistoryRepository(apiService)

        whenever(apiService.getPausedMatches(10)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<PausedMatchResponse>>>(0)
            callback.onResponse(
                call,
                Response.error(404, "error".toResponseBody())
            )
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCalled = false
        var receivedError: Int? = null

        repository.getPausedMatches(
            userId = 10,
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCalled = true
                receivedError = it
            }
        )

        assertFalse(successCalled)
        assertTrue(errorCalled)
        assertEquals(404, receivedError)
    }

    /**
     * TEST 4: GET PAUSED MATCHES FAILURE
     *
     * Comprueba:
     * - ocurre fallo de red
     * -> llama a onError con null
     */
    @Test
    fun getPausedMatches_failure_onError_null() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<PausedMatchResponse>>>()

        val repository = MatchHistoryRepository(apiService)

        whenever(apiService.getPausedMatches(10)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<PausedMatchResponse>>>(0)
            callback.onFailure(call, Throwable())
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCalled = false
        var receivedError: Int? = -1

        repository.getPausedMatches(
            userId = 10,
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCalled = true
                receivedError = it
            }
        )

        assertFalse(successCalled)
        assertTrue(errorCalled)
        assertNull(receivedError)
    }

    /**
     * TEST 5: PARSE BOARD TYPE INVÁLIDO
     *
     * Comprueba:
     * - board inválido
     * -> usa BoardType. ACE por defecto
     */
    @Test
    fun getPausedMatches_invalidBoard_usesAceDefault() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<PausedMatchResponse>>>()

        val repository = MatchHistoryRepository(apiService)

        val response = listOf(
            PausedMatchResponse(
                matchId = 1,
                p1Id = 10,
                p2Id = 20,
                p1Username = "player1",
                p2Username = "player2",
                p2Elo = 1200,
                matchType = "RANKED",
                board = "INVALID_BOARD",
                timeBase = 300000,
                timeIncrement = 2
            )
        )

        whenever(apiService.getPausedMatches(10)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<PausedMatchResponse>>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.whenever(call).enqueue(any())

        var receivedList: List<InProgressGameSummary>? = null

        repository.getPausedMatches(
            userId = 10,
            onSuccess = {
                receivedList = it
            },
            onError = {}
        )

        assertEquals(BoardType.ACE, receivedList!![0].boardType)
    }

    /**
     * TEST 6: PARSE TIME MODE EXTENDED
     *
     * Comprueba:
     * - una partida de más de 30 minutos
     * -> se mapea a EXTENDED
     */
    @Test
    fun getPausedMatches_extendedTimeMode_ok() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<PausedMatchResponse>>>()

        val repository = MatchHistoryRepository(apiService)

        val response = listOf(
            PausedMatchResponse(
                matchId = 1,
                p1Id = 10,
                p2Id = 20,
                p1Username = "player1",
                p2Username = "player2",
                p2Elo = 1200,
                matchType = "RANKED",
                board = "ACE",
                timeBase = 3600000,
                timeIncrement = 20
            )
        )

        whenever(apiService.getPausedMatches(10)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<PausedMatchResponse>>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.whenever(call).enqueue(any())

        var receivedList: List<InProgressGameSummary>? = null

        repository.getPausedMatches(
            userId = 10,
            onSuccess = {
                receivedList = it
            },
            onError = {}
        )

        assertEquals(TimeMode.EXTENDED, receivedList!![0].timeMode)
        assertEquals("60:00", receivedList[0].myTime)
    }
}
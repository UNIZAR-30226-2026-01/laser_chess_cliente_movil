package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.social.CreateFriendshipRequest
import com.gracehopper.laserchessapp.data.model.social.FriendSummary
import com.gracehopper.laserchessapp.data.model.social.FriendshipStatusResponse
import com.gracehopper.laserchessapp.data.model.social.ReceivedRequestsResponse
import com.gracehopper.laserchessapp.data.model.user.UserFriendshipStatus
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
 * Test unitarios para FriendRepository
 */
class FriendRepositoryTest {

    /**
     * TEST 1: GET FRIENDS CON ÉXITO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * - body NO es null (válido, con lista válida)
     * -> llama a onSuccess correctamente con la lista
     */
    @Test
    fun getFriends_exito_onSuccess() {

        // creo mocks
        val apiService = mock<ApiService>()
        val call = mock<Call<List<FriendSummary>>>()

        // creo repo con el apiService falso
        val repository = FriendRepository(apiService)

        // no hay request
        val response = emptyList<FriendSummary>()

        // cuando se llame a apiService.getFriends -> devolver call falso
        whenever(apiService.getFriendships()).thenReturn(call)

        // simulo que la API responde correctamente
        doAnswer {
            val callback = it.getArgument<Callback<List<FriendSummary>>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedList: List<FriendSummary>? = null
        var errorCalled = false

        // ejecuto getFriends
        repository.getFriends(
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
     * TEST 2: GET FRIENDS CON BODY NULO
     *
     * Comprueba:
     * - la API devuelve éxito
     * - body nulo
     * -> llama a onSuccess con lista vacía
     */
    @Test
    fun getFriends_body_null_onSuccess_emptyList() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<FriendSummary>>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.getFriendships()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<FriendSummary>>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedList: List<FriendSummary>? = null
        var errorCalled = false

        repository.getFriends(
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
        assertEquals(emptyList<FriendSummary>(), receivedList)

    }

    /**
     * TEST 3: GET FRIENDS CON ERROR 500 (error del servidor)
     *
     * Comprueba:
     * - la API devuelve error 500
     * -> llama a onError(500)
     */
    @Test
    fun getFriends_error_500_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<FriendSummary>>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.getFriendships()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<FriendSummary>>>(0)
            callback.onResponse(
                call,
                Response.error(
                    500,
                    "Server error".toResponseBody(null)
                )
            )
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.getFriends(
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
     * TEST 4: GET FRIENDS CON FALLO DE RED
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError(null)
     */
    @Test
    fun getFriends_fallo_red_onError_null() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<FriendSummary>>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.getFriendships()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<FriendSummary>>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.getFriends(
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
     * TEST 5: ADD FRIEND CON ÉXITO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * -> llama a onSuccess correctamente
     */
    @Test
    fun addFriend_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.addFriend(any<CreateFriendshipRequest>()))
            .thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.success(Unit))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCalled = false

        repository.addFriend(
            "username",
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCalled = true
            }
        )

        assertTrue(successCalled)
        assertFalse(errorCalled)

    }

    /**
     * TEST 6: ADD FRIEND CON ERROR 409 (ya existe una amistad/solicitud)
     *
     * Comprueba:
     * - la API devuelve error 409
     * -> llama a onError(409)
     */
    @Test
    fun addFriend_error_409_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.addFriend(any<CreateFriendshipRequest>())).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.error(409, "Conflict".toResponseBody(null)))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.addFriend(
            "username",
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCode = it
            }
        )

        assertFalse(successCalled)
        assertEquals(409, errorCode)

    }

    /**
     * TEST 7: ADD FRIEND CON FALLO DE RED
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError(null)
     */
    @Test
    fun addFriend_fallo_red_onError_null() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.addFriend(any<CreateFriendshipRequest>())).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.addFriend(
            "username",
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
     * TEST 8: OBTENER NÚMERO DE SOLICITUDES DE AMISTAD RECIBIDAS CON ÉXITO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * - body NO es null (válido, con número válido)
     * -> llama a onSuccess correctamente con el número
     */
    @Test
    fun getNumReceivedFriendshipRequests_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<ReceivedRequestsResponse>>()

        val repository = FriendRepository(apiService)

        val response = ReceivedRequestsResponse(3)

        whenever(apiService.getNumReceivedFriendshipRequests()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<ReceivedRequestsResponse>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedCount = -1
        var errorCalled = false

        repository.getNumReceivedFriendshipRequests(
            onSuccess = {
                successCalled = true
                receivedCount = it
            },
            onError = {
                errorCalled = true
            }
        )

        assertTrue(successCalled)
        assertFalse(errorCalled)
        assertEquals(3, receivedCount)

    }

    /**
     * TEST 9: OBTENER NÚMERO DE SOLICITUDES DE AMISTAD RECIBIDAS CON BODY NULO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * - body es null
     * -> llama a onSuccess correctamente
     */
    fun getNumReceivedFriendshipRequests_body_null_onSuccess_0() {

        val apiService = mock<ApiService>()
        val call = mock<Call<ReceivedRequestsResponse>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.getNumReceivedFriendshipRequests()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<ReceivedRequestsResponse>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedCount = -1
        var errorCalled = false

        repository.getNumReceivedFriendshipRequests(
            onSuccess = {
                successCalled = true
                receivedCount = it
            },
            onError = {
                errorCalled = true
            }
        )

        assertTrue(successCalled)
        assertFalse(errorCalled)
        assertEquals(0, receivedCount)

    }

    @Test
    fun getNumReceivedFriendshipRequests_fallo_red_onError_null() {

        val apiService = mock<ApiService>()
        val call = mock<Call<ReceivedRequestsResponse>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.getNumReceivedFriendshipRequests()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<ReceivedRequestsResponse>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.getNumReceivedFriendshipRequests(
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
     * TEST 10: OBTENER SOLICITUDES DE AMISTAD RECIBIDAS CON ÉXITO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * -> llama a onSuccess correctamente con la lista
     */
    @Test
    fun getReceivedFriendshipRequests_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<FriendSummary>>>()

        val repository = FriendRepository(apiService)

        val response = emptyList<FriendSummary>()

        whenever(apiService.getReceivedFriendshipRequests()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<FriendSummary>>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedList: List<FriendSummary>? = null
        var errorCalled = false

        repository.getReceivedFriendshipRequests(
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
        assertEquals(response, receivedList)

    }

    /**
     * TEST 11: OBTENER SOLICITUDES DE AMISTAD RECIBIDAS CON BODY NULO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * - body es null
     * -> llama a onSuccess correctamente
     */
    @Test
    fun getReceivedFriendshipRequests_body_null_onSuccess_emptyList() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<FriendSummary>>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.getReceivedFriendshipRequests()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<FriendSummary>>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedList: List<FriendSummary>? = null
        var errorCalled = false

        repository.getReceivedFriendshipRequests(
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
        assertEquals(emptyList<FriendSummary>(), receivedList)

    }

    /**
     * TEST 12: OBTENER SOLICITUDES DE AMISTAD ENVIADAS CON ÉXITO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * -> llama a onSuccess correctamente con la lista
     */
    @Test
    fun getSentFriendshipRequests_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<FriendSummary>>>()

        val repository = FriendRepository(apiService)

        val response = emptyList<FriendSummary>()

        whenever(apiService.getSentFriendshipRequests()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<FriendSummary>>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedList: List<FriendSummary>? = null
        var errorCalled = false

        repository.getSentFriendshipRequests(
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
        assertEquals(response, receivedList)

    }

    /**
     * TEST 13: OBTENER SOLICITUDES DE AMISTAD ENVIADAS CON BODY NULO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * - body es null
     * -> llama a onSuccess correctamente
     */
    @Test
    fun getSentFriendshipRequests_body_null_onSuccess_emptyList() {

        val apiService = mock<ApiService>()
        val call = mock<Call<List<FriendSummary>>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.getSentFriendshipRequests()).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<List<FriendSummary>>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedList: List<FriendSummary>? = null
        var errorCalled = false

        repository.getSentFriendshipRequests(
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
        assertEquals(emptyList<FriendSummary>(), receivedList)

    }

    /**
     * TEST 14: OBTENER ESTADO DE LA SOLICITUD DE AMISTAD CON ÉXITO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * -> llama a onSuccess correctamente con el estado
     */
    @Test
    fun getFriendshipStatus_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<FriendshipStatusResponse>>()

        val repository = FriendRepository(apiService)

        val response = FriendshipStatusResponse(1, 2,
            senderAccept = true, receiverAccept = true)

        whenever(apiService.getFriendshipStatus("username")).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<FriendshipStatusResponse>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedStatus: UserFriendshipStatus? = null
        var errorCalled = false

        repository.getFriendshipStatus(
            myId = 1L,
            username = "username",
            onSuccess = {
                successCalled = true
                receivedStatus = it
            },
            onError = {
                errorCalled = true
            }
        )

        assertTrue(successCalled)
        assertFalse(errorCalled)
        assertEquals(UserFriendshipStatus.FRIEND, receivedStatus)

    }

    /**
     * TEST 15: OBTENER ESTADO DE LA SOLICITUD DE AMISTAD CON BODY NULO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * - body es null
     * -> llama a onError(null)
     */
    @Test
    fun getFriendshipStatus_body_null_onError_null() {

        val apiService = mock<ApiService>()
        val call = mock<Call<FriendshipStatusResponse>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.getFriendshipStatus("username")).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<FriendshipStatusResponse>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.getFriendshipStatus(
            myId = 1L,
            username = "username",
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
     * TEST 16: OBTENER ESTADO DE LA SOLICITUD DE AMISTAD CON ERROR 404
     *
     * Comprueba:
     * - la API devuelve error 404
     * -> llama a onError(404)
     */
    @Test
    fun getFriendshipStatus_error_404_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<FriendshipStatusResponse>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.getFriendshipStatus("username")).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<FriendshipStatusResponse>>(0)
            callback.onResponse(call, Response.error(404, "No encontrado".toResponseBody(null)))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var receivedStatus: UserFriendshipStatus? = null
        var errorCalled = false

        repository.getFriendshipStatus(
            myId = 1L,
            username = "username",
            onSuccess = {
                successCalled = true
                receivedStatus = it
            },
            onError = {
                errorCalled = true
            }
        )

        assertTrue(successCalled)
        assertEquals(UserFriendshipStatus.NON_FRIEND, receivedStatus)
        assertFalse(errorCalled)

    }

    /**
     * TEST 17: OBTENER ESTADO DE LA SOLICITUD DE AMISTAD CON FALLO DE RED
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError(null)
     */
    @Test
    fun getFriendshipStatus_fallo_red_onError_null() {

        val apiService = mock<ApiService>()
        val call = mock<Call<FriendshipStatusResponse>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.getFriendshipStatus("username")).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<FriendshipStatusResponse>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.getFriendshipStatus(
            myId = 1L,
            username = "username",
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
     * TEST 18: ACEPTAR SOLICITUD DE AMISTAD CON ÉXITO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * -> llama a onSuccess correctamente
     */
    @Test
    fun acceptFriendship_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.acceptFriendship("username")).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.success(Unit))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCalled = false

        repository.acceptFriendship(
            "username",
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCalled = true
            }
        )

        assertTrue(successCalled)
        assertFalse(errorCalled)

    }

    /**
     * TEST 19: ACEPTAR SOLICITUD DE AMISTAD CON ERROR 404
     *
     * Comprueba:
     * - la API devuelve error 404
     * -> llama a onError(404)
     */
    @Test
    fun acceptFriendship_error_404_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.acceptFriendship("username")).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.error(404, "No encontrado".toResponseBody(null)))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.acceptFriendship(
            "username",
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCode = it
            }
        )

        assertFalse(successCalled)
        assertEquals(404, errorCode)

    }

    /**
     * TEST 20: ELIMINAR SOLICITUD DE AMISTAD CON ÉXITO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * -> llama a onSuccess correctamente
     */
    @Test
    fun deleteFriendship_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.deleteFriendship("username")).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.success(Unit))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCalled = false

        repository.deleteFriendship(
            "username",
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCalled = true
            }
        )

        assertTrue(successCalled)
        assertFalse(errorCalled)

    }

    @Test
    fun deleteFriendship_error_404_onError() {

        val apiService = mock<ApiService>()
        val call = mock<Call<Unit>>()

        val repository = FriendRepository(apiService)

        whenever(apiService.deleteFriendship("username")).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(call, Response.error(404, "No encontrado".toResponseBody(null)))
            null
        }.whenever(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.deleteFriendship(
            "username",
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCode = it
            }
        )

        assertFalse(successCalled)
        assertEquals(404, errorCode)

    }

}
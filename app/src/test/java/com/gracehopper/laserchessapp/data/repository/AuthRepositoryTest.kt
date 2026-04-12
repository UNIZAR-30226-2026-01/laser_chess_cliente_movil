package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.auth.LoginRequest
import com.gracehopper.laserchessapp.data.model.auth.LoginResponse
import com.gracehopper.laserchessapp.data.model.auth.RegisterRequest
import com.gracehopper.laserchessapp.data.model.auth.RegisterResponse
import com.gracehopper.laserchessapp.data.remote.ApiService
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.junit.Assert.*
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Test unitarios para AuthRepository
 */
class AuthRepositoryTest {

    /**
     * TEST 1: LOGIN CON ÉXITO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * - body NO es null (válido)
     * -> llama a onSuccess correctamente
     */
    @Test
    fun login_exito_onSuccess() {

        // creo mocks
        val apiService = mock(ApiService::class.java)
        val call = mock(Call::class.java) as Call<LoginResponse>

        // creo repo con el apiService falso
        val repository = AuthRepository(apiService)

        val request = LoginRequest("username", "password")
        val response = LoginResponse("token")

        // cuando se llame a apiService.login -> devolver call falso
        `when`(apiService.login(request)).thenReturn(call)

        // simulo qu ela API responde correctamente
        doAnswer {
            val callback = it.getArgument<Callback<LoginResponse>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.`when`(call).enqueue(any())

        var successCalled = false
        var errorCalled = false

        // ejecuto login
        repository.login(
            request,
            onSuccess = {
                successCalled = true
                assertEquals("token", it.accessToken)
            }, onError = {
                errorCalled = true
            }
        )

        // verifico resultados
        assertTrue(successCalled)
        assertFalse(errorCalled)

    }

    /**
     * TEST 2: LOGIN CON BODY NULO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * - body es null (inválido)
     * -> llama a onError(null)
     */
    @Test
    fun login_error_body_null_onError_null() {

        val apiService = mock(ApiService::class.java)
        val call = mock(Call::class.java) as Call<LoginResponse>
        val repository = AuthRepository(apiService)

        val request = LoginRequest("username", "password")

        `when`(apiService.login(request)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<LoginResponse>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.`when`(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.login(
            request,
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertNull(errorCode)

    }

    /**
     * TEST 3: LOGIN CON ERROR 500 (error del servidor)
     *
     * Comprueba:
     * - la API devuelve error 500
     * -> llama a onError 500
     */
    @Test
    fun login_error_500_onError() {

        val apiService = mock(ApiService::class.java)
        val call = mock(Call::class.java) as Call<LoginResponse>
        val repository = AuthRepository(apiService)

        val request = LoginRequest("username", "password")

        `when`(apiService.login(request)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<LoginResponse>>(0)
            callback.onResponse(
                call,
                Response.error(500,
                    "Server error".toResponseBody(null))
            )
            null
        }.`when`(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.login(
            request,
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertEquals(500, errorCode)

    }

    /**
     * TEST 4: LOGIN CON ERROR 401 (credenciales incorrectas)
     *
     * Comprueba:
     * - la API devuelve error 401
     * -> llama a onError 401
     */
    @Test
    fun login_error_401_onError() {

        val apiService = mock(ApiService::class.java)
        val call = mock(Call::class.java) as Call<LoginResponse>
        val repository = AuthRepository(apiService)

        val request = LoginRequest("username", "password")

        `when`(apiService.login(request)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<LoginResponse>>(0)
            callback.onResponse(
                call,
                Response.error(401,
                    "Unauthorized".toResponseBody(null))
            )
            null
        }.`when`(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.login(
            request,
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertEquals(401, errorCode)

    }

    /**
     * TEST 5: LOGIN CON ERROR 400 (datos inválidos)
     *
     * Comprueba:
     * - la API devuelve error 400
     * -> llama a onError 400
     */
    @Test
    fun login_error_400_onError() {

        val apiService = mock(ApiService::class.java)
        val call = mock(Call::class.java) as Call<LoginResponse>
        val repository = AuthRepository(apiService)

        val request = LoginRequest("username", "password")

        `when`(apiService.login(request)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<LoginResponse>>(0)
            callback.onResponse(
                call,
                Response.error(400,
                    "Invalid data".toResponseBody(null))
            )
            null
        }.`when`(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.login(
            request,
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertEquals(400, errorCode)

    }

    /**
     * TEST 6: LOGIN CON FALLO POR RED
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError(null)
     */
    @Test
    fun login_fallo_red_onError_null() {

        val apiService = mock(ApiService::class.java)
        val call = mock(Call::class.java) as Call<LoginResponse>
        val repository = AuthRepository(apiService)

        val request = LoginRequest("username", "password")

        `when`(apiService.login(request)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<LoginResponse>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.`when`(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.login(
            request,
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertNull(errorCode)

    }

    /**
     * TEST 7: REGISTER CON ÉXITO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * -> llama a onSuccess correctamente
     */
    @Test
    fun register_exito_onSuccess() {

        val apiService = mock(ApiService::class.java)
        val call = mock(Call::class.java) as Call<RegisterResponse>
        val repository = AuthRepository(apiService)

        val request = RegisterRequest("username", "mail@test.tst", "password")
        val response = RegisterResponse(1L)

        `when`(apiService.register(request)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<RegisterResponse>>(0)
            callback.onResponse(call, Response.success(response))
            null
        }.`when`(call).enqueue(any())

        var successCalled = false
        var errorCalled = false

        repository.register(
            request,
            onSuccess = {
                successCalled = true
                assertEquals(1L, it.accountId)
            },
            onError = {
                errorCalled = true
            }
        )

        assertTrue(successCalled)
        assertFalse(errorCalled)

    }

    /**
     * TEST 8: REGISTER CON BODY NULO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * - body es null (inválido)
     * -> llama a onError(null)
     */
    @Test
    fun register_error_body_null_onError_null() {

        val apiService = mock(ApiService::class.java)
        val call = mock(Call::class.java) as Call<RegisterResponse>
        val repository = AuthRepository(apiService)

        val request = RegisterRequest("username", "mail@test.tst", "password")

        `when`(apiService.register(request)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<RegisterResponse>>(0)
            callback.onResponse(call, Response.success(null))
            null
        }.`when`(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.register(
            request,
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertNull(errorCode)

    }

    /**
     * TEST 9: REGISTER CON ERROR 500 (error del servidor)
     *
     * Comprueba:
     * - la API devuelve error 500
     * -> llama a onError 500
     */
    @Test
    fun register_error_500_onError() {

        val apiService = mock(ApiService::class.java)
        val call = mock(Call::class.java) as Call<RegisterResponse>
        val repository = AuthRepository(apiService)

        val request = RegisterRequest("username", "mail@test.tst", "password")

        `when`(apiService.register(request)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<RegisterResponse>>(0)
            callback.onResponse(
                call,
                Response.error(500,
                    "Error server".toResponseBody(null))
            )
            null
        }.`when`(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.register(
            request,
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertEquals(500, errorCode)

    }

    /**
     * TEST 10: REGISTER CON ERROR 409 (usuario ya existe)
     *
     * Comprueba:
     * - la API devuelve error 409
     * -> llama a onError 409
     */
    @Test
    fun register_error_409_onError() {

        val apiService = mock(ApiService::class.java)
        val call = mock(Call::class.java) as Call<RegisterResponse>
        val repository = AuthRepository(apiService)

        val request = RegisterRequest("username", "mail@test.tst", "password")

        `when`(apiService.register(request)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<RegisterResponse>>(0)
            callback.onResponse(
                call,
                Response.error(409,
                    "Conflict".toResponseBody(null))
            )
            null
        }.`when`(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.register(
            request,
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertEquals(409, errorCode)

    }

    /**
     * TEST 11: REGISTER CON ERROR 400 (datos inválidos)
     *
     * Comprueba:
     * - la API devuelve error 400
     * -> llama a onError 400
     */
    @Test
    fun register_error_400_onError() {

        val apiService = mock(ApiService::class.java)
        val call = mock(Call::class.java) as Call<RegisterResponse>
        val repository = AuthRepository(apiService)

        val request = RegisterRequest("username", "mail@test.tst", "password")

        `when`(apiService.register(request)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<RegisterResponse>>(0)
            callback.onResponse(
                call,
                Response.error(400,
                    "Invalida data".toResponseBody(null))
            )
            null
        }.`when`(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.register(
            request,
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertEquals(400, errorCode)

    }

    /**
     * TEST 12: REGISTER CON FALLO POR RED
     *
     * Comprueba:
     * - falla la conexión (onFailure)
     * -> llama a onError(null)
     */
    @Test
    fun register_fallo_red_onError_null() {

        val apiService = mock(ApiService::class.java)
        val call = mock(Call::class.java) as Call<RegisterResponse>
        val repository = AuthRepository(apiService)

        val request = RegisterRequest("username", "mail@test.tst", "password")

        `when`(apiService.register(request)).thenReturn(call)

        doAnswer {
            val callback = it.getArgument<Callback<RegisterResponse>>(0)
            callback.onFailure(call, RuntimeException("Network error"))
            null
        }.`when`(call).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.register(
            request,
            onSuccess = { successCalled = true },
            onError = { errorCode = it }
        )

        assertFalse(successCalled)
        assertNull(errorCode)

    }

}
package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.auth.LoginRequest
import com.gracehopper.laserchessapp.data.model.auth.LoginResponse
import com.gracehopper.laserchessapp.data.model.auth.RegisterRequest
import com.gracehopper.laserchessapp.data.model.auth.RegisterResponse
import com.gracehopper.laserchessapp.data.remote.ApiService
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
 * Repositorio de prueba para la autenticación.
 */
class AuthRepositoryTest {

    /**
     * TEST 1: LOGIN CON ÉXITO
     *
     * Comprueba:
     * - la API devuelve éxito (200)
     * - body NO es null
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

        // cuando se llame a apiSerice.login -> devolver call falso
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
     * TEST 2: LOGIN CON ERROR 401 (credenciales incorrectas)
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
                    okhttp3.ResponseBody.create(null, "Unauthorized"))
            )
            null
        }.`when`(call).enqueue(any())

        var errorCode: Int? = null

        repository.login(
            request,
            onSuccess = {},
            onError = { errorCode = it }
        )

        assertEquals(401, errorCode)
    }

    /**
     * TEST 3: LOGIN CON FALLO POR RED
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

        var errorCode: Int? = 999

        repository.login(
            request,
            onSuccess = {},
            onError = { errorCode = it }
        )

        assertNull(errorCode)

    }

    /**
     * TEST 4: REGISTER CON ÉXITO
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
     * TEST 5: REGISTER CON ERROR (usuario ya existe)
     *
     * Comprueba:
     * - la API devuelve error 409
     * -> llama a onError 409
     */
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
                    okhttp3.ResponseBody.create(null, "Conflict"))
            )
            null
        }.`when`(call).enqueue(any())

        var errorCode: Int? = null

        repository.register(
            request,
            onSuccess = {},
            onError = { errorCode = it }
        )

        assertEquals(409, errorCode)

    }

}
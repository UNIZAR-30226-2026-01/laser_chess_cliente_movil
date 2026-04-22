package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.ranking.AllRatingsResponse
import com.gracehopper.laserchessapp.data.model.user.AccountResponse
import com.gracehopper.laserchessapp.data.model.user.MyAccountResponse
import com.gracehopper.laserchessapp.data.model.user.MyProfile
import com.gracehopper.laserchessapp.data.model.user.UpdateAccountRequest
import com.gracehopper.laserchessapp.data.model.user.UserProfile
import com.gracehopper.laserchessapp.data.model.user.UserRatings
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
 * Tests unitarios para UserRepository.
 */
class UserRepositoryTest {

    /**
     * TEST 1: ÉXITO
     *
     * Comprueba:
     * - respuesta correcta
     * -> llama a onSuccess
     */
    @Test
    fun getMyProfile_exito_onSuccess() {

        // creo mocks
        val apiService = mock<ApiService>()
        val accountCall = mock<Call<MyAccountResponse>>()
        val ratingsCall = mock<Call<AllRatingsResponse>>()

        // creo repo con el apiService falso
        val repository = UserRepository(apiService)

        val myAccount = MyAccountResponse(
            accountId = 1L,
            mail = "mail@test.ts",
            username = "username",
            avatar = 1,
            level = 1,
            xp = 100,
            money = 100,
            boardSkin = 1,
            pieceSkin = 1,
            winAnimation = 1
        )

        val ratings = AllRatingsResponse(
            userId = 1L,
            blitz = 1100,
            rapid = 1200,
            classic = 1300,
            extended = 1400
        )

        // cuando se llame a apiService.getMyAccount -> devolver call falso
        whenever(apiService.getMyAccount()).thenReturn(accountCall)
        // cuando se llame a apiService.getRatings -> devolver call falso
        whenever(apiService.getRatings(1L)).thenReturn(ratingsCall)

        // simulo que la API responde correctamente
        doAnswer {
            val callback = it.getArgument<Callback<MyAccountResponse>>(0)
            callback.onResponse(accountCall, Response.success(myAccount))
            null
        }.whenever(accountCall).enqueue(any())

        doAnswer {
            val callback = it.getArgument<Callback<AllRatingsResponse>>(0)
            callback.onResponse(ratingsCall, Response.success(ratings))
            null
        }.whenever(ratingsCall).enqueue(any())

        var successCalled = false
        var receivedProfile: MyProfile? = null
        var errorCalled = false

        // ejecuto getMyProfile
        repository.getMyProfile(
            onSuccess = {
                successCalled = true
                receivedProfile = it
            },
            onError = {
                errorCalled = true
            }
        )

        // verifico resultados
        assertTrue(successCalled)
        assertFalse(errorCalled)
        assertEquals(1L, receivedProfile?.id)
        assertEquals("mail@test.ts", receivedProfile?.mail)
        assertEquals("username", receivedProfile?.username)
        assertEquals(UserRatings(1100, 1200, 1300, 1400), receivedProfile?.ratings)

    }

    /**
     * TEST 2: BODY NULO AL RECUPERAR EL PERFIL
     *
     * Comprueba:
     * - respuesta con body nulo
     * -> llama a onError
     */
    @Test
    fun getMyProfile_error_myAccount_body_nulo_onError() {

        val apiService = mock<ApiService>()
        val accountCall = mock<Call<MyAccountResponse>>()

        val repository = UserRepository(apiService)

        whenever(apiService.getMyAccount()).thenReturn(accountCall)

        doAnswer {
            val callback = it.getArgument<Callback<MyAccountResponse>>(0)
            callback.onResponse(accountCall, Response.success(null))
            null
        }.whenever(accountCall).enqueue(any())

        var successCalled = false
        var errorCalled = false

        repository.getMyProfile(
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
     * TEST 3: FALLO RED AL RECUPERAR RATINGS
     *
     * Comprueba:
     * - falla la conexión (onFailure) al recuperar ratings
     * -> llama a onError(null)
     */
    @Test
    fun getMyProfile_error_ratings_fallo_red_onError_null() {

        val apiService = mock<ApiService>()
        val accountCall = mock<Call<MyAccountResponse>>()
        val ratingsCall = mock<Call<AllRatingsResponse>>()

        val repository = UserRepository(apiService)

        val myAccount = MyAccountResponse(
            accountId = 1L,
            mail = "mail@test.ts",
            username = "username",
            avatar = 1,
            level = 1,
            xp = 100,
            money = 100,
            boardSkin = 1,
            pieceSkin = 1,
            winAnimation = 1
        )

        whenever(apiService.getMyAccount()).thenReturn(accountCall)
        whenever(apiService.getRatings(1L)).thenReturn(ratingsCall)

        doAnswer {
            val callback = it.getArgument<Callback<MyAccountResponse>>(0)
            callback.onResponse(accountCall, Response.success(myAccount))
            null
        }.whenever(accountCall).enqueue(any())

        doAnswer {
            val callback = it.getArgument<Callback<AllRatingsResponse>>(0)
            callback.onFailure(ratingsCall, RuntimeException("Network error"))
            null
        }.whenever(ratingsCall).enqueue(any())

        var successCalled = false
        var errorCalled = false

        repository.getMyProfile(
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
     * TEST 4: ÉXITO AL RECUPERAR EL PERFIL
     *
     * Comprueba:
     * - respuesta correcta
     * -> llama a onSuccess
     */
    @Test
    fun getUserProfile_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val accountCall = mock<Call<AccountResponse>>()
        val ratingsCall = mock<Call<AllRatingsResponse>>()

        val repository = UserRepository(apiService)

        val account = AccountResponse(
            accountId = 2L,
            username = "username2",
            avatar = 2,
            level = 2,
            xp = 200,
            boardSkin = 2,
            pieceSkin = 2,
            winAnimation = 2
        )

        val ratings = AllRatingsResponse(
            userId = 2L,
            blitz = 1100,
            rapid = 1200,
            classic = 1300,
            extended = 1400
        )

        whenever(apiService.getAccount(2L)).thenReturn(accountCall)
        whenever(apiService.getRatings(2L)).thenReturn(ratingsCall)

        doAnswer {
            val callback = it.getArgument<Callback<AccountResponse>>(0)
            callback.onResponse(accountCall, Response.success(account))
            null
        }.whenever(accountCall).enqueue(any())

        doAnswer {
            val callback = it.getArgument<Callback<AllRatingsResponse>>(0)
            callback.onResponse(ratingsCall, Response.success(ratings))
            null
        }.whenever(ratingsCall).enqueue(any())

        var successCalled = false
        var errorCalled = false
        var receivedProfile: UserProfile? = null

        repository.getUserProfile(
            userId = 2L,
            onSuccess = {
                successCalled = true
                receivedProfile = it
            },
            onError = {
                errorCalled = true
            }
        )

        assertTrue(successCalled)
        assertFalse(errorCalled)
        assertEquals(2L, receivedProfile?.id)
        assertEquals("username2", receivedProfile?.username)
        assertEquals(UserRatings(1100, 1200, 1300, 1400), receivedProfile?.ratings)

    }

    /**
     * TEST 5: ERROR AL RECUPERAR EL PERFIL
     *
     * Comprueba:
     * - respuesta con error
     * -> llama a onError
     */
    @Test
    fun getUserProfile_error_account_404_onError() {

        val apiService = mock<ApiService>()
        val accountCall = mock<Call<AccountResponse>>()

        val repository = UserRepository(apiService)

        whenever(apiService.getAccount(2L)).thenReturn(accountCall)

        doAnswer {
            val callback = it.getArgument<Callback<AccountResponse>>(0)
            callback.onResponse(accountCall, Response.error(404, "No encontrado".toResponseBody(null)))
            null
        }.whenever(accountCall).enqueue(any())

        var successCalled = false
        var errorCalled = false

        repository.getUserProfile(
            userId = 2L,
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
     * TEST 6: ÉXITO AL ACTUALIZAR EL PERFIL RECUPERANDO RATINGS
     *
     * Comprueba:
     * - respuesta correcta
     * - ratings recuperados correctamente
     * -> llama a onSuccess
     */
    @Test
    fun updateMyProfile_exito_obteniendo_ratings_onSuccess() {

        val apiService = mock<ApiService>()
        val updateCall = mock<Call<MyAccountResponse>>()
        val ratingsCall = mock<Call<AllRatingsResponse>>()

        val repository = UserRepository(apiService)

        val updatedAccount = MyAccountResponse(
            accountId = 1L,
            mail = "mail@test.ts",
            username = "newUsername",
            avatar = 1,
            level = 1,
            xp = 100,
            money = 100,
            boardSkin = 1,
            pieceSkin = 1,
            winAnimation = 1
        )

        val ratings = AllRatingsResponse(
            userId = 1L,
            blitz = 1100,
            rapid = 1200,
            classic = 1300,
            extended = 1400
        )

        whenever(apiService.updateMyAccount(any())).thenReturn(updateCall)
        whenever(apiService.getRatings(1L)).thenReturn(ratingsCall)

        doAnswer {
            val callback = it.getArgument<Callback<MyAccountResponse>>(0)
            callback.onResponse(updateCall, Response.success(updatedAccount))
            null
        }.whenever(updateCall).enqueue(any())

        doAnswer {
            val callback = it.getArgument<Callback<AllRatingsResponse>>(0)
            callback.onResponse(ratingsCall, Response.success(ratings))
            null
        }.whenever(ratingsCall).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null
        var receivedProfile: MyProfile? = null

        repository.updateMyProfile(
            request = UpdateAccountRequest(username = "newUsername"),
            onSuccess = {
                successCalled = true
                receivedProfile = it
            },
            onError = {
                errorCode = it
            }
        )

        assertTrue(successCalled)
        assertNull(errorCode)
        assertEquals(1L, receivedProfile?.id)
        assertEquals("newUsername", receivedProfile?.username)
        assertEquals(UserRatings(1100, 1200, 1300, 1400), receivedProfile?.ratings)

    }

    /**
     * TEST 7: ERROR AL ACTUALIZAR EL PERFIL
     *
     * Comprueba:
     * - respuesta con error
     * -> llama a onError
     */
    @Test
    fun updateMyProfile_error_400_onError() {

        val apiService = mock<ApiService>()
        val updateCall = mock<Call<MyAccountResponse>>()

        val repository = UserRepository(apiService)

        whenever(apiService.updateMyAccount(any())).thenReturn(updateCall)

        doAnswer {
            val callback = it.getArgument<Callback<MyAccountResponse>>(0)
            callback.onResponse(updateCall, Response.error(400, "Bad request".toResponseBody()))
            null
        }.whenever(updateCall).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null

        repository.updateMyProfile(
            request = UpdateAccountRequest(username = "bad_request"),
            onSuccess = {
                successCalled = true
            },
            onError = {
                errorCode = it
            }
        )

        assertFalse(successCalled)
        assertEquals(400, errorCode)

    }

    /**
     * TEST 8: FALLO RED AL ACTUALIZAR EL PERFIL
     *
     * Comprueba:
     * - falla la conexión (onFailure) al actualizar
     * -> llama a onError(null)
     */
    @Test
    fun updateMyProfile_fallo_red_onError_null() {

        val apiService = mock<ApiService>()
        val updateCall = mock<Call<MyAccountResponse>>()

        val repository = UserRepository(apiService)

        whenever(apiService.updateMyAccount(any())).thenReturn(updateCall)

        doAnswer {
            val callback = it.getArgument<Callback<MyAccountResponse>>(0)
            callback.onFailure(updateCall, RuntimeException("Network error"))
            null
        }.whenever(updateCall).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999

        repository.updateMyProfile(
            request = UpdateAccountRequest(username = "network_error"),
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
     * TEST 9: ÉXITO AL ELIMINAR LA CUENTA
     *
     * Comprueba:
     * - respuesta correcta
     * -> llama a onSuccess
     */
    @Test
    fun deleteMyAccount_exito_onSuccess() {

        val apiService = mock<ApiService>()
        val deleteCall = mock<Call<Unit>>()

        val repository = UserRepository(apiService)

        whenever(apiService.deleteMyAccount()).thenReturn(deleteCall)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(deleteCall, Response.success(Unit))
            null
        }.whenever(deleteCall).enqueue(any())

        var successCalled = false
        var errorCalled = false


        repository.deleteMyAccount(
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
     * TEST 10: ERROR AL ELIMINAR LA CUENTA
     *
     * Comprueba:
     * - respuesta con error
     * -> llama a onError
     */
    @Test
    fun deleteMyAccount_error_500_onError() {

        val apiService = mock<ApiService>()
        val deleteCall = mock<Call<Unit>>()

        val repository = UserRepository(apiService)

        whenever(apiService.deleteMyAccount()).thenReturn(deleteCall)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onResponse(deleteCall, Response.error(500, "Server error".toResponseBody()))
            null
        }.whenever(deleteCall).enqueue(any())

        var successCalled = false
        var errorCode: Int? = null


        repository.deleteMyAccount(
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
     * TEST 11: FALLO RED AL ELIMINAR LA CUENTA
     *
     * Comprueba:
     * - falla la conexión (onFailure) al eliminar
     * -> llama a onError(null)
     */
    @Test
    fun deleteMyAccount_fallo_red_onError_null() {

        val apiService = mock<ApiService>()
        val deleteCall = mock<Call<Unit>>()

        val repository = UserRepository(apiService)

        whenever(apiService.deleteMyAccount()).thenReturn(deleteCall)

        doAnswer {
            val callback = it.getArgument<Callback<Unit>>(0)
            callback.onFailure(deleteCall, RuntimeException("Network error"))
            null
        }.whenever(deleteCall).enqueue(any())

        var successCalled = false
        var errorCode: Int? = 999


        repository.deleteMyAccount(
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

    // TODO: changePassword tests

}
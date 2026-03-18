package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.auth.AccountResponse
import com.gracehopper.laserchessapp.data.model.auth.LoginRequest
import com.gracehopper.laserchessapp.data.model.auth.LoginResponse
import com.gracehopper.laserchessapp.data.model.auth.RegisterRequest
import com.gracehopper.laserchessapp.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repositorio encargado de gestionar las operaciones de autenticación.
 *
 * Esta clase actúa como un mediador entre la capa de datos remota y el resto
 * de la aplicación, proporcionando métodos para el inicio de sesión y el
 * registro de nuevos usuarios.
 *
 * @property apiService Instancia de la interfaz de Retrofit para realizar las peticiones a la API
 */
class AuthRepository (private val apiService: ApiService) {

    /**
     * Realiza una petición para iniciar sesión en el sistema.
     *
     * @param request Objeto que contiene las credenciales del usuario
     * @return Objeto [Call] que envuelve la respuesta [LoginResponse]
     */
    fun login(request: LoginRequest, onSuccess: (LoginResponse) -> Unit,
              onError: (Int?) -> Unit) {
        apiService.login(request).enqueue(object: Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        onSuccess(loginResponse)
                    } else {
                        onError(null)
                    }
                } else {
                    onError(response.code())
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                onError(null)
            }
        })
    }

    /**
     * Realiza una petición para registrar una nueva cuenta de usuario.
     *
     * @param request Objeto que contiene los datos necesarios para el registro
     * @return Objeto [Call] que envuelve la respuesta [AccountResponse]
     */
    fun register(request: RegisterRequest, onSuccess: (AccountResponse) -> Unit,
                 onError: (Int?) -> Unit) {
        apiService.register(request).enqueue(object: Callback<AccountResponse> {

            override fun onResponse(
                call: Call<AccountResponse?>,
                response: Response<AccountResponse?>
            ) {
                if (response.isSuccessful) {
                    val accountResponse = response.body()
                    if (accountResponse != null) {
                        onSuccess(accountResponse)
                    } else {
                        onError(null)
                    }
                } else {
                    onError(response.code())
                }
            }

            override fun onFailure(call: Call<AccountResponse?>, t: Throwable) {
                onError(null)
            }

        })
    }
}
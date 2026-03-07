package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.AccountResponse
import com.gracehopper.laserchessapp.data.model.LoginRequest
import com.gracehopper.laserchessapp.data.model.LoginResponse
import com.gracehopper.laserchessapp.data.model.RegisterRequest
import com.gracehopper.laserchessapp.data.remote.ApiService
import retrofit2.Call

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
    fun login(request: LoginRequest): Call<LoginResponse> {
        return apiService.login(request)
    }

    /**
     * Realiza una petición para registrar una nueva cuenta de usuario.
     *
     * @param request Objeto que contiene los datos necesarios para el registro
     * @return Objeto [Call] que envuelve la respuesta [AccountResponse]
     */
    fun register(request: RegisterRequest): Call<AccountResponse> {
        return apiService.register(request)
    }
}
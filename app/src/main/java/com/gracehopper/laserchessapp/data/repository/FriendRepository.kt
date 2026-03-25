package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.social.CreateFriendshipRequest
import com.gracehopper.laserchessapp.data.model.social.FriendSummary
import com.gracehopper.laserchessapp.data.model.social.FriendshipStatusResponse
import com.gracehopper.laserchessapp.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repositorio encargado de gestionar las operaciones de amistad.
 *
 * Esta clase actúa como un mediador entre la capa de datos remota y el resto
 * de la aplicación, proporcionando métodos para obtener la lista de amigos.
 *
 * @property apiService Instancia de la interfaz de Retrofit para realizar las peticiones a la API
 */
class FriendRepository(private val apiService: ApiService) {

    /**
     * Obtiene la lista de amigos del usuario actual.
     *
     * @param onSuccess Callback que se ejecutará en caso de éxito.
     */
    fun getFriends(onSuccess: (List<FriendSummary>?) -> Unit,
                   onError: (Int?) -> Unit) {
        apiService.getFriendships().enqueue(object : Callback<List<FriendSummary>> {

            override fun onResponse(call: Call<List<FriendSummary>>,
                response: Response<List<FriendSummary>>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body().orEmpty())
                } else {
                    onError(response.code())
                }
            }

            override fun onFailure(call: Call<List<FriendSummary>>, t: Throwable) {
                android.util.Log.e("FriendRepository", "Network failure", t)
                onError(null)
            }
        })
    }

    /**
     * Agrega una nueva solicitud de amistad.
     *
     * @param request Objeto que contiene los datos de la solicitud.
     */
    fun addFriend (username: String, onSuccess: () -> Unit,
                   onError: (Int?) -> Unit) {
        val request = CreateFriendshipRequest(username)
        apiService.addFriend(request).enqueue(object : Callback<Unit> {

            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(response.code())
                }
            }

            override fun onFailure(call: Call<Unit?>, t: Throwable) {
                onError(null)
            }
        })
    }

    /**
     * Obtiene el estado de la solicitud de amistad entre dos usuarios.
     *
     * @param username Nombre de usuario del otro usuario
     */
    fun getFriendshipStatus(username: String, onSuccess: (FriendshipStatusResponse) -> Unit,
                            onError: (Int?) -> Unit) {
        apiService.getFriendshipStatus(username).enqueue(object : Callback<FriendshipStatusResponse> {
            override fun onResponse(call: Call<FriendshipStatusResponse>,
                response: Response<FriendshipStatusResponse>
            ) {
                if (response.isSuccessful) {
                    val status = response.body()
                    if (status != null) {
                        onSuccess(status)
                    } else {
                        onError(null)
                    }
                } else {
                    onError(response.code())
                }
            }

            override fun onFailure(call: Call<FriendshipStatusResponse>, t: Throwable) {
                onError(null)
            }

        })
    }

    fun acceptFriendship(username: String, onSuccess: () -> Unit,
                         onError: (Int?) -> Unit) {
        apiService.acceptFriendship(username).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(response.code())
                }
            }

            override fun onFailure(call: Call<Unit?>, t: Throwable) {
                onError(null)
            }
        })
    }

    /**
     * Elimina una solicitud de amistad.
     *
     * @param username Nombre de usuario del otro usuario
     */
    fun deleteFriendship(username: String, onSuccess: () -> Unit,
                         onError: (Int?) -> Unit) {
        apiService.deleteFriendship(username).enqueue(object : Callback<Unit> {

            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(response.code())
                }
            }

            override fun onFailure(call: Call<Unit?>, t: Throwable) {
                onError(null)
            }
        })

    }

}
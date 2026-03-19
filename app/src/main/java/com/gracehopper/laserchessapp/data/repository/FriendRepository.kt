package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.social.FriendSummary
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
}
package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.game.ChallengeCountResponse
import com.gracehopper.laserchessapp.data.model.game.PendingChallengeResponse
import com.gracehopper.laserchessapp.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repositorio encargado de gestionar las solicitudes de partidas amistosas.
 *
 * @param apiService Instancia de la interfaz de Retrofit para realizar las peticiones a la API
 */
class ChallengeRepository(private val apiService: ApiService) {

    /**
     * Obtiene la lista de solicitudes de partidas amistosas pendientes.
     */
    fun getPendingChallenges(onSuccess: (List<PendingChallengeResponse>) -> Unit,
                             onError: (Int?) -> Unit) {

        apiService.getPendingChallenges()
            .enqueue(object : Callback<List<PendingChallengeResponse>> {

                override fun onResponse(
                    call : Call<List<PendingChallengeResponse>>,
                    response : Response<List<PendingChallengeResponse>>
                ) {

                    if (response.isSuccessful) {
                        onSuccess(response.body().orEmpty())
                    } else {
                        onError(response.code())
                    }

                }

                override fun onFailure(
                    call : Call<List<PendingChallengeResponse>>,
                    t : Throwable
                ) {
                    onError(null)
                }

            }
        )

    }

    /**
     * Obtiene el número de retos pendientes.
     */
    fun getChallengeCount(onSuccess: (Int) -> Unit,
                          onError: (Int?) -> Unit) {

        apiService.getChallengeCount()
            .enqueue(object : Callback<ChallengeCountResponse> {

                override fun onResponse(
                    call: Call<ChallengeCountResponse>,
                    response: Response<ChallengeCountResponse>
                ) {
                    if (response.isSuccessful) {
                        onSuccess(response.body()?.count ?: 0)
                    } else {
                        onError(response.code())
                    }
                }

                override fun onFailure(
                    call: Call<ChallengeCountResponse>,
                    t: Throwable
                ) {
                    onError(null)
                }
            })
    }

}

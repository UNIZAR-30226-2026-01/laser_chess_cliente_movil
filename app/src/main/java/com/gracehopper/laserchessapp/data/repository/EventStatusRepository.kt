package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repositorio encargado de gestionar las operaciones relacionadas con el estado del usuario.
 */
class EventStatusRepository(
    private val apiService: ApiService
) {

    /**
     * Marca al usuario como online.
     */
    fun markOnline() {
        apiService.markOnline().enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}

            override fun onFailure(call: Call<Unit>, t: Throwable) {}
        })
    }

    /**
     * Marca al usuario como offline.
     */
    fun markOffline() {
        apiService.markOffline().enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}

            override fun onFailure(call: Call<Unit>, t: Throwable) {}
        })
    }
}
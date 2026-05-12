package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.notifications.RegisterDeviceRequest
import com.gracehopper.laserchessapp.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repositorio encargado de gestionar las operaciones relacionadas con el dispositivo.
 *
 * @property apiService Instancia de la interfaz de Retrofit para realizar las peticiones a la API
 */
class DeviceRepository(private val apiService: ApiService) {

    /**
     * Registra el dispositivo en el servidor.
     *
     * @param token Token del dispositivo
     */
    fun registerDevice(
        token: String,
        onSuccess: () -> Unit = {},
        onError: (Int?) -> Unit = {}
    ) {

        apiService.registerDevice(RegisterDeviceRequest(token)).enqueue(
            object : Callback<Unit> {

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

            }
        )

    }

    /**
     * Elimina el dispositivo del servidor.
     *
     * @param token Token del dispositivo
     */
    fun deleteDevice(
        token: String,
        onSuccess: () -> Unit = {},
        onError: (Int?) -> Unit = {}
    ) {

        apiService.deleteDevice(RegisterDeviceRequest(token)).enqueue(
            object : Callback<Unit> {

                override fun onResponse(call: Call<Unit?>, response: Response<Unit?>) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onError(response.code())
                    }
                }

                override fun onFailure(call: Call<Unit?>, t: Throwable) {
                    onError(null)
                }

            }
        )

    }

}
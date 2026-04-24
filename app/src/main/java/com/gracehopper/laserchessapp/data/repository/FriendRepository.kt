package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.model.social.CreateFriendshipRequest
import com.gracehopper.laserchessapp.data.model.social.FriendSummary
import com.gracehopper.laserchessapp.data.model.social.FriendshipStatusResponse
import com.gracehopper.laserchessapp.data.model.social.ReceivedRequestsResponse
import com.gracehopper.laserchessapp.data.model.user.UserFriendshipStatus
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
    fun getFriends(
        onSuccess: (List<FriendSummary>?) -> Unit,
        onError: (Int?) -> Unit
    ) {
        apiService.getFriendships().enqueue(object : Callback<List<FriendSummary>> {

            override fun onResponse(
                call: Call<List<FriendSummary>>,
                response: Response<List<FriendSummary>>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body().orEmpty())
                } else {
                    onError(response.code())
                }
            }

            override fun onFailure(call: Call<List<FriendSummary>>, t: Throwable) {
                onError(null)
            }
        })
    }

    /**
     * Agrega una nueva solicitud de amistad.
     *
     * @param username Nombre de usuario del otro usuario
     */
    fun addFriend(
        username: String, onSuccess: () -> Unit,
        onError: (Int?) -> Unit
    ) {
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

    fun getNumReceivedFriendshipRequests(
        onSuccess: (Int) -> Unit,
        onError: (Int?) -> Unit
    ) {

        apiService.getNumReceivedFriendshipRequests()
            .enqueue(object : Callback<ReceivedRequestsResponse> {

                override fun onResponse(
                    call: Call<ReceivedRequestsResponse>,
                    response: Response<ReceivedRequestsResponse>
                ) {
                    if (response.isSuccessful) {
                        onSuccess(response.body()?.count ?: 0)
                    } else {
                        onError(response.code())
                    }
                }

                override fun onFailure(
                    call: Call<ReceivedRequestsResponse>,
                    t: Throwable
                ) {
                    onError(null)
                }
            })
    }

    /**
     * Obtiene la lista de solicitudes de amistad recibidas.
     */
    fun getReceivedFriendshipRequests(
        onSuccess: (List<FriendSummary>) -> Unit,
        onError: (Int?) -> Unit
    ) {

        apiService.getReceivedFriendshipRequests()
            .enqueue(object : Callback<List<FriendSummary>> {

                override fun onResponse(
                    call: Call<List<FriendSummary>>,
                    response: Response<List<FriendSummary>>
                ) {
                    if (response.isSuccessful) {
                        onSuccess(response.body().orEmpty())
                    } else {
                        onError(response.code())
                    }
                }

                override fun onFailure(call: Call<List<FriendSummary>>, t: Throwable) {
                    onError(null)
                }
            })

    }

    /**
     * Obtiene la lista de solicitudes de amistad enviadas.
     */
    fun getSentFriendshipRequests(
        onSuccess: (List<FriendSummary>) -> Unit,
        onError: (Int?) -> Unit
    ) {

        apiService.getSentFriendshipRequests()
            .enqueue(object : Callback<List<FriendSummary>> {

                override fun onResponse(
                    call: Call<List<FriendSummary>>,
                    response: Response<List<FriendSummary>>
                ) {

                    if (response.isSuccessful) {
                        onSuccess(response.body().orEmpty())
                    } else {
                        onError(response.code())
                    }
                }

                override fun onFailure(call: Call<List<FriendSummary>>, t: Throwable) {
                    onError(null)
                }
            })

    }


    /**
     * Obtiene el estado de la solicitud de amistad entre dos usuarios.
     *
     * @param username Nombre de usuario del otro usuario
     */
    fun getFriendshipStatus(
        username: String,
        onSuccess: (UserFriendshipStatus) -> Unit,
        onError: (Int?) -> Unit
    ) {

        apiService.getFriendshipStatus(username).enqueue(
            object : Callback<FriendshipStatusResponse> {

                override fun onResponse(
                    call: Call<FriendshipStatusResponse>,
                    response: Response<FriendshipStatusResponse>
                ) {
                    if (!response.isSuccessful) {

                        when (response.code()) {
                            404 -> onSuccess(UserFriendshipStatus.NON_FRIEND)
                            else -> onError(response.code())
                        }
                        return

                    }

                    val status = response.body()

                    if (status == null) {
                        onError(null)
                        return
                    }

                    val myId = CurrentUserManager.getMyCurrentId()
                    if (myId == null) {
                        onError(null)
                        return
                    }

                    val imSender = status.senderId == myId

                    val friendshipStatus = when {

                        // Si ambas personas han aceptado, ya existe amistad
                        status.senderAccept && status.receiverAccept -> {
                            UserFriendshipStatus.FRIEND
                        }

                        // A partir de aquí, al menos una debe ser falsa

                        // Yo la emito
                        imSender && status.senderAccept -> {
                            UserFriendshipStatus.SENT_REQUEST
                        }

                        // La emite la otra persona, pero no la ha aceptado aún
                        !imSender && status.senderAccept -> {
                            UserFriendshipStatus.RECEIVED_REQUEST
                        }

                        // La emito yo pero no la ha aceptado? y la otra persona sí
                        imSender && status.receiverAccept -> {
                            UserFriendshipStatus.RECEIVED_REQUEST
                        }

                        // La emite la otra persona y no la ha aceptado, yo sí
                        !imSender && status.receiverAccept -> {
                            UserFriendshipStatus.SENT_REQUEST
                        }

                        // Ni la otra persona ni yo la hemos aceptado, no hay amistad
                        else -> {
                            UserFriendshipStatus.NON_FRIEND
                        }

                    }

                    onSuccess(friendshipStatus)

                }

                override fun onFailure(
                    call: Call<FriendshipStatusResponse>,
                    t: Throwable
                ) {
                    onError(null)
                }

            }
        )

    }

    /**
     * Acepta una solicitud de amistad.
     *
     * @param username Nombre de usuario del emisor de la solicitud
     */
    fun acceptFriendship(
        username: String, onSuccess: () -> Unit,
        onError: (Int?) -> Unit
    ) {

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
    fun deleteFriendship(
        username: String, onSuccess: () -> Unit,
        onError: (Int?) -> Unit
    ) {

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
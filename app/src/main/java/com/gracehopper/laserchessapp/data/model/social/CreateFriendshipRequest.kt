package com.gracehopper.laserchessapp.data.model.social

import com.google.gson.annotations.SerializedName

/**
 * Clase de solicitud para crear una nueva amistad.
 */
data class CreateFriendshipRequest (
    @SerializedName("username") val username: String
)
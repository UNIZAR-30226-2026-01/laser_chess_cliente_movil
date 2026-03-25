package com.gracehopper.laserchessapp.data.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Clase de datos que representa una respuesta de registro de una cuenta.
 *
 * @property accountId Identificador único de la cuenta registrada
 */
data class RegisterResponse(
    @SerializedName("account_id") val accountId: Long
)
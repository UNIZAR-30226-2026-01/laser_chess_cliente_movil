package com.gracehopper.laserchessapp.data.model.user

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para la respuesta de la API de información de XP.
 *
 * @property xp Cantidad de experiencia acumulada.
 * @property requiredXp Cantidad mínima de experiencia requerida para el siguiente nivel.
 */
data class XPInfoResponse (
    @SerializedName("xp") val xp: Long,
    @SerializedName("required_xp") val requiredXp: Long
)
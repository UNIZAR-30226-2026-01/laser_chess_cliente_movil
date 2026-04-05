package com.gracehopper.laserchessapp.data.model.user

import com.google.gson.annotations.SerializedName

/**
 * Clase de datos que representa una solicitud de update de una cuenta.
 *
 * @property username Nombre de usuario
 * @property mail Dirección de correo electrónico del usuario
 * @property avatar Skin de avatar
 * @property boardSkin Skin del tablero
 * @property pieceSkin Skin de las piezas
 * @property winAnimation Animación de fin de partida
 */
data class UpdateAccountRequest(
    @SerializedName("username") val username: String? = null,
    @SerializedName("mail") val mail: String? = null,
    @SerializedName("avatar") val avatar: Int? = null,
    @SerializedName("board_skin") val boardSkin: Int? = null,
    @SerializedName("piece_skin") val pieceSkin: Int? = null,
    @SerializedName("win_animation") val winAnimation: Int? = null
)
package com.gracehopper.laserchessapp.data.model.user

import com.google.gson.annotations.SerializedName

/**
 * Clase que representa mi perfil de usuario.
 *
 * @property accountId Identificador único del usuario
 * @property mail Dirección de correo electrónico del usuario
 * @property username Nombre de usuario
 * @property avatar Skin de avatar
 * @property level Nivel del usuario
 * @property xp Puntos de experiencia del usuario
 * @property money Monedas virtuales del usuario
 * @property boardSkin Skin del tablero
 * @property pieceSkin Skin de las piezas
 * @property winAnimation Animación de fin de partida
 */
data class MyAccountResponse(
    @SerializedName("account_id") val accountId: Long,
    @SerializedName("mail") val mail: String,
    @SerializedName("username") val username: String,
    @SerializedName("avatar") val avatar: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("xp") val xp: Int,
    @SerializedName("money") val money: Int,
    @SerializedName("board_skin") val boardSkin: Int,
    @SerializedName("piece_skin") val pieceSkin: Int,
    @SerializedName("win_animation") val winAnimation: Int
)
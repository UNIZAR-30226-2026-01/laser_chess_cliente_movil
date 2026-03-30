package com.gracehopper.laserchessapp.data.model.game

import com.gracehopper.laserchessapp.data.model.user.TimeMode

/**
 * Clase de datos que representa un resumen de una partida en progreso.
 *
 * @property id Identificador único de la partida ?
 * @property myTime Tiempo actual del jugador
 * @property opponentUsername Nombre de usuario del oponente
 * @property opponentTime Tiempo actual del oponente
 * @property timeMode Modo de tiempo utilizado
 * @property boardType Tipo de tablero utilizado
 */
data class InProgressGameSummary(
    val id: String,
    val myTime: String,
    val opponentUsername: String,
    val opponentTime: String,
    val timeMode: TimeMode,
    val boardType: BoardType
)
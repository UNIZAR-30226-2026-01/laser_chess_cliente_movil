package com.gracehopper.laserchessapp.data.model.game

import com.gracehopper.laserchessapp.data.model.user.TimeMode
import java.io.Serializable

/**
 * Clase de datos que representa la configuración de la partida
 *
 * @property boardId Identificador del tablero
 * @property boardName Nombre del tablero
 * @property mode Modo de tiempo
 * @property startingTimeSeconds Tiempo de inicio en segundos
 * @property incrementSeconds Incremento de tiempo en segundos
 */
data class GameConfig (
    var boardId: Int? = null,
    var boardName: String? = null,
    var mode: TimeMode = TimeMode.BLITZ,
    var startingTimeSeconds: Int = 300, // 5 min
    var incrementSeconds: Int = 0,
    var isCustom: Boolean = false
) : Serializable
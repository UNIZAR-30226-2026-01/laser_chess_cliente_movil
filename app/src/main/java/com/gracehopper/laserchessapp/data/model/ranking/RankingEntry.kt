package com.gracehopper.laserchessapp.data.model.ranking

import com.gracehopper.laserchessapp.data.model.user.TimeMode

/**
 * Clase que representa un perfil del ranking.
 *
 * @property id Identificador único del usuario
 * @property username Nombre de usuario
 * @property avatar Skin de avatar
 * @property elo Elo del usuario
 * @property timeMode Modo de juego
 */
data class RankingEntry (
    val id: String,
    val username: String,
    val avatar: Int,

    // 2 posibles casos:--------------------------------------------------------
    // 1. backend nos devuelve el elo concreto de un modo de tiempo:
    val elo: Int,
    val timeMode: TimeMode
    // o -----------------------------------------------------------------------
    // 2. emplear userRatings, tener todo guardado y a la hora de elegir
    //    cuál mostrar se emplea entry.ratings.blitz y tal
    // val ratings: UserRatings
)
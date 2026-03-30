package com.gracehopper.laserchessapp.data.model.game

/**
 * Clase de datos que representa una opción de tablero.
 *
 * @property id Identificador único del tablero
 * @property name Nombre del tablero
 * @property image Recurso de imagen asociado al tablero
 */
data class BoardOption (
    val id: Int,
    val name: String,
    val image: Int
)
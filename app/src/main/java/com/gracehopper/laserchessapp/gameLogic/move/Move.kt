package com.gracehopper.laserchessapp.gameLogic.move

data class Move(
    val type: Char,             // T (Traslación); R (rot der [right]) , L (rot izq [left])
    val from: String,           // Casilla de origen
    val to: String?,            // Casilla destino (en caso de traslación)
    val destroyed: String?,     // Casilla en la que una pieza ha sido destruida
    val timer: Double?             // Timer en ms
)

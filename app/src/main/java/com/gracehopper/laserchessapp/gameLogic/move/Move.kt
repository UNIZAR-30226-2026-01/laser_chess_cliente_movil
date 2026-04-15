package com.gracehopper.laserchessapp.gameLogic.move

/**
 * Data class que representa un movimiento recibido del servidor.
 *
 * Contiene toda la información necesaria para aplicar el movimiento
 * en el tablero y actualizar el estado de la partida.
 */
data class Move(
    val type: Char,             // T = Traslación, R = Rotación derecha, L = Rotación izquierda
    val from: String,           // Casilla de origen
    val to: String?,            // Casilla destino (en caso de traslación)
    val destroyed: String?,     // Casilla en la que una pieza ha sido destruida
    val laserPath: String?,     // Ruta del laser
    val timer: Double?          // Tiempo restante o actualización de timer
)

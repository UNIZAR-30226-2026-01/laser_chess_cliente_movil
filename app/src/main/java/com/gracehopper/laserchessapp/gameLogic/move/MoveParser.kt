package com.gracehopper.laserchessapp.gameLogic.move

/**
 * Objeto encargado de parsear un movimiento en formato string
 * recibido del servidor a un objeto Move.
 */
object MoveParser {

    /**
     * Convierte un string de movimiento en un objeto Move.
     *
     * Formato esperado:
     * - Tipo: T (traslación), R (rotación derecha), L (rotación izquierda)
     * - Movimiento: Tfrom:to
     * - Captura: xcasilla
     * - Timer: %{valor}
     *
     * @param moveStr Movimiento en formato string
     * @return Objeto Move parseado
     */
    fun parseMove(moveStr: String): Move {
        val clean = moveStr.removeSuffix(";")
        val parts = clean.split("%")

        val movePart = parts[0]
        val laserPart = parts[1]
        val timerPart = parts[2]

        val type = movePart[0]

        var from: String
        var to: String? = null
        var destroyed: String? = null

        var msgMove = movePart

        /**
         * Timer
         */
        val timer = timerPart
            .substringAfter("{")
            .substringBefore("}")
            .toDouble()

        /**
         * Extraer captura (x...)
         */
        if (msgMove.contains("x")) {
            destroyed = msgMove.substringAfter("x")
            msgMove = msgMove.substringBefore("x")
        }

        /**
         * Determinar si es traslación o rotación:
         * - Traslación: contiene ":"
         * - Rotación: solo tiene origen
         */
        if (msgMove.contains(":")) {
            from = msgMove.substringAfter(type).substringBefore(":")
            to = msgMove.substringAfter(":")
        } else {
            from = msgMove.substring(1)
        }

        return Move(type, from, to, destroyed,laserPart, timer)
    }
}
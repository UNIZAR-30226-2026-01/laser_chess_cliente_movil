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
        val type = moveStr[0]           // T, R, L

        var from: String
        var to: String? = null
        var destroyed: String? = null
        var timer: Double? = null

        var msgMove = moveStr

        /**
         * Extraer timer(%{...})
         */
        if (moveStr.contains("%{")) {
            val timerStr = moveStr.substringAfter("%{").substringBefore("}")
            timer = timerStr.toDouble()
            msgMove = moveStr.substringBefore("%")
        }

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
            from = msgMove.substringAfter("T").substringBefore(":")
            to = msgMove.substringAfter(":")
        } else {
            from = msgMove.substring(1)             // Despues de la R o L
        }

        return Move(type, from, to, destroyed, timer)
    }
}
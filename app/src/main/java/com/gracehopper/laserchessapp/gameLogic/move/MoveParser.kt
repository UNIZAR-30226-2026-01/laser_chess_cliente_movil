package com.gracehopper.laserchessapp.gameLogic.move

object MoveParser {

    fun parseMove(moveStr: String): Move {
        val type = moveStr[0]           // T, R, L

        var from = ""
        var to: String? = null
        var destroyed: String? = null
        var timer: Int? = null

        var msgMove = moveStr

        // Al recibir mensaje, extraemos los datos
        // Timer
        if (moveStr.contains("%{")) {
            val timerStr = moveStr.substringAfter("%{").substringBefore("}")
            timer = timerStr.toInt()
            msgMove = moveStr.substringBefore("%")
        }

        // Captura
        if (msgMove.contains("x")) {
            destroyed = msgMove.substringAfter("x")
            msgMove = msgMove.substringBefore("x")
        }

        // Traslación o rotacion
        if (msgMove.contains(":")) {
            from = msgMove.substringAfter("T").substringBefore(":")
            to = msgMove.substringAfter(":")
        } else {
            from = msgMove.substring(1)             // Despues de la R o L
        }

        return Move(type, from, to, destroyed, timer)
    }
}
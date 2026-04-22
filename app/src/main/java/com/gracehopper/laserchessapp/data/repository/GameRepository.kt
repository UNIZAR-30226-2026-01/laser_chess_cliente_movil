package com.gracehopper.laserchessapp.data.repository

import com.google.gson.Gson
import com.gracehopper.laserchessapp.data.manager.ActiveGameManager
import com.gracehopper.laserchessapp.data.model.game.WSClientMessage
import com.gracehopper.laserchessapp.gameLogic.move.CoordsConverter

/**
 * Repositorio de comunicación con el backend para la partida.
 */
class GameRepository(
    private val sender: (String) -> Unit = { ActiveGameManager.sendGameMessage(it) }
) {

    private val gson = Gson()

    fun sendMessage(type: String, content: String) {
        val message = WSClientMessage(type, content)
        val json = gson.toJson(message)
        sender(json)
    }

    fun sendMove(from: Pair<Int, Int>, to: Pair<Int, Int>) {
        val fromNot = CoordsConverter.positionToNotation(from)
        val toNot = CoordsConverter.positionToNotation(to)
        sendMessage("Move", "T$fromNot:$toNot")
    }

    fun sendRotateRight(position: Pair<Int, Int>) {
        val notation = CoordsConverter.positionToNotation(position)
        sendMessage("Move", "R$notation")
    }

    fun sendRotateLeft(position: Pair<Int, Int>) {
        val notation = CoordsConverter.positionToNotation(position)
        sendMessage("Move", "L$notation")
    }

}
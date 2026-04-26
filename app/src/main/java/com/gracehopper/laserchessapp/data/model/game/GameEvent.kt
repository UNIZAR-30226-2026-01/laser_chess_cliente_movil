package com.gracehopper.laserchessapp.data.model.game

/**
 * Eventos de juego
 */
sealed class GameEvent {

    /**
     * Estado inicial de la partida
     *
     * @param boardCsv CSV del tablero inicial
     * @param redPlayerId ID del jugador rojo interno
     */
    data class InitialState(
        val boardCsv: String?,
        val redPlayerId: Long?
    ) : GameEvent()

    /**
     * Movimiento de pieza
     *
     * @param moveAndTime Movimiento y tiempo restante del jugador
     */
    data class Move(
        val moveAndTime: String?
    ) : GameEvent()

    /**
     * Estado del juego
     *
     * @param log Mensaje de estado del juego
     */
    data class State(
        val log: String?
    ) : GameEvent()

    /**
     * Petición de pausa
     */
    object PauseRequest : GameEvent()

    /**
     * Rechazar petición de pausa
     */
    object PauseReject : GameEvent()

    /**
     * Partida pausada
     */
    object Paused : GameEvent()

    /**
     * Fin de partida
     *
     * @param winner Nombre del winner
     * @param victoryCause Causa de la victoria
     */
    data class End(
        val winner: String?,
        val victoryCause: String?
    ) : GameEvent()

    /**
     * Error para mensajes lógicos del juego
     *
     * @param message Mensaje de error
     */
    data class Error(
        val message: String
    ) : GameEvent()

    /**
     * Solicitud de partida rechazada
     */
    object ChallengeRejected : GameEvent()

    /**
     * Conexión cerrada
     *
     * @param reason Razón del cierre de conexión
     */
    data class ConnectionClosed(
        val reason: String?
    ) : GameEvent()

    /**
     * El rival se ha desconectado
     */
    object OpponentDisconnected : GameEvent()

    /**
     * El rival se ha reconectado
     */
    object OpponentReconnected : GameEvent()

    /**
     * Reconexión del usuario a la partida
     *
     * @param opponentId ID del rival
     * @param remainingTime Tiempo restante en su timer
     */
    data class Reconnected(
        val opponentId: String?,
        val remainingTime: String?
    ) : GameEvent()


}
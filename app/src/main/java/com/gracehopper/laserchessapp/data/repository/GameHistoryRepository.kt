package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.model.game.BoardType
import com.gracehopper.laserchessapp.data.model.game.InProgressGameSummary
import com.gracehopper.laserchessapp.data.model.game.PausedGameResponse
import com.gracehopper.laserchessapp.data.model.user.TimeMode
import com.gracehopper.laserchessapp.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repositorio encargado de gestionar el historial de partidas.
 *
 * @property apiService Instancia de Retrofit para realizar peticiones a la API.
 */
class GameHistoryRepository(private val apiService: ApiService) {

    /**
     * Obtiene las partidas pausadas del usuario dado su ID.
     *
     * @param userId ID del usuario actual.
     * @param onSuccess Callback con la lista de [InProgressGameSummary] mapeada.
     * @param onError Callback con el código de error HTTP o null si es error de red.
     */
    fun getPausedGames(
        userId: Long,
        onSuccess: (List<InProgressGameSummary>) -> Unit,
        onError: (Int?) -> Unit
    ) {
        apiService.getPausedGames(userId).enqueue(object : Callback<List<PausedGameResponse>> {

            override fun onResponse(
                call: Call<List<PausedGameResponse>>,
                response: Response<List<PausedGameResponse>>
            ) {
                if (response.isSuccessful) {
                    val paused = response.body().orEmpty()
                    val mapped = paused.map { dto -> dto.toInProgressGameSummary(userId) }
                    onSuccess(mapped)
                } else {
                    onError(response.code())
                }
            }

            override fun onFailure(call: Call<List<PausedGameResponse>>, t: Throwable) {
                onError(null)
            }
        })
    }


    private fun PausedGameResponse.toInProgressGameSummary(myUserId: Long): InProgressGameSummary {
        val iAmP1 = (p1Id == myUserId)

        val opponentId = if (iAmP1) p2Id else p1Id
        val opponentUsername = if (iAmP1) p2Username else p1Username
        val timeBaseSeconds = timeBase / 1000
        val myTimeStr   = formatSeconds(timeBaseSeconds)
        val oppTimeStr  = formatSeconds(timeBaseSeconds)

        val timeMode = parseTimeMode(timeBaseSeconds)
        val boardType = parseBoardType(board)

        return InProgressGameSummary(
            id              = matchId.toString(),
            myTime          = myTimeStr,
            opponentId      = opponentId,
            opponentUsername = opponentUsername,
            opponentTime    = oppTimeStr,
            timeMode        = timeMode,
            boardType       = boardType,
            timeBaseMs      = timeBase,
            timeIncrement   = timeIncrement
        )
    }

    /** Convierte segundos a formato mm:ss */
    private fun formatSeconds(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    private fun parseTimeMode(timeBaseSeconds: Int): TimeMode {
        return when {
            timeBaseSeconds <= 300  -> TimeMode.BLITZ
            timeBaseSeconds <= 900  -> TimeMode.RAPID
            timeBaseSeconds <= 1800 -> TimeMode.CLASSIC
            else                    -> TimeMode.EXTENDED
        }
    }

    private fun parseBoardType(board: String): BoardType {
        return try {
            BoardType.valueOf(board.uppercase())
        } catch (_: IllegalArgumentException) {
            BoardType.ACE
        }
    }
}
package com.gracehopper.laserchessapp.data.repository

import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.model.ranking.RankingEntry
import com.gracehopper.laserchessapp.data.model.ranking.RankingEntryResponse
import com.gracehopper.laserchessapp.data.model.user.TimeMode
import com.gracehopper.laserchessapp.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repositorio encargado de gestionar las operaciones relacionadas con el ranking.
 *
 * @property apiService Instancia de la interfaz de Retrofit para realizar las peticiones a la API
 */
class RankingRepository(private val apiService: ApiService) {

    /**
     * Obtiene el ranking de un usuario dado su ID.
     *
     * @param eloType Tipo de elo
     * @param userId ID del usuario
     */
    fun getRankById(
        eloType: TimeMode,
        userId: Long,
        onSuccess: (Long) -> Unit,
        onError: () -> Unit
    ) {

        apiService.getRankById(eloType.name.lowercase(), userId).enqueue(
            object : Callback<Long> {

                override fun onResponse(call: Call<Long>, response: Response<Long>) {
                    val rank = response.body()

                    if (!response.isSuccessful || rank == null) {
                        onError()
                        return
                    }

                    onSuccess(rank)
                }

                override fun onFailure(call: Call<Long>, t: Throwable) {
                    onError()
                }
            }
        )
    }

    fun getTopRankUsers(
        eloType: TimeMode,
        onSuccess: (List<RankingEntry>) -> Unit,
        onError: (Int?) -> Unit
    ) {
        apiService.getTopRankUsers(eloType.name.lowercase()).enqueue(
            object : Callback<List<RankingEntryResponse>> {

                override fun onResponse(
                    call: Call<List<RankingEntryResponse>>,
                    response: Response<List<RankingEntryResponse>>
                ) {
                    val body = response.body()

                    if (!response.isSuccessful || body == null) {
                        onError(response.code())
                        return
                    }

                    val currentUserId = CurrentUserManager.getMyCurrentProfile()?.id

                    val rankingEntries = body.mapIndexed { index, entry ->
                        RankingEntry(
                            id = entry.id,
                            username = entry.username,
                            avatar = entry.avatar,
                            elo = entry.rating,
                            position = index + 1,
                            isCurrentUser = entry.id == currentUserId
                        )
                    }

                    onSuccess(rankingEntries)
                }

                override fun onFailure(
                    call: Call<List<RankingEntryResponse>>,
                    t: Throwable
                ) {
                    onError(null)
                }
            }
        )
    }

}
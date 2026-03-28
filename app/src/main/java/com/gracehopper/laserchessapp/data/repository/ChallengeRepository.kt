package com.gracehopper.laserchessapp.data.repository
/
import com.gracehopper.laserchessapp.data.model.game.PendingChallengeResponse
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChallengeRepository {

    private val apiService = NetworkUtils.getApiService()

    fun getPendingChallenges(onSuccess: (List<PendingChallengeResponse>) -> Unit,
                             onError: (Int?) -> Unit) {

        apiService.getPendingChallenges()
            .enqueue(object : Callback<List<PendingChallengeResponse>> {

                override fun onResponse(
                    call : Call<List<PendingChallengeResponse>>,
                    response : Response<List<PendingChallengeResponse>>
                ) {

                    if (response.isSuccessful) {
                        onSuccess(response.body().orEmpty())
                    } else {
                        onError(response.code())
                    }

                }

                override fun onFailure(
                    call : Call<List<PendingChallengeResponse>>,
                    t : Throwable
                ) {
                    onError(null)
                }

            }
        )

    }

}
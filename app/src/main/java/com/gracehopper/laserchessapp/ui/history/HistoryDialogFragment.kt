package com.gracehopper.laserchessapp.ui.history

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.game.GameResume
import com.gracehopper.laserchessapp.data.model.user.AccountResponse
import com.gracehopper.laserchessapp.data.remote.ApiService
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Diálogo para mostrar el historial de partidas terminadas.
 */
class HistoryDialogFragment : DialogFragment() {

    private lateinit var apiService: ApiService
    private lateinit var buttonClose: ImageButton
    private lateinit var recyclerHistory: RecyclerView
    private lateinit var textEmpty: TextView
    private lateinit var progressHistory: ProgressBar

    private lateinit var adapter: HistoryGameAdapter

    private val usernameCache = mutableMapOf<Long, String>()
    private var games: List<GameResume> = emptyList()
    private var pendingRequests = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiService = NetworkUtils.getApiService()
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClose    = view.findViewById(R.id.buttonCloseHistory)
        recyclerHistory = view.findViewById(R.id.recyclerHistory)
        textEmpty      = view.findViewById(R.id.textHistoryEmpty)
        progressHistory = view.findViewById(R.id.progressHistory)

        setupRecyclerView()
        setupListeners()
        loadHistory()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupRecyclerView() {
        adapter = HistoryGameAdapter(emptyList(), emptyMap()) { game ->

            val json = com.google.gson.Gson().toJson(game)

            requireContext()
                .getSharedPreferences("app", android.content.Context.MODE_PRIVATE)
                .edit()
                .putString("historyGame", json)
                .apply()

            startActivity(
                android.content.Intent(
                    requireContext(),
                    com.gracehopper.laserchessapp.ui.game.GameReplayActivity::class.java
                )
            )
        }
        recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        recyclerHistory.adapter = adapter
    }

    private fun setupListeners() {
        buttonClose.setOnClickListener { dismiss() }
    }

    private fun loadHistory() {
        val myId = TokenManager.getUserId() ?: run {
            showEmpty()
            return
        }

        showLoading()

        apiService.getFinishedGames(myId).enqueue(object : Callback<List<GameResume>> {
            override fun onResponse(
                call: Call<List<GameResume>>,
                response: Response<List<GameResume>>
            ) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    val data = response.body().orEmpty()
                    games = data
                    if (data.isEmpty()) {
                        activity?.runOnUiThread { showEmpty() }
                    } else {
                        loadUsernames(data)
                    }
                } else {
                    activity?.runOnUiThread {
                        showEmpty()
                        Toast.makeText(requireContext(),
                            "Error al cargar el historial",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<GameResume>>, t: Throwable) {
                if (!isAdded) return
                activity?.runOnUiThread {
                    showEmpty()
                    Toast.makeText(requireContext(),
                        "Sin conexión",
                        Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * Carga los nombres de usuario de los jugadores en segundo plano.
     */
    private fun loadUsernames(games: List<GameResume>) {
        val idsToFetch = games
            .flatMap { listOf(it.p1_id, it.p2_id) }
            .toSet()
            .filter { !usernameCache.containsKey(it) }

        if (idsToFetch.isEmpty()) {
            activity?.runOnUiThread { showGames() }
            return
        }

        pendingRequests = idsToFetch.size

        idsToFetch.forEach { userId ->
            apiService.getAccount(userId).enqueue(object : Callback<AccountResponse> {
                override fun onResponse(
                    call: Call<AccountResponse>,
                    response: Response<AccountResponse>
                ) {
                    if (!isAdded) return
                    response.body()?.let { account ->
                        usernameCache[account.accountId] = account.username
                    }
                    onRequestDone()
                }

                override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                    if (!isAdded) return
                    onRequestDone()
                }
            })
        }
    }

    private fun onRequestDone() {
        pendingRequests--
        if (pendingRequests <= 0) {
            activity?.runOnUiThread { showGames() }
        }
    }


    private fun showLoading() {
        progressHistory.visibility = View.VISIBLE
        recyclerHistory.visibility = View.GONE
        textEmpty.visibility = View.GONE
    }

    private fun showEmpty() {
        progressHistory.visibility = View.GONE
        recyclerHistory.visibility = View.GONE
        textEmpty.visibility = View.VISIBLE
    }

    private fun showGames() {
        progressHistory.visibility = View.GONE
        textEmpty.visibility = View.GONE
        adapter.updateData(games, usernameCache)
        recyclerHistory.visibility = View.VISIBLE
    }
}
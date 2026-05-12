package com.gracehopper.laserchessapp.ui.history

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.model.game.GameResume
import com.gracehopper.laserchessapp.data.model.user.AccountResponse
import com.gracehopper.laserchessapp.data.remote.ApiService
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.FriendRepository
import com.gracehopper.laserchessapp.ui.user.UserProfileDialogFragment
import com.gracehopper.laserchessapp.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.graphics.drawable.toDrawable
import androidx.core.content.edit

class HistoryDialogFragment : DialogFragment() {

    private lateinit var apiService: ApiService
    private lateinit var buttonClose: ImageButton
    private lateinit var friendRepository: FriendRepository
    private lateinit var recyclerHistory: RecyclerView
    private lateinit var textEmpty: TextView
    private lateinit var progressHistory: ProgressBar

    private lateinit var adapter: HistoryGameAdapter

    private val userCache = mutableMapOf<Long, AccountResponse>()
    private var games: List<GameResume> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiService = NetworkUtils.getApiService()
        friendRepository = FriendRepository(apiService)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_history, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topSection = view.findViewById<View>(R.id.dialog_top_section)
        buttonClose = topSection.findViewById(R.id.buttonCloseDialog)
        
        val dialogTitle = topSection.findViewById<TextView>(R.id.dialogTitle)
        val dialogIcon = topSection.findViewById<ImageView>(R.id.dialogIcon)
        
        dialogTitle.text = "Últimas partidas"
        
        dialogIcon.setImageResource(R.drawable.history_32px)
        dialogIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.LCWhite))

        recyclerHistory = view.findViewById(R.id.recyclerHistory)
        textEmpty = view.findViewById(R.id.textHistoryEmpty)
        progressHistory = view.findViewById(R.id.progressHistory)

        setupRecyclerView()
        setupListeners()
        loadHistory()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }

    private fun setupRecyclerView() {
        adapter = HistoryGameAdapter(
            games = emptyList(),
            userCache = userCache,
            onViewClick = { game ->
                val json = com.google.gson.Gson().toJson(game)

                requireContext()
                    .getSharedPreferences("app", android.content.Context.MODE_PRIVATE)
                    .edit {
                        putString("historyGame", json)
                    }

                startActivity(
                    android.content.Intent(
                        requireContext(),
                        com.gracehopper.laserchessapp.ui.game.GameReplayActivity::class.java
                    )
                )
            },
            onRivalClick = { game ->
                openRivalProfile(game)
            }
        )

        recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        recyclerHistory.adapter = adapter
    }

    private fun setupListeners() {
        buttonClose.setOnClickListener { dismiss() }
    }

    private fun loadHistory() {
        val myId = TokenManager.getUserId()

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
                        activity?.runOnUiThread { showGames() }
                        loadUsers(data)
                    }
                } else {
                    activity?.runOnUiThread {
                        showEmpty()
                        Toast.makeText(
                            requireContext(),
                            "Error al cargar el historial",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<GameResume>>, t: Throwable) {
                if (!isAdded) return
                activity?.runOnUiThread {
                    showEmpty()
                    Toast.makeText(
                        requireContext(),
                        "Sin conexión",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    /**
     * Carga usuaria y actualiza progresivamente
     */
    private fun loadUsers(games: List<GameResume>) {

        val idsToFetch = games
            .flatMap { listOf(it.p1Id, it.p2Id) }
            .toSet()
            .filter { !userCache.containsKey(it) }

        idsToFetch.forEach { userId ->
            apiService.getAccount(userId).enqueue(object : Callback<AccountResponse> {

                override fun onResponse(
                    call: Call<AccountResponse>,
                    response: Response<AccountResponse>
                ) {
                    if (!isAdded) return

                    response.body()?.let { account ->
                        userCache[account.accountId] = account

                        activity?.runOnUiThread {
                            adapter.updateData(games, userCache)
                        }
                    }
                }

                override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                    // ignoramos fallo individual
                }
            })
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

    private fun openRivalProfile(game: GameResume) {
        val myId = CurrentUserManager.getMyCurrentId()

        val rivalId = if (game.p1Id == myId) game.p2Id else game.p1Id

        val rivalAccount = userCache[rivalId]
        if (rivalAccount == null) {
            Toast.makeText(requireContext(), "No se pudo obtener el perfil del rival", Toast.LENGTH_SHORT).show()
            return
        }

        friendRepository.getFriendshipStatus(
            myId = myId,
            username = rivalAccount.username,
            onSuccess = { status ->
                activity?.runOnUiThread {
                    UserProfileDialogFragment.newInstance(
                        friendId = rivalId,
                        mode = status
                    ).show(parentFragmentManager, "UserProfileDialog")
                }
            },
            onError = {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "No se pudo cargar el estado de amistad", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun showGames() {
        progressHistory.visibility = View.GONE
        textEmpty.visibility = View.GONE
        adapter.updateData(games, userCache)
        recyclerHistory.visibility = View.VISIBLE
    }
}
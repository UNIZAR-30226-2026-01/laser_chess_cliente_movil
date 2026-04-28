package com.gracehopper.laserchessapp.ui.social

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.game.BoardType
import com.gracehopper.laserchessapp.data.model.game.InProgressGameSummary
import com.gracehopper.laserchessapp.data.model.social.FriendSummary
import com.gracehopper.laserchessapp.data.model.user.TimeMode
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.FriendRepository
import com.gracehopper.laserchessapp.databinding.FragmentSocialBinding
import com.gracehopper.laserchessapp.ui.social.FriendAdapter
import com.gracehopper.laserchessapp.ui.social.InProgressAdapter
import com.gracehopper.laserchessapp.ui.user.UserProfileDialogFragment
import com.gracehopper.laserchessapp.utils.validation.UsernameValidationResult
import com.gracehopper.laserchessapp.utils.validation.UsernameValidator

class SocialFragment : Fragment() {

    private var _binding: FragmentSocialBinding? = null
    private val binding get() = _binding!!

    private lateinit var friendsAdapter: FriendAdapter
    private lateinit var emptyMessage: TextView
    private lateinit var recyclerFriends : RecyclerView

    private lateinit var inProgressAdapter: InProgressAdapter

    private val repository by lazy {
        FriendRepository(NetworkUtils.getApiService())
    }

    private enum class SocialTab {
        SOCIAL, IN_PROGRESS
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // cada vez que se elimine un amigo, se vuelve a cargar la lista de amigos
        parentFragmentManager.setFragmentResultListener(
            "friend_removed",
            viewLifecycleOwner
        ) { _, _ ->
            loadFriends()
        }

        // cada vez que se acepte/rechace una solicitud de amistad, se vuelve a cargar
        parentFragmentManager.setFragmentResultListener(
            "requests_updated",
            viewLifecycleOwner
        ) { _, _ ->
            loadFriends()
            loadNumReceivedRequests()
        }

        // cada vez que el dialogo se cierre, se vuelve a cargar la lista de amigos
        parentFragmentManager.setFragmentResultListener(
            "requests_dialog_closed",
            viewLifecycleOwner
        ) { _, _ ->
            loadFriends()
            loadNumReceivedRequests()
        }

        emptyMessage = view.findViewById(R.id.emptyMessage)
        recyclerFriends = view.findViewById(R.id.recyclerFriends)

        setupRecycler()
        loadFriends()
        loadNumReceivedRequests()
        loadFakeGamesInProgress()
        setupTabs()

        setupListeners()
        selectTab(SocialTab.SOCIAL)
    }

    private fun showUserProfileDialog(friend: FriendSummary) {
        UserProfileDialogFragment.newInstance(friend.id)
            .show(parentFragmentManager, "UserProfileDialog")
    }

    private fun setupRecycler() {
        friendsAdapter = FriendAdapter(emptyList()) { friend -> showUserProfileDialog(friend) }
        binding.recyclerFriends.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = friendsAdapter
        }

        inProgressAdapter = InProgressAdapter(emptyList()) { game -> resumeGame(game) }
        binding.recyclerInProgressGames.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = inProgressAdapter
        }
    }

    private fun loadFriends() {

        repository.getFriends(onSuccess = { friends ->
            val friendsList = friends ?: emptyList()

            if (friendsList.isEmpty()) {
                friendsAdapter.updateFriends(emptyList())
                recyclerFriends.visibility = View.GONE
                emptyMessage.visibility = View.VISIBLE
            } else {
                friendsAdapter.updateFriends(friendsList)
                recyclerFriends.visibility = View.VISIBLE
                emptyMessage.visibility = View.GONE
            }

        }, onError = { errorCode ->
            friendsAdapter.updateFriends(emptyList())
            recyclerFriends.visibility = View.GONE
            emptyMessage.visibility = View.VISIBLE

            when (errorCode) {
                401 -> Toast.makeText(requireContext(), "No autorizado", Toast.LENGTH_SHORT).show()
                500 -> Toast.makeText(requireContext(), "Error del servidor", Toast.LENGTH_SHORT).show()
                null -> Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(requireContext(), "Error: $errorCode", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadNumReceivedRequests() {

        repository.getNumReceivedFriendshipRequests(
            onSuccess = { response ->

                if (response == 0) {
                    binding.txtNumSolicitudes.visibility = View.GONE
                } else {
                    binding.txtNumSolicitudes.visibility = View.VISIBLE
                    binding.txtNumSolicitudes.text = response.toString()
                }

            },
            onError = {
                binding.txtNumSolicitudes.visibility = View.GONE
                Toast.makeText(requireContext(),
                    "Error al cargar el número de solicitudes",
                    Toast.LENGTH_SHORT).show()
            }
        )

    }

    private fun loadFakeGamesInProgress() {
        val fakeMatches = listOf(
            InProgressGameSummary(
                id = "1",
                myTime = "13:00",
                opponentUsername = "Usuario",
                opponentTime = "12:00",
                timeMode = TimeMode.BLITZ,
                boardType = BoardType.ACE
            ),
            InProgressGameSummary(
                id = "2",
                myTime = "14:00",
                opponentUsername = "Usuario",
                opponentTime = "11:00",
                timeMode = TimeMode.EXTENDED,
                boardType = BoardType.SOPHIE
            )
        )

        if (fakeMatches.isEmpty()) {
            binding.emptyInProgressMessage.visibility = View.VISIBLE
            binding.recyclerInProgressGames.visibility = View.GONE
        } else {
            binding.emptyInProgressMessage.visibility = View.GONE
            binding.recyclerInProgressGames.visibility = View.VISIBLE
            inProgressAdapter.updateData(fakeMatches)
        }
    }

    private fun resumeGame(game: InProgressGameSummary) {
        Toast.makeText(requireContext(), "Retomar partida con id ${game.id}", Toast.LENGTH_SHORT).show()
    }

    private fun setupTabs() {
        binding.tabFriends.setOnClickListener { selectTab(SocialTab.SOCIAL) }

        binding.tabPartidas.setOnClickListener { selectTab(SocialTab.IN_PROGRESS) }
    }

    private fun selectTab(tab: SocialTab) {
        when (tab) {
            SocialTab.SOCIAL -> {
                binding.layoutSocialContent.visibility = View.VISIBLE
                binding.btnAddFriend.visibility = View.VISIBLE
                binding.layoutInProgressContent.visibility = View.GONE

                binding.indicatorFriends.visibility = View.VISIBLE
                binding.indicatorMatches.visibility = View.GONE
                binding.txtTabFriends.setTextColor(ContextCompat.getColor(requireContext(), R.color.LCWhite))
                binding.txtTabMatches.setTextColor(ContextCompat.getColor(requireContext(), R.color.S3))
                binding.tabFriends.setBackgroundResource(R.color.S3)
                binding.tabPartidas.setBackgroundResource(R.color.S2)
            }

            SocialTab.IN_PROGRESS -> {
                binding.layoutSocialContent.visibility = View.GONE
                binding.btnAddFriend.visibility = View.GONE
                binding.layoutInProgressContent.visibility = View.VISIBLE

                binding.indicatorFriends.visibility = View.GONE
                binding.indicatorMatches.visibility = View.VISIBLE
                binding.txtTabFriends.setTextColor(ContextCompat.getColor(requireContext(), R.color.S3))
                binding.txtTabMatches.setTextColor(ContextCompat.getColor(requireContext(), R.color.LCWhite))
                binding.tabFriends.setBackgroundResource(R.color.S2)
                binding.tabPartidas.setBackgroundResource(R.color.S3)
            }
        }
    }

    private fun setupListeners() {
        binding.btnAddFriend.setOnClickListener {
            showAddFriendDialog()
        }
        binding.buttonSolicitudes.setOnClickListener {
            RequestsDialogFragment().show(parentFragmentManager, "DialogRequests")
        }
    }

    private fun showAddFriendDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_friend, null)

        val editTextUsername = dialogView.findViewById<EditText>(R.id.editTextFriendUsername)
        val buttonSendFriendRequest = dialogView.findViewById<Button>(R.id.buttonSendFriendRequest)
        val buttonCopyInvitationLink = dialogView.findViewById<Button>(R.id.buttonCopyInvitationLink)
        val buttonCloseDialog = dialogView.findViewById<ImageButton>(R.id.buttonCloseDialog)
        val textInvitationLink = dialogView.findViewById<TextView>(R.id.textInvitationLink)

        val invitationLink = "https://laserchess.com/invite/User"

        textInvitationLink.text = invitationLink

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        textInvitationLink.setOnClickListener {
            copyInvitationLink(invitationLink)
        }

        buttonCopyInvitationLink.setOnClickListener {
            copyInvitationLink(invitationLink)
        }

        buttonSendFriendRequest.setOnClickListener {
            val username = editTextUsername.text.toString().trim()

            when (UsernameValidator.validate(username)) {

                UsernameValidationResult.Valid -> {
                    sendFriendRequest(username)
                    dialog.dismiss()
                }

                UsernameValidationResult.EmptyUsername -> {
                    editTextUsername.error = "El username no puede estar vacío"
                }

                UsernameValidationResult.LongUsername -> {
                    editTextUsername.error = "Máximo ${UsernameValidator.MAX_LENGTH} caracteres"
                }

                UsernameValidationResult.InvalidUsername -> {
                    editTextUsername.error = "El username no puede contener espacios en blanco"
                }

            }
        }

        buttonCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun copyInvitationLink(invitationLink: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE)
                as ClipboardManager
        val clip = ClipData.newPlainText("invitation_link", invitationLink)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(requireContext(), "Enlace copiado", Toast.LENGTH_SHORT).show()
    }

    private fun sendFriendRequest(username : String) {

        repository.addFriend(username = username, onSuccess = {
            Toast.makeText(requireContext(), "Solicitud enviada a $username", Toast.LENGTH_SHORT).show()

            loadFriends()
        }, onError = { errorCode ->
            when (errorCode) {
                400 -> Toast.makeText(requireContext(),
                    "Solicitud inválida", Toast.LENGTH_SHORT).show()
                401 -> Toast.makeText(requireContext(),
                    "No autorizado", Toast.LENGTH_SHORT).show()
                404 -> Toast.makeText(requireContext(),
                    "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                409 -> Toast.makeText(requireContext(),
                    "Ya existe una amistad o solicitud", Toast.LENGTH_SHORT).show()
                null -> Toast.makeText(requireContext(),
                    "Error de conexión", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(requireContext(),
                    "Error: $errorCode", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        loadFriends()
        loadNumReceivedRequests()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
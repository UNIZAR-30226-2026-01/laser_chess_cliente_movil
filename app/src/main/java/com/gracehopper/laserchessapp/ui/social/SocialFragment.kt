package com.gracehopper.laserchessapp.ui

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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.game.BoardType
import com.gracehopper.laserchessapp.data.model.game.InProgressMatchSummary
import com.gracehopper.laserchessapp.data.model.social.FriendSummary
import com.gracehopper.laserchessapp.data.model.user.TimeMode
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.FriendRepository
import com.gracehopper.laserchessapp.databinding.FragmentSocialBinding
import com.gracehopper.laserchessapp.ui.user.UserProfileDialogFragment

class SocialFragment : Fragment() {

    private var _binding: FragmentSocialBinding? = null
    private val binding get() = _binding!!

    private lateinit var friendsAdapter: FriendAdapter
    private lateinit var emptyMessage: TextView
    private lateinit var recyclerFriends : RecyclerView

    private lateinit var inProgressAdapter: InProgressAdapter

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

        emptyMessage = view.findViewById(R.id.emptyMessage)
        recyclerFriends = view.findViewById(R.id.recyclerFriends)

        setupRecycler()
        loadFriends()
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

        inProgressAdapter = InProgressAdapter(emptyList()) { game -> resumeGame(game)}
        binding.recyclerInProgressGames.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = inProgressAdapter
        }
    }

    private fun loadFriends() {
        val repository = FriendRepository(NetworkUtils.getApiService())

        repository.getFriends(onSuccess = { friends ->
            if (friends != null) {
                if (friends.isEmpty()) {
                    emptyMessage.visibility = View.VISIBLE
                } else {
                    friendsAdapter.updateFriends(friends)
                    emptyMessage.visibility = View.GONE
                }
            }
        }, onError = { errorCode ->
            emptyMessage.visibility = View.VISIBLE

            when (errorCode) {
                401 -> Toast.makeText(requireContext(), "No autorizado", Toast.LENGTH_SHORT).show()
                500 -> Toast.makeText(requireContext(), "Error del servidor", Toast.LENGTH_SHORT).show()
                null -> Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(requireContext(), "Error: $errorCode", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadFakeGamesInProgress() {
        val fakeMatches = listOf(
            InProgressMatchSummary(
                id = "1",
                myTime = "13:00",
                opponentUsername = "Usuario",
                opponentTime = "12:00",
                timeMode = TimeMode.BLITZ,
                boardType = BoardType.ACE
            ),
            InProgressMatchSummary(
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

    private fun resumeGame(game: InProgressMatchSummary) {
        Toast.makeText(requireContext(), "Retomar partida con id ${game.id}", Toast.LENGTH_SHORT).show()
    }

    private fun setupTabs() {
        binding.tabSocial.setOnClickListener { selectTab(SocialTab.SOCIAL) }

        binding.tabInProgress.setOnClickListener { selectTab(SocialTab.IN_PROGRESS) }
    }

    private fun selectTab(tab: SocialTab) {
        when (tab) {
            SocialTab.SOCIAL -> {
                binding.layoutSocialContent.visibility = View.VISIBLE
                binding.btnAddFriend.visibility = View.VISIBLE
                binding.layoutInProgressContent.visibility = View.GONE

                binding.tabSocial.setBackgroundResource(R.drawable.bg_tab_selected)
                binding.tabInProgress.setBackgroundResource(R.drawable.bg_tab_unselected)
            }

            SocialTab.IN_PROGRESS -> {
                binding.layoutSocialContent.visibility = View.GONE
                binding.btnAddFriend.visibility = View.GONE
                binding.layoutInProgressContent.visibility = View.VISIBLE

                binding.tabSocial.setBackgroundResource(R.drawable.bg_tab_unselected)
                binding.tabInProgress.setBackgroundResource(R.drawable.bg_tab_selected)
            }
        }
    }

    private fun setupListeners() {
        binding.btnAddFriend.setOnClickListener {
            showAddFriendDialog()
        }
        binding.cardSolicitudes.setOnClickListener {
            showRequestsDialog()
        }
    }

    private fun showAddFriendDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_friend, null)

        val editTextUsername = dialogView.findViewById<EditText>(R.id.editTextFriendUsername)
        val buttonSendFriendRequest = dialogView.findViewById<Button>(R.id.buttonSendFriendRequest)
        val buttonCopyInvitationLink = dialogView.findViewById<Button>(R.id.buttonCopyInvitationLink)
        val buttonCloseDialog = dialogView.findViewById<ImageButton>(R.id.buttonCloseDialog)
        val textInvitationLink = dialogView.findViewById<TextView>(R.id.textInvitationLink)

        val invitationLink = "https://www.reddit.com/r/adventuretime/comments/1g9hv45/i_love_adventure_time_so_much_i_cant_even_express/?tl=pt-br"

        textInvitationLink.text = invitationLink

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        textInvitationLink.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE)
                    as ClipboardManager

            val clip = ClipData.newPlainText("invitation_link", invitationLink)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(requireContext(), "Enlace copiado", Toast.LENGTH_SHORT).show()
        }

        buttonCopyInvitationLink.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE)
                    as ClipboardManager
            val clip = ClipData.newPlainText("invitation_link", invitationLink)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(requireContext(), "Enlace copiado", Toast.LENGTH_SHORT).show()
        }

        buttonSendFriendRequest.setOnClickListener {
            val username = editTextUsername.text.toString().trim()

            if (username.isNotEmpty()) {
                sendFriendRequest(username)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(),
                    "Por favor, introduce un nombre de usuario",
                    Toast.LENGTH_SHORT).show()
            }
        }

        buttonCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun sendFriendRequest(username : String) {
        val repository = FriendRepository(NetworkUtils.getApiService())

        repository.addFriend(username = username, onSuccess = {
            Toast.makeText(requireContext(), "Solicitud enviada a $username", Toast.LENGTH_SHORT).show()

            loadFriends()
        }, onError = { errorCode ->
            when (errorCode) {
                400 -> Toast.makeText(requireContext(), "Solicitud inválida", Toast.LENGTH_SHORT).show()
                401 -> Toast.makeText(requireContext(), "No autorizado", Toast.LENGTH_SHORT).show()
                404 -> Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                409 -> Toast.makeText(requireContext(), "Ya existe una amistad o solicitud", Toast.LENGTH_SHORT).show()
                null -> Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(requireContext(), "Error: $errorCode", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRequestsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_friendship_requests, null)

        val buttonCloseDialog = dialogView.findViewById<ImageButton>(R.id.buttonCloseRequestsDialog)

        val receivedContainer = dialogView.findViewById<LinearLayout>(R.id.layoutReceivedRequestsContainer)
        val receivedTab = dialogView.findViewById<TextView>(R.id.tabReceivedRequests)
        var layoutReceivedContent = dialogView.findViewById<LinearLayout>(R.id.layoutReceivedRequestsContent)

        val sentContainer = dialogView.findViewById<LinearLayout>(R.id.layoutSentRequestsContainer)
        val sentTab = dialogView.findViewById<TextView>(R.id.tabSentRequests)
        val layoutSentContent = dialogView.findViewById<LinearLayout>(R.id.layoutSentRequestsContent)

        val emptyReceived = dialogView.findViewById<TextView>(R.id.textEmptyReceivedRequests)
        val emptySent = dialogView.findViewById<TextView>(R.id.textEmptySentRequests)

        // Datos falsos de momento
        val receivedRequests = listOf("Usuario1", "Usuario2")
        val sentRequests = listOf("Usuario3")

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Limpiar contenedores por seguridad
        receivedContainer.removeAllViews()
        sentContainer.removeAllViews()

        // RECIBIDIAS
        if (receivedRequests.isEmpty()) {
            emptyReceived.visibility = View.VISIBLE
        } else {
            emptyReceived.visibility = View.GONE

            for (username in receivedRequests) {
                val itemView = layoutInflater.inflate(R.layout.item_friendship_request,
                    receivedContainer, false)

                val textUsername = itemView.findViewById<TextView>(R.id.textRequestUsername)
                val buttonAccept = itemView.findViewById<ImageButton>(R.id.buttonAcceptRequest)
                val buttonReject = itemView.findViewById<ImageButton>(R.id.buttonRejectCancelRequest)

                textUsername.text = username
                buttonAccept.visibility = View.VISIBLE

                buttonAccept.setOnClickListener {
                    acceptFriendshipRequest(username)
                    Toast.makeText(requireContext(),
                        "Solicitud aceptada: $username",
                        Toast.LENGTH_SHORT).show()
                }

                buttonReject.setOnClickListener {
                    rejectFriendshipRequest(username)
                    Toast.makeText(requireContext(),
                        "Solicitud rechazada: $username",
                        Toast.LENGTH_SHORT).show()
                }

                receivedContainer.addView(itemView)

            }

        }

        // ENVIADAS
        if (sentRequests.isEmpty()) {
            emptySent.visibility = View.VISIBLE
        } else {
            emptySent.visibility = View.GONE

            for (username in sentRequests) {
                val itemView = layoutInflater.inflate(R.layout.item_friendship_request,
                    sentContainer, false)

                val textUsername = itemView.findViewById<TextView>(R.id.textRequestUsername)
                val buttonAccept = itemView.findViewById<ImageButton>(R.id.buttonAcceptRequest)
                val buttonCancel = itemView.findViewById<ImageButton>(R.id.buttonRejectCancelRequest)

                textUsername.text = username
                buttonAccept.visibility = View.GONE

                buttonCancel.setOnClickListener {
                    cancelSentFriendshipRequest(username)
                    Toast.makeText(requireContext(),
                        "Solicitud cancelada: $username",
                        Toast.LENGTH_SHORT).show()
                }

                sentContainer.addView(itemView)

            }

        }

        // TABS
        fun selectRequestsTab(showReceived: Boolean) {

            if (showReceived) {
                layoutReceivedContent.visibility = View.VISIBLE
                layoutSentContent.visibility = View.GONE

                receivedTab.setBackgroundResource(R.drawable.bg_tab_selected)
                sentTab.setBackgroundResource(R.drawable.bg_tab_unselected)
            } else {
                layoutReceivedContent.visibility = View.GONE
                layoutSentContent.visibility = View.VISIBLE

                receivedTab.setBackgroundResource(R.drawable.bg_tab_unselected)
                sentTab.setBackgroundResource(R.drawable.bg_tab_selected)
            }

        }

        receivedTab.setOnClickListener {
            selectRequestsTab(true)
        }

        sentTab.setOnClickListener {
            selectRequestsTab(false)
        }

        selectRequestsTab(true)

        buttonCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

    }

    private fun acceptFriendshipRequest(username: String) {
        // TODO llamada al backend para aceptar solicitud
    }

    private fun rejectFriendshipRequest(username: String) {
        // TODO llamada al backend para rechazar solicitud
    }

    private fun cancelSentFriendshipRequest(username: String) {
        // TODO llamada al backend para cancelar solicitud
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
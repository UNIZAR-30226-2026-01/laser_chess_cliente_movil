package com.gracehopper.laserchessapp.ui.social

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.FriendRepository
import com.gracehopper.laserchessapp.ui.user.UserProfileDialogFragment
import com.gracehopper.laserchessapp.ui.user.UserProfileDialogMode
import com.gracehopper.laserchessapp.ui.utils.AvatarUtils


class RequestsDialogFragment : DialogFragment() {

    private val repository by lazy {
        FriendRepository(NetworkUtils.getApiService())
    }

    private lateinit var buttonCloseDialog: ImageButton

    private lateinit var receivedContainer: LinearLayout
    private lateinit var receivedTab: TextView
    private lateinit var layoutReceivedContent: LinearLayout
    private lateinit var emptyReceived: TextView

    private lateinit var sentContainer: LinearLayout
    private lateinit var sentTab: TextView
    private lateinit var layoutSentContent: LinearLayout
    private lateinit var emptySent: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialogView = layoutInflater.inflate(R.layout.dialog_friendship_requests, null)

        bindViews(dialogView)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        setupTabs()

        // se vuelve a cargar la lista de solicitudes
        parentFragmentManager.setFragmentResultListener(
            "requests_updated",
            this
        ) { _, _ ->
            loadReceivedRequests()
            loadSentRequests()
        }

        setupListeners(dialog)

        loadReceivedRequests()
        loadSentRequests()

        selectRequestsTab(true)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog

    }

    private fun bindViews(dialogView: View) {

        buttonCloseDialog = dialogView.findViewById(R.id.buttonCloseRequestsDialog)

        receivedContainer = dialogView.findViewById(R.id.layoutReceivedRequestsContainer)
        receivedTab = dialogView.findViewById(R.id.tabReceivedRequests)
        layoutReceivedContent = dialogView.findViewById(R.id.layoutReceivedRequestsContent)

        sentContainer = dialogView.findViewById(R.id.layoutSentRequestsContainer)
        sentTab = dialogView.findViewById(R.id.tabSentRequests)
        layoutSentContent = dialogView.findViewById(R.id.layoutSentRequestsContent)

        emptyReceived = dialogView.findViewById(R.id.textEmptyReceivedRequests)
        emptySent = dialogView.findViewById(R.id.textEmptySentRequests)

    }

    private fun setupTabs() {

        receivedTab.setOnClickListener {
            selectRequestsTab(true)
        }

        sentTab.setOnClickListener {
            selectRequestsTab(false)
        }

    }

    private fun setupListeners(dialog: AlertDialog) {

        buttonCloseDialog.setOnClickListener {
            dialog.dismiss()
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

    private fun loadReceivedRequests() {

        receivedContainer.removeAllViews()

        repository.getReceivedFriendshipRequests(
            onSuccess = { receivedRequests ->
                if (receivedRequests.isEmpty()) {
                    emptyReceived.visibility = View.VISIBLE
                } else {
                    emptyReceived.visibility = View.GONE

                    for (request in receivedRequests) {
                        val itemView = LayoutInflater.from(requireContext()).inflate(
                            R.layout.item_friendship_request,
                            receivedContainer,
                            false
                        )

                        val textUsername = itemView.findViewById<TextView>(R.id.textRequestUsername)
                        val imageAvatar = itemView.findViewById<ImageView>(R.id.imgRequestAvatar)
                        val buttonAccept =
                            itemView.findViewById<ImageButton>(R.id.buttonAcceptRequest)
                        val buttonReject =
                            itemView.findViewById<ImageButton>(R.id.buttonRejectCancelRequest)

                        textUsername.text = request.username
                        imageAvatar.setImageResource(AvatarUtils.getAvatarDrawable(request.avatar))
                        buttonAccept.visibility = View.VISIBLE

                        itemView.setOnClickListener {
                            UserProfileDialogFragment
                                .newInstance(request.id, UserProfileDialogMode.RECEIVED_REQUEST)
                                .show(parentFragmentManager, "UserProfileDialog")
                        }

                        buttonAccept.setOnClickListener {
                            buttonAccept.isEnabled = false
                            buttonReject.isEnabled = false
                            acceptFriendshipRequest(request.username, buttonAccept, buttonReject)
                        }

                        buttonReject.setOnClickListener {
                            buttonAccept.isEnabled = false
                            buttonReject.isEnabled = false
                            rejectFriendshipRequest(request.username, buttonAccept, buttonReject)
                        }

                        receivedContainer.addView(itemView)
                    }
                }
            },
            onError = { _ ->
                emptyReceived.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    "Error al cargar solicitudes recibidas",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun loadSentRequests() {

        sentContainer.removeAllViews()

        repository.getSentFriendshipRequests(

            onSuccess = { sentRequests ->
                if (sentRequests.isEmpty()) {
                    emptySent.visibility = View.VISIBLE
                } else {
                    emptySent.visibility = View.GONE

                    for (request in sentRequests) {
                        val itemView = LayoutInflater.from(requireContext()).inflate(
                            R.layout.item_friendship_request,
                            sentContainer,
                            false
                        )

                        val textUsername = itemView.findViewById<TextView>(R.id.textRequestUsername)
                        val imageAvatar = itemView.findViewById<ImageView>(R.id.imgRequestAvatar)
                        val buttonAccept =
                            itemView.findViewById<ImageButton>(R.id.buttonAcceptRequest)
                        val buttonCancel =
                            itemView.findViewById<ImageButton>(R.id.buttonRejectCancelRequest)

                        textUsername.text = request.username
                        imageAvatar.setImageResource(AvatarUtils.getAvatarDrawable(request.avatar))
                        buttonAccept.visibility = View.GONE

                        itemView.setOnClickListener {
                            UserProfileDialogFragment
                                .newInstance(request.id, UserProfileDialogMode.SENT_REQUEST)
                                .show(parentFragmentManager, "UserProfileDialog")
                        }

                        buttonCancel.setOnClickListener {
                            buttonCancel.isEnabled = false
                            cancelSentFriendshipRequest(request.username, buttonCancel)
                        }

                        sentContainer.addView(itemView)
                    }
                }
            },
            onError = { _ ->
                emptySent.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    "Error al cargar solicitudes enviadas",
                    Toast.LENGTH_SHORT
                ).show()
            }

        )

    }

    private fun acceptFriendshipRequest(
        username: String,
        buttonAccept: ImageButton,
        buttonReject: ImageButton
    ) {

        repository.acceptFriendship(
            username = username,
            onSuccess = {
                if (!isAdded) return@acceptFriendship
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(), "Solicitud aceptada",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadReceivedRequests()
                    parentFragmentManager.setFragmentResult("requests_updated", Bundle())
                }
            },
            onError = { errorCode ->
                if (!isAdded) return@acceptFriendship
                requireActivity().runOnUiThread {
                    buttonAccept.isEnabled = true
                    buttonReject.isEnabled = true
                    Toast.makeText(
                        requireContext(), "Error al aceptar: $errorCode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    }

    private fun rejectFriendshipRequest(
        username: String,
        buttonAccept: ImageButton,
        buttonReject: ImageButton
    ) {

        repository.deleteFriendship(
            username = username,
            onSuccess = {
                if (!isAdded) return@deleteFriendship
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(), "Solicitud rechazada",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadReceivedRequests()
                    parentFragmentManager.setFragmentResult("requests_updated", Bundle())
                }
            },
            onError = { errorCode ->
                if (!isAdded) return@deleteFriendship
                requireActivity().runOnUiThread {
                    buttonAccept.isEnabled = true
                    buttonReject.isEnabled = true
                    Toast.makeText(
                        requireContext(), "Error al rechazar: $errorCode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    }

    private fun cancelSentFriendshipRequest(username: String, buttonCancel: ImageButton) {

        repository.deleteFriendship(
            username = username,
            onSuccess = {
                if (!isAdded) return@deleteFriendship
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(), "Solicitud cancelada",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadSentRequests()
                }
            },
            onError = { errorCode ->
                if (!isAdded) return@deleteFriendship
                requireActivity().runOnUiThread {
                    buttonCancel.isEnabled = true
                    Toast.makeText(
                        requireContext(), "Error al cancelar: $errorCode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        parentFragmentManager.setFragmentResult("requests_dialog_closed", Bundle())
    }


}
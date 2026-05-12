package com.gracehopper.laserchessapp.ui.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.model.ranking.RankingEntry
import com.gracehopper.laserchessapp.data.model.user.TimeMode
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.FriendRepository
import com.gracehopper.laserchessapp.data.repository.RankingRepository
import com.gracehopper.laserchessapp.databinding.FragmentRankingBinding
import com.gracehopper.laserchessapp.ui.user.MyProfileDialogFragment
import com.gracehopper.laserchessapp.ui.user.UserProfileDialogFragment
import com.gracehopper.laserchessapp.ui.utils.ItemUtils

class RankingFragment : Fragment() {

    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

    private val friendRepository by lazy {
        FriendRepository(NetworkUtils.getApiService())
    }

    private val rankingRepository by lazy {
        RankingRepository(NetworkUtils.getApiService())
    }

    private lateinit var rankingAdapter: RankingEntryAdapter

    private val rankingModes = TimeMode.entries
        .filter { it != TimeMode.CUSTOM }

    private var selectedMode: TimeMode = TimeMode.BLITZ

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOwnRankCard()
        setupDropdown()
        setupRecycler()
        loadRanking(selectedMode)
    }

    private fun setupOwnRankCard() {

        context?.let {
            binding.myPosition.cardRankingEntry.setCardBackgroundColor(
                ContextCompat.getColor(it, R.color.S3)
            )
        }

        binding.myPosition.root.setOnClickListener {
            MyProfileDialogFragment().show(
                parentFragmentManager,
                "MyProfileDialog"
            )
        }

        CurrentUserManager.myProfile.observe(viewLifecycleOwner) { profile ->

            profile ?: return@observe

            binding.myPosition.textRankingUsername.text = profile.username

            binding.myPosition.imageRankingAvatar.setImageResource(
                ItemUtils.getItemDrawable(profile.avatar)
            )
        }
    }

    private fun setupDropdown() {
        val lcGreen = ContextCompat.getColor(requireContext(), R.color.LCGreen)

        val selector = binding.includeRankingModeSelector.root
        val txtTitle = selector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgIcon = selector.findViewById<ImageView>(R.id.imgSelectorIcon)

        txtTitle.text = getRankingModeName(selectedMode)
        imgIcon.setImageResource(R.drawable.ic_tiempo)
        imgIcon.setColorFilter(lcGreen)

        selector.setOnClickListener {
            showRankingModeBottomSheet(lcGreen, txtTitle)
        }
    }

    private fun showRankingModeBottomSheet(color: Int, targetView: TextView) {
        val dialog = buildBottomSheet("Modo de ranking", color) { container, dlg ->
            rankingModes.forEach { mode ->
                addButton(container, dlg, getRankingModeName(mode)) {
                    selectedMode = mode
                    targetView.text = getRankingModeName(mode)
                    loadRanking(selectedMode)
                }
            }
        }

        dialog.show()
    }

    private fun buildBottomSheet(
        title: String,
        titleColor: Int,
        fillOptions: (container: LinearLayout, dialog: BottomSheetDialog) -> Unit
    ): BottomSheetDialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.TemaBottomSheetTransparente)
        val dialogView = layoutInflater.inflate(R.layout.dialog_selector_desplegable, null)

        dialogView.findViewById<TextView>(R.id.txtDialogTitle).apply {
            text = title
            setTextColor(titleColor)
        }

        val container = dialogView.findViewById<LinearLayout>(R.id.layoutOptionsContainer)
        fillOptions(container, dialog)

        dialog.setContentView(dialogView)
        return dialog
    }

    private fun addButton(
        container: LinearLayout,
        dialog: BottomSheetDialog,
        label: String,
        onClick: () -> Unit
    ) {
        val button = com.google.android.material.button.MaterialButton(requireContext()).apply {
            text = label
            setTextColor(ContextCompat.getColor(context, R.color.LCWhite))
            backgroundTintList = ContextCompat.getColorStateList(context, R.color.S2)
            cornerRadius = 36

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }

            setOnClickListener {
                onClick()
                dialog.dismiss()
            }
        }

        container.addView(button)
    }

    private fun getRankingModeName(mode: TimeMode): String {
        return when (mode) {
            TimeMode.BLITZ -> "Blitz"
            TimeMode.RAPID -> "Rapid"
            TimeMode.CLASSIC -> "Classic"
            TimeMode.EXTENDED -> "Extended"
            else -> mode.name
        }
    }

    private fun setupRecycler() {

        rankingAdapter = RankingEntryAdapter(
            entries = emptyList(),
            onUserClicked = { entry ->
                openUserFromRanking(entry)
            }
        )

        binding.recyclerRanking.apply {
            adapter = rankingAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun loadRanking(mode: TimeMode) {

        rankingRepository.getTopRankUsers(
            eloType = mode,
            onSuccess = { ranking ->
                requireActivity().runOnUiThread {

                    val myId = CurrentUserManager.getMyCurrentId()

                    val myEntry = ranking.find { it.id == myId }

                    myEntry?.let { entry ->

                        binding.myPosition.textRankingPosition.text =
                            entry.position.toString()

                        binding.myPosition.textRankingElo.text =
                            entry.elo.toString()
                    }

                    rankingAdapter.updateData(ranking)
                }
            },
            onError = { code ->
                requireActivity().runOnUiThread {
                    rankingAdapter.updateData(emptyList())

                    val message = when(code) {
                        null -> "Error de conexión al cargar el ranking"
                        else -> "No se puedo cargar el ranking"
                    }

                    Toast.makeText(requireContext(),
                        message, Toast.LENGTH_SHORT).show()
                }
            }
        )

    }

    fun openUserFromRanking(entry: RankingEntry) {

        val myId = CurrentUserManager.getMyCurrentId()

        if (entry.id == myId) {
            MyProfileDialogFragment().show(
                parentFragmentManager,
                "MyProfileDialog"
            )
            return
        }

        friendRepository.getFriendshipStatus(
            myId = myId,
            username = entry.username,
            onSuccess = { status ->
                requireActivity().runOnUiThread {
                    UserProfileDialogFragment.newInstance(
                        friendId = entry.id,
                        mode = status
                    ).show(parentFragmentManager, "UserProfileDialog")
                }
            },
            onError = {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(),
                        "No se pudo cargar el estado de amistad",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    }

    override fun onResume() {
        super.onResume()
        loadRanking(selectedMode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
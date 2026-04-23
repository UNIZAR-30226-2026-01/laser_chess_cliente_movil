package com.gracehopper.laserchessapp.ui.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracehopper.laserchessapp.data.model.ranking.RankingEntry
import com.gracehopper.laserchessapp.data.model.user.TimeMode
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.RankingRepository
import com.gracehopper.laserchessapp.databinding.FragmentRankingBinding

class RankingFragment : Fragment() {

    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

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
    ): View? {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdown()
        setupRecycler()
        loadRanking(selectedMode)
    }

    private fun setupDropdown() {

        val dropdownAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            rankingModes.map { it.name }
        )

        binding.dropdownRankingMode.setAdapter(dropdownAdapter)
        binding.dropdownRankingMode.setText(rankingModes.first().name, false)

        binding.dropdownRankingMode.setOnItemClickListener { _, _, position, _ ->
            selectedMode = rankingModes[position]
            loadRanking(selectedMode)
        }
    }

    private fun setupRecycler() {

        rankingAdapter = RankingEntryAdapter(emptyList())

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

    override fun onResume() {
        super.onResume()
        loadRanking(selectedMode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
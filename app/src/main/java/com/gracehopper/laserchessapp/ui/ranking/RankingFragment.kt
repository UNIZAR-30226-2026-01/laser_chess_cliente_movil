package com.gracehopper.laserchessapp.ui.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.ranking.RankingEntry
import com.gracehopper.laserchessapp.data.model.user.TimeMode
import com.gracehopper.laserchessapp.databinding.FragmentRankingBinding

class RankingFragment : Fragment() {

    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

    private lateinit var rankingAdapter: RankingEntryAdapter

    private val rankingModes = TimeMode.entries
        .filter { it != TimeMode.CUSTOM }

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
        loadFakeRanking(TimeMode.BLITZ)
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
            val selectedMode = rankingModes[position]
            loadFakeRanking(selectedMode)
        }
    }

    private fun setupRecycler() {

        rankingAdapter = RankingEntryAdapter(emptyList())

        binding.recyclerRanking.apply {
            adapter = rankingAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun loadFakeRanking(mode: TimeMode) {

        val fakeRanking = when (mode) {
            TimeMode.BLITZ -> buildFakeRanking(1800)
            TimeMode.RAPID -> buildFakeRanking(1700)
            TimeMode.CLASSIC -> buildFakeRanking(1600)
            TimeMode.EXTENDED -> buildFakeRanking(1500)
            else -> emptyList()
        }

        rankingAdapter.updateData(fakeRanking)

    }

    private fun buildFakeRanking(baseElo: Int): List<RankingEntry> {
        return listOf(
            RankingEntry(
                id = 1L,
                username = "Username",
                avatar = 1,
                elo = baseElo + 120,
                position = 1
            ),
            RankingEntry(
                id = 2L,
                username = "Username",
                avatar = 2,
                elo = baseElo + 90,
                position = 2
            ),
            RankingEntry(
                id = 3L,
                username = "Username",
                avatar = 3,
                elo = baseElo + 70,
                position = 3
            ),
            RankingEntry(
                id = 4L,
                username = "Username",
                avatar = 4,
                elo = baseElo + 40,
                position = 4
            ),
            RankingEntry(
                id = 5L,
                username = "MiUsername",
                avatar = 1,
                elo = baseElo + 15,
                position = 5,
                isCurrentUser = true
            ),
            RankingEntry(
                id = 6L,
                username = "Username",
                avatar = 2,
                elo = baseElo,
                position = 6
            ),
            RankingEntry(
                id = 7L,
                username = "Username",
                avatar = 3,
                elo = baseElo - 25,
                position = 7
            ),
            RankingEntry(
                id = 8L,
                username = "Username",
                avatar = 4,
                elo = baseElo - 40,
                position = 8
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
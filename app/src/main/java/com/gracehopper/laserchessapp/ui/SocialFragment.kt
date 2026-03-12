package com.gracehopper.laserchessapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.social.FriendSummary
import com.gracehopper.laserchessapp.databinding.FragmentSocialBinding

class SocialFragment : Fragment() {

    private var _binding: FragmentSocialBinding? = null
    private val binding get() = _binding!!

    private lateinit var friendsAdapter: FriendAdapter

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

        setupRecycler()
        loadFakeData()
        setupTabs()
        selectTab(SocialTab.SOCIAL)
    }

    private fun setupRecycler() {
        friendsAdapter = FriendAdapter(emptyList())
        binding.recyclerFriends.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = friendsAdapter
        }
    }

    private fun loadFakeData() {
        val fakeFriends = listOf(
            FriendSummary("1", "User1", R.drawable.ic_avatar, 1234),
            FriendSummary("2", "User2", R.drawable.ic_avatar, 5678),
            FriendSummary("3", "User3", R.drawable.ic_avatar, 9012)
        )

        friendsAdapter.updateFriends(fakeFriends)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
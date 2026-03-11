package com.gracehopper.laserchessapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.data.model.social.FriendSummary
import com.gracehopper.laserchessapp.databinding.ItemFriendBinding

class FriendAdapter (private var friends: List<FriendSummary>)
            : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    class FriendViewHolder(val binding: ItemFriendBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]

        with(holder.binding) {
            holder.binding.txtFriendUsername.text = friend.username
            holder.binding.imgFriendAvatar.setImageResource(friend.avatar)
            holder.binding.txtFriendXp.text = "${friend.xp} xp"
        }
    }

    override fun getItemCount(): Int = friends.size

    fun updateFriends(newFriends: List<FriendSummary>) {
        friends = newFriends
        notifyDataSetChanged() // para avisar a RecyclerView
    }

}
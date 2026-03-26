package com.gracehopper.laserchessapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.data.model.social.FriendSummary
import com.gracehopper.laserchessapp.databinding.ItemFriendBinding
import com.gracehopper.laserchessapp.R

class FriendAdapter (private var friends: List<FriendSummary>,
            private val onFriendClick: (FriendSummary) -> Unit)
            : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    class FriendViewHolder(val binding: ItemFriendBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]

        holder.binding.apply {
            txtFriendUsername.text = friend.username
            imgFriendAvatar.setImageResource(getAvatarDrawable(friend.avatar))
            txtFriendXp.text = "${friend.level} xp"
        }

        holder.itemView.setOnClickListener { onFriendClick(friend) }
    }

    override fun getItemCount(): Int = friends.size

    fun updateFriends(newFriends: List<FriendSummary>) {
        friends = newFriends
        notifyDataSetChanged() // para avisar a RecyclerView
    }

    fun getAvatarDrawable(avatarId: Int): Int {
        return when (avatarId) {
            1 -> R.drawable.avatar_1
            2 -> R.drawable.avatar_2
            3 -> R.drawable.avatar_3
            4 -> R.drawable.avatar_4
            else -> R.drawable.ic_avatar
        }
    }

}
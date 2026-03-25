package com.gracehopper.laserchessapp.data.model.social

import com.google.gson.annotations.SerializedName

data class FriendshipStatusResponse (
    @SerializedName("sender_id") val senderId: Long,
    @SerializedName("receiver_id") val receiverId: Long,
    @SerializedName("sender_accept") val senderAccept: Boolean,
    @SerializedName("receiver_accept") val receiverAccept: Boolean
)
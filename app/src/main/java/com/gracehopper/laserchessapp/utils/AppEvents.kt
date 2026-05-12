package com.gracehopper.laserchessapp.utils

import kotlinx.coroutines.flow.MutableSharedFlow

object AppEvents {

    val challengesUpdated = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val friendRequestReceived = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val newFriendshipReceived = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

}
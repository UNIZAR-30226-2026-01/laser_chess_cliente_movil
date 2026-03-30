package com.gracehopper.laserchessapp.data.remote.websocket

data class ClientSocketMessage(
    val Type: String,
    val Content: String
)

data class ServerSocketMessage(
    val Type: String,
    val Content: String,
    val Extra: String?
)



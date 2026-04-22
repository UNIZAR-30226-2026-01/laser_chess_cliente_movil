package com.gracehopper.laserchessapp.data.model.game

import com.google.gson.annotations.SerializedName

enum class GameMessageType {

    @SerializedName("Move")
    MOVE,
    @SerializedName("GetState")
    GET_STATE,
    @SerializedName("GetInitialState")
    GET_INITIAL_STATE,
    @SerializedName("Pause")
    PAUSE,

    @SerializedName("State")
    STATE,
    @SerializedName("MatchStart")
    MATCH_START,
    @SerializedName("InitialState")
    INITIAL_STATE,
    @SerializedName("PauseRequest")
    PAUSE_REQUEST,
    @SerializedName("PauseReject")
    PAUSE_REJECT,
    @SerializedName("Paused")
    PAUSED,
    @SerializedName("End")
    END,
    @SerializedName("Error")
    ERROR,
    @SerializedName("EOC")
    EOC,

    @SerializedName("Disconnection")
    DISCONNECTION,
    @SerializedName("Reconnection")
    RECONNECTION

}
package com.example.agoratesting.utils

import com.example.agoratesting.data.AccountInfo
import com.example.agoratesting.data.MeetingInfo
import com.example.agoratesting.data.MeetingRoom
import io.agora.chat.ChatMessage

object SDKManager{
    var APP_KEY :String = "611000680#1167414"
    var APP_ID :String = "a2ab9666ddc44391b17dfbb4a27a2a10"
}


var dummyMeetingList = listOf(
    MeetingInfo(
        "Meeting 1",
        "Tuesday",
        "09/12/2023",
        MeetingRoom(
            "007eJxTYNjwbbX0TElml1M6qxgUUo8oCXdbGlg7/rr9NqjkmdHFn3cUGBKNEpMszczMUlKSTUyMLQ2TDM1T0pKSTBKNzIFShgbB66RTGwIZGZwm+TIwQiGIz8lQklpckpxaUVrAwAAAsL8gvg==",
            "testcexup",
            "219929636831233"),
        "personID"),
)

object TempMeeting{
    var TempChatRoom = mutableListOf<ChatMessage>()

    var ListMember = mutableListOf<AccountInfo>()

    var isVolumeOff = false

    var isCameraOff = false

    var isMicOff = false

}
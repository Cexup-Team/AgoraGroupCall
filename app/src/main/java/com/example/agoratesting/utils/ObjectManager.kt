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
            "007eJxTYDihMsFw+f/ZqzX/1f3hkj6jyfD6cLf4kTfrK9en7V0THvVBgSHRKDHJ0szMLCUl2cTE2NIwydA8JS0pySTRyBwoZWgQc144tSGQkeHZm0wGRigE8TkZSlKLS5JTK0oLGBgAuG0kXA==",
            "testcexup",
            "219929636831233"),
        "personID"),
)

object TempMeeting{
    var TempChatRoom = mutableListOf<ChatMessage>()

    var ListMember = mutableListOf<AccountInfo>()
}
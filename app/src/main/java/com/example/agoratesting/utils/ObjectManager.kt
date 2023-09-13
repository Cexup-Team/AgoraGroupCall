package com.example.agoratesting.utils

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
            "007eJxTYGCu1wlmunTyZV5Fpcv32Bez7KeVxF21NRY1vTqPUzZfsVqBIdEoMcnSzMwsJSXZxMTY0jDJ0DwlLSnJJNHIHChlaGAWwpjaEMjI4JcQx8jIAIEgPidDSWpxSXJqRWkBAwMARe0eug==",
            "testcexup",
            "219929636831233"),
        "personID"),
)

var TempChatRoom = mutableListOf<ChatMessage>()
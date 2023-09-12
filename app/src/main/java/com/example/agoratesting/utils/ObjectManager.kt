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
            "007eJxTYLDv8f/N2qf04Eun8Pkmxh22l3j8733IC12qd7ZfQqi/+q4CQ6JRYpKlmZlZSkqyiYmxpWGSoXlKWlKSSaKROVDK0ODpnf8pDYGMDCYmRxgYoRDE52QoSS0uSU6tKC1gYAAAG4Uisg==",
            "testcexup",
            "219929636831233"),
        "personID"),
    MeetingInfo(
        "Meeting 2",
        "Tuesday",
        "09/12/2023",
        MeetingRoom(
            "007eJxTYLDv8f/N2qf04Eun8Pkmxh22l3j8733IC12qd7ZfQqi/+q4CQ6JRYpKlmZlZSkqyiYmxpWGSoXlKWlKSSaKROVDK0ODpnf8pDYGMDCYmRxgYoRDE52QoSS0uSU6tKC1gYAAAG4Uisg==",
            "testcexup",
            "219929636831233"),
        "personID"),
    MeetingInfo(
        "Meeting 3",
        "Tuesday",
        "09/12/2023",
        MeetingRoom(
            "007eJxTYLDv8f/N2qf04Eun8Pkmxh22l3j8733IC12qd7ZfQqi/+q4CQ6JRYpKlmZlZSkqyiYmxpWGSoXlKWlKSSaKROVDK0ODpnf8pDYGMDCYmRxgYoRDE52QoSS0uSU6tKC1gYAAAG4Uisg==",
            "testcexup",
            "219929636831233"),
        "personID"),
)

var TempChatRoom = mutableListOf<ChatMessage>()
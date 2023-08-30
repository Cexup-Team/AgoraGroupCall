package com.example.agoratesting.utils

import com.example.agoratesting.data.Chat


object chatManager {
    var APP_KEY :String = "611000680#1167414"
    var ROOM_ID :String = ""

    var targetID :String = ""
    var isChatRoom : Boolean = false
}

object videoManager {
    var APP_ID :String = "a2ab9666ddc44391b17dfbb4a27a2a10"
    var channelName :String = "testcexup"
    var rtcToken :String = ""
}

object userManager {
    var username :String = ""
    var userToken :String = ""
}

var TempChat : MutableMap<String, Chat>? = null

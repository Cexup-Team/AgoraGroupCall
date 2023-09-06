package com.example.agoratesting.utils

import io.agora.chat.ChatMessage


object chatManager {
    var APP_KEY :String = "611000680#1167414"
    var ROOM_ID :String = "219929636831233"

    var targetID :String = ""
    var isChatRoom : Boolean = false
}

object videoManager {
    var APP_ID :String = "a2ab9666ddc44391b17dfbb4a27a2a10"
    var channelName :String = "testcexup"
    var rtcToken :String = "007eJxTYIhaF5Cw439mubHw6le9dTqqsyt59ErWbLrn2Mws/Dy1w0eBIdEoMcnSzMwsJSXZxMTY0jDJ0DwlLSnJJNHIHChlaLDr7feUhkBGhouPDzAxMkAgiM/JUJJaXJKcWlFawMAAAO4oItk="
}

object userManager {
    //tester
    var username :String = "tester"
    var userToken :String = "007eJxTYPh6mufKzF36/DnKeuL6NnMaMnpzb53YpuJZy7VSmkE894QCQ6JRYpKlmZlZSkqyiYmxpWGSoXlKWlKSSaKROVDK0OD22+8pDYGMDPtsNZgZGVgZGIEQxFdhMLNISzS0SDTQNTIwN9E1NExN1U0yS0rUNUmxNDJPMU1LMjRMAwDKlSZK"
    //tester2
//    var username :String = "tester2"
//    var userToken :String = "007eJxTYDBqXdjxk72onvnJJ+98rd2r3U7OTgxcXbUnzu/MXaHoo68VGBKNEpMszczMUlKSTUyMLQ2TDM1T0pKSTBKNzIFShgbP3n5PaQhkZJD8UMTKyMDKwAiEIL4KQ0piirGBobmBrpGRgYGuoWFqqm6ScSKQMDUzM0g0SzQytEgDADvMKb0="
}

var TempChatRoom = mutableListOf<ChatMessage>()
package com.example.agoratesting.utils

import com.example.agoratesting.data.Chat


object chatManager {
    var APP_KEY :String = "611000680#1167414"
    var ROOM_ID :String = "219929636831233"

    var targetID :String = ""
    var isChatRoom : Boolean = false
}

object videoManager {
    var APP_ID :String = "a2ab9666ddc44391b17dfbb4a27a2a10"
    var channelName :String = "testcexup"
    var rtcToken :String = "007eJxTYLB6kX1g6YXFRyeuE/Fhtln3fK+wo9MGx0tS90rCvQ6YzGpVYEg0SkyyNDMzS0lJNjExtjRMMjRPSUtKMkk0MgdKGRrwH3qX0hDIyGBaY8LMyACBID4nQ0lqcUlyakVpAQMDABs7IcE="
}

object userManager {
    var username :String = "tester"
    var userToken :String = "007eJxTYJC2lOFr/psX7qbr/CWxzK2h0jPPjF0lpXbzlikRgTsC2BUYEo0SkyzNzMxSUpJNTIwtDZMMzVPSkpJMEo3MgVKGBl6H3qU0BDIyRDFaMzEysDIwAiGIr8JgZpGWaGiRaKBrZGBuomtomJqqm2SWlKhrkmJpZJ5impZkaJgGAOohI9w="
}

var TempChat : MutableMap<String, Chat>? = null

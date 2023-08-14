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
    var rtcToken :String = "007eJxTYGDUyRJRnHj98+uXZhf2/Jk3U+tqIC+7qMxKg6iKeenc9k0KDIlGiUmWZmZmKSnJJibGloZJhuYpaUlJJolG5kApQ4PsvTdTGgIZGbK/HGFmZIBAEJ+ToSS1uCQ5taK0gIEBAKlPIW8="
}

object userManager {
    var username :String = "tester2"
    var userToken :String = "007eJxTYFg5tdB+i36nPJvX0riXE13/CDx/FRK98tv/8iK5dVMX9ixVYEg0SkyyNDMzS0lJNjExtjRMMjRPSUtKMkk0MgdKGRpYb7+Z0hDIyJAm/pmJkYGVgREIQXwVhpTEFGMDQ3MDXSMjAwNdQ8PUVN0k40QgYWpmZpBolmhkaJEGAAllKN4="
}

var TempChat : MutableMap<String, Chat>? = null

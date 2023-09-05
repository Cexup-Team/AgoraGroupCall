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
    var rtcToken :String = "007eJxTYPgv1b/wXeuCyu879n0JSLry8/h+tlkzd57UjfrxofyC00MfBYZEo8QkSzMzs5SUZBMTY0vDJEPzlLSkJJNEI3OglKFBxvpvKQ2BjAwsmxMZGKEQxOdkKEktLklOrSgtYGAAAJzGJoc="
}

object userManager {
    //tester
    var username :String = "tester"
    var userToken :String = "007eJxTYHhS86D3/vego9/SVKtbIt7cOVRgsSZ84eGDMW+DDn866DhNgSHRKDHJ0szMLCUl2cTE2NIwydA8JS0pySTRyBwoZWjQu/5bSkMgI8OGfVtZGRlYGRiBEMRXYTCzSEs0tEg00DUyMDfRNTRMTdVNMktK1DVJsTQyTzFNSzI0TAMAt1gtSQ=="
    //tester2
//    var username :String = "tester2"
//    var userToken :String = "007eJxTYBA57rXxyzrPp5s2zE4yusyewLhx1onLMY8Lnae2/XaJLNykwJBolJhkaWZmlpKSbGJibGmYZGiekpaUZJJoZA6UMjSYu/5bSkMgI4PXkWBWRgZWBkYgBPFVGFISU4wNDM0NdI2MDAx0DQ1TU3WTjBOBhKmZmUGiWaKRoUUaAFqsKZk="
}

var TempChat : MutableMap<String, Chat>? = null

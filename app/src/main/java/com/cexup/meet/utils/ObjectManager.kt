package com.cexup.meet.utils

import com.cexup.meet.data.AccountInfo
import com.cexup.meet.data.ChatRTM
import com.cexup.meet.data.MeetingInfo
import java.util.UUID

object SDKManager{
    var APP_ID :String = "a2ab9666ddc44391b17dfbb4a27a2a10"
}

object TempMeeting{
    var rtcToken = ""
    var rtmToken = ""


    var username = ""
    var channelName = ""

    var TempChatRoom = mutableListOf<ChatRTM>()

    var ListMember = mutableListOf<AccountInfo>()

    var isVolumeOff = false

    var isCameraOff = false

    var isMicOff = false

}
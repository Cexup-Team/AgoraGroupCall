package com.cexup.meet.data

import io.agora.rtm.RtmMessage


data class ChatRTM(
    var username : String,
    var channelName : String,
    var message : RtmMessage
)

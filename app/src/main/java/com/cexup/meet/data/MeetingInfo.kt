package com.cexup.meet.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeetingInfo(
    var rtcToken : String?,
    var rtmToken : String?,
    var username : String?,
    var channelName : String?,
):Parcelable
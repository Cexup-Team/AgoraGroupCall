package com.example.agoratesting.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeetingInfo(
    var title : String?,
    var hari : String?,
    var tanggal : String?,
    var roomMeeting : MeetingRoom?,
    var personID: String?,

):Parcelable

@Parcelize
data class MeetingRoom(
    var rtcToken : String?,
    var channelName : String?,
    var roomID : String?,
):Parcelable
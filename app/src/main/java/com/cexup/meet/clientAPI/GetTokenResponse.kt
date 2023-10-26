package com.cexup.meet.clientAPI

import com.google.gson.annotations.SerializedName

data class GetTokenResponse(

	@field:SerializedName("rtmToken")
	val rtmToken: String,

	@field:SerializedName("rtcToken")
	val rtcToken: String
)

package com.cexup.meet.clientAPI

import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("rte/{channelName}/{role}/{tokenType}/{rtcuid}/{rtmuid}/")
    fun getToken(
        @Path("channelName") channelName : String,
        @Path("role") role : String,
        @Path("tokenType") tokenType : String, // uid or userAccount
        @Path("rtcuid") rtcuid : String, //(type varies String/Int based on tokenType)
        @Path("rtmuid") rtmuid : String,
        @Query("expiry") expiry : Int, //in second

    ) : Call<GetTokenResponse>
}
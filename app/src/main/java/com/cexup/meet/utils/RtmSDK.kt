package com.cexup.meet.utils

import android.content.Context
import android.util.Log
import com.cexup.meet.data.ChatRTM
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmChannel
import io.agora.rtm.RtmChannelAttribute
import io.agora.rtm.RtmChannelListener
import io.agora.rtm.RtmChannelMember
import io.agora.rtm.RtmClient
import io.agora.rtm.RtmClientListener
import io.agora.rtm.RtmFileMessage
import io.agora.rtm.RtmImageMessage
import io.agora.rtm.RtmMediaOperationProgress
import io.agora.rtm.RtmMessage


object RtmSDK {
    var rtmClient : RtmClient? = null
    var rtmChannel : RtmChannel? = null

    fun initSDK(
        mContext : Context,
        appID : String,
        channel: String
    ){
        try {
            rtmClient = RtmClient.createInstance(mContext, appID, getRtmClientListener(channel))
            rtmClient?.login(TempMeeting.rtmToken, TempMeeting.username, object : ResultCallback<Void>{
                override fun onSuccess(p0: Void?) {
//                    Nothing
                }

                override fun onFailure(p0: ErrorInfo?) {
                    Log.e("Exception", p0?.errorDescription?:"")
                    throw Exception(p0?.errorDescription)
                }

            })
        } catch (e: Exception){
            Log.e("Exception", e.message?: "")
            throw Exception(e)
        }
    }

    fun joinChannel(channel : String){
        rtmChannel = rtmClient?.createChannel(channel, getRtmChannelListener())
        rtmChannel?.join(object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                //nothing
            }

            override fun onFailure(p0: ErrorInfo?) {
                throw Exception(p0?.errorDescription)
            }

        })
    }

    private fun getRtmChannelListener(): RtmChannelListener {
        val listener = object :RtmChannelListener{
            override fun onMemberCountUpdated(p0: Int) {
                //Something
            }

            override fun onAttributesUpdated(p0: MutableList<RtmChannelAttribute>?) {
                //Something
            }

            override fun onMessageReceived(p0: RtmMessage?, p1: RtmChannelMember?) {
                if (p0 != null && p1 != null){
                    val message = ChatRTM(p1.userId, p1.channelId, p0)
                    TempMeeting.TempChatRoom.add(message)
                }
            }

            override fun onImageMessageReceived(p0: RtmImageMessage?, p1: RtmChannelMember?) {
                //Something
            }

            override fun onFileMessageReceived(p0: RtmFileMessage?, p1: RtmChannelMember?) {
                //Something
            }

            override fun onMemberJoined(p0: RtmChannelMember?) {
                //Something
            }

            override fun onMemberLeft(p0: RtmChannelMember?) {
                TODO("Not yet implemented")
            }

        }
        return listener
    }

    private fun getRtmClientListener(channel: String) : RtmClientListener{
        val listener = object : RtmClientListener{
            override fun onConnectionStateChanged(p0: Int, p1: Int) {
                if (p0 == 3 && p1 == 2){
                    joinChannel(channel)
                }
            }

            override fun onMessageReceived(p0: RtmMessage?, p1: String?) {
                //Something
            }

            override fun onImageMessageReceivedFromPeer(p0: RtmImageMessage?, p1: String?) {
                //Something
            }

            override fun onFileMessageReceivedFromPeer(p0: RtmFileMessage?, p1: String?) {
                //Something
            }

            override fun onMediaUploadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {
                //Something
            }

            override fun onMediaDownloadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {
                //Something
            }

            override fun onTokenExpired() {
                //Something
            }

            override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {
                //Something
            }

        }
        return listener
    }
}
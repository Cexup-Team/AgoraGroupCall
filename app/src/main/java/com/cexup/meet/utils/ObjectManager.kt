package com.cexup.meet.utils

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.cexup.meet.GlobalSettings
import com.cexup.meet.data.AccountInfo
import com.cexup.meet.data.ChatRTM
import com.cexup.meet.data.MeetingInfo
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
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


object TempMeeting{
    var TempChatRoom = mutableListOf<ChatRTM>()
    var ListMember = mutableListOf<AccountInfo>()

    var isVolumeOff = false
    var isCameraOff = false
    var isMicOff = false
}
object SDKManager{
    var APP_ID :String = "a2ab9666ddc44391b17dfbb4a27a2a10"

    var rtcEngine : RtcEngine? = null
    var rtmClient : RtmClient? = null
    var rtmChannel : RtmChannel? = null

    fun logoutRTM(){
        rtmClient?.logout(object : ResultCallback<Void>{
            override fun onSuccess(p0: Void?) {
                rtmClient?.release()
            }

            override fun onFailure(p0: ErrorInfo?) {
                Log.e("RTM Logout", "OnFailure : ${p0?.errorDescription}")
            }

        })
    }
    fun initRtmSDK(
        mActivity: Activity,
        meetingDetails : MeetingInfo) {
        try {
            rtmClient = RtmClient.createInstance(mActivity.baseContext, APP_ID, getRtmClientListener(meetingDetails.channelName.toString(), mActivity))
            rtmClient?.login(meetingDetails.rtmToken, meetingDetails.username, object : ResultCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    //VOID
                }

                override fun onFailure(p0: ErrorInfo?) {
                    Log.e("Exception", p0?.errorDescription?:"")
                    throw Exception(p0?.errorDescription)
                }

            })
        } catch (e: Exception){
            Log.e("Exception", e.message?: "")
        }
    }

    private fun getRtmClientListener(channel: String, mActivity: Activity): RtmClientListener {
        val listener = object : RtmClientListener{
            override fun onConnectionStateChanged(p0: Int, p1: Int) {
                if (p0 == 3 && p1 == 2){
                    Log.w("onConnectionStateChanged", "Connection State : $p0, Reason : $p1")
                    joinChannel(channel, mActivity)
                } else if (p1 == 6){
                    rtmClient?.release()
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

    private fun joinChannel(channel: String, mActivity: Activity) {
        Log.w("RtmChannel", "Join RtmChannel")
        rtmChannel = rtmClient?.createChannel(channel, getRtmChannelListener(mActivity))
        rtmChannel?.join(object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                Log.w("RtmChannel", "Join RtmChannel Success")
            }

            override fun onFailure(p0: ErrorInfo?) {
                throw Exception(p0?.errorDescription)
            }

        })

    }

    private fun getRtmChannelListener(mActivity: Activity): RtmChannelListener {
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

                    try {
                        mActivity.runOnUiThread {
                            Toast.makeText(mActivity.baseContext, "New Message From :  ${p1.userId}", Toast.LENGTH_SHORT).show()                        }
                    } catch (e:Exception){
                        Log.e("onMessageReceived : Toast", "Exception : $e")
                    }


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
                //Something
            }

        }

        return listener
    }

    fun initRtcSDK(mActivity: Activity, mRtcEventHandler : IRtcEngineEventHandler) {
        Log.w("RtcSDK", "Try Init SDK")
        val globalSettings = GlobalSettings()
        try {
            val config = RtcEngineConfig()
            config.mContext = mActivity.baseContext
            config.mAppId = APP_ID
            config.mEventHandler = mRtcEventHandler
            config.mChannelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
            config.mAudioScenario =
                Constants.AudioScenario.getValue(Constants.AudioScenario.DEFAULT)
            config.mAreaCode = globalSettings.getAreaCode()
            rtcEngine = RtcEngine.create(config)
            rtcEngine?.setParameters(
                "{"
                        + "\"rtc.report_app_scenario\":"
                        + "{"
                        + "\"appScenario\":" + 100 + ","
                        + "\"serviceType\":" + 11 + ","
                        + "\"appVersion\":\"" + RtcEngine.getSdkVersion() + "\""
                        + "}"
                        + "}"
            )
            rtcEngine?.setLocalAccessPoint(globalSettings.getPrivateCloudConfig())

        } catch (e: Exception) {
            Log.e("RtcSDK", "Exception : $e")
        }
    }
}
package com.example.agoratesting.utils

import android.content.Context
import android.util.Log
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.proxy.LocalAccessPointConfiguration

object VidSDK {
    var rtcEngine : RtcEngine? = null

    fun initSDK(
        mContext : Context,
        appId: String,
        mRtcEventHandler : IRtcEngineEventHandler,
        mAreaCode : Int,
        mCloudConfig : LocalAccessPointConfiguration
    ){
        try {
            val config = RtcEngineConfig()
            config.mContext = mContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            config.mChannelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
            config.mAudioScenario =
                Constants.AudioScenario.getValue(Constants.AudioScenario.DEFAULT)
            config.mAreaCode = mAreaCode ?: 0
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
            rtcEngine?.setLocalAccessPoint(mCloudConfig)

        } catch (e: Exception) {
            Log.e("Vid SDK", "Exception : ${e.message}")
        }
    }
}
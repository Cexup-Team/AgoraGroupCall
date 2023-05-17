package com.example.agoratesting

import android.util.Log
import io.agora.rtc2.Constants
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.proxy.LocalAccessPointConfiguration
import io.agora.rtc2.video.VideoEncoderConfiguration
import io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE
import io.agora.rtc2.video.VideoEncoderConfiguration.ORIENTATION_MODE
import io.agora.rtc2.video.VideoEncoderConfiguration.VD_640x360

class GlobalSettings {
    private var videoEncodingDimension: String? = null
    private var videoEncodingFrameRate: String? = null
    private var videoEncodingOrientation: String? = null
    private var areaCodeStr = "GLOBAL"

    // private cloud config
    var privateCloudIp = ""
    var privateCloudLogReportEnable = false
    var privateCloudLogServerDomain = ""
    var privateCloudLogServerPort = 80
    var privateCloudLogServerPath = ""
    var privateCloudUseHttps = false
    // var privateCloudIp = "10.62.0.85"
    // var privateCloudLogReportEnable = true
    // var privateCloudLogServerDomain = "10.72.0.29"
    // var privateCloudLogServerPort = 442
    // var privateCloudLogServerPath = "/kafka/log/upload/v1"
    // var privateCloudUseHttps = true

    fun getVideoEncodingDimension(): String {
        return videoEncodingDimension ?: "VD_960x540"
    }

    fun getPrivateCloudConfig(): LocalAccessPointConfiguration {
        val config = LocalAccessPointConfiguration()
        if (privateCloudIp.isEmpty()) {
            return config
        }
        config.ipList = ArrayList()
        config.ipList.add(privateCloudIp)
        config.domainList = ArrayList()
        config.mode = Constants.LOCAL_RPOXY_LOCAL_ONLY
        if (privateCloudLogReportEnable) {
            val advancedConfig = LocalAccessPointConfiguration.AdvancedConfigInfo()
            val logUploadServer = LocalAccessPointConfiguration.LogUploadServerInfo()
            logUploadServer.serverDomain = privateCloudLogServerDomain
            logUploadServer.serverPort = privateCloudLogServerPort
            logUploadServer.serverPath = privateCloudLogServerPath
            logUploadServer.serverHttps = privateCloudUseHttps

            advancedConfig.logUploadServer = logUploadServer
            config.advancedConfig = advancedConfig
        }
        return config
    }

    fun getVideoEncodingDimensionObject(): VideoEncoderConfiguration.VideoDimensions {
        return videoEncodingDimension?.let {
            try {
                val tmp = VideoEncoderConfiguration::class.java.getDeclaredField(it)
                tmp.isAccessible = true
                tmp.get(null) as VideoEncoderConfiguration.VideoDimensions
            } catch (e: NoSuchFieldException) {
                Log.e("Field", "Can not find field $it")
                VD_640x360
            } catch (e: IllegalAccessException) {
                Log.e("Field", "Could not access field $it")
                VD_640x360
            }
        } ?: VD_640x360
    }

    fun setVideoEncodingDimension(videoEncodingDimension: String) {
        this.videoEncodingDimension = videoEncodingDimension
    }

    fun getVideoEncodingFrameRate(): String {
        return videoEncodingFrameRate ?: FRAME_RATE.FRAME_RATE_FPS_15.name
    }

    fun setVideoEncodingFrameRate(videoEncodingFrameRate: String) {
        this.videoEncodingFrameRate = videoEncodingFrameRate
    }

    fun getVideoEncodingOrientation(): String {
        return videoEncodingOrientation ?: ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE.name
    }

    fun setVideoEncodingOrientation(videoEncodingOrientation: String) {
        this.videoEncodingOrientation = videoEncodingOrientation
    }

    fun getAreaCodeStr(): String {
        return areaCodeStr
    }

    fun setAreaCodeStr(areaCodeStr: String) {
        this.areaCodeStr = areaCodeStr
    }

    fun getAreaCode(): Int {
        return when (areaCodeStr) {
            "CN" -> RtcEngineConfig.AreaCode.AREA_CODE_CN
            "NA" -> RtcEngineConfig.AreaCode.AREA_CODE_NA
            "EU" -> RtcEngineConfig.AreaCode.AREA_CODE_EU
            "AS" -> RtcEngineConfig.AreaCode.AREA_CODE_AS
            "JP" -> RtcEngineConfig.AreaCode.AREA_CODE_JP
            "IN" -> RtcEngineConfig.AreaCode.AREA_CODE_IN
            else -> RtcEngineConfig.AreaCode.AREA_CODE_GLOB
        }
    }
}

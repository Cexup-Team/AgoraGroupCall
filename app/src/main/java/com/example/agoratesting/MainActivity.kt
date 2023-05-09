package com.example.agoratesting

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas


class MainActivity : AppCompatActivity() {
    private val ReqID = 22
    private val REQUESTED_PERMISSION =
        arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA)
    private val appId: String = "4300d680db99440ea74c274f82d30c59"
    private val channelName: String = "CexupTesting"
    private val token: String =
        "007eJxTYBBWyHaKvNER9mL19vfVCifaBL8lyLlX+pjfFfab/7e7broCg4mxgUGKmYVBSpKlpYmJQWqiuUmykblJmoVRirFBsqllwp6IlIZARoY5G3ewMDJAIIjPw+CcWlFaEJJaXJKZl87AAAAjUSI+"
    private val uid: Int = 0
    private var isJoined = false
    private var agoraEngine: RtcEngine? = null
    private var localSurfaceView: SurfaceView? = null
    private var remoteSurfaceView: SurfaceView? = null
    private var containerRemoteView: FrameLayout? = null
    private var containerLocalView:FrameLayout? = null


    private fun checkSelfPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                REQUESTED_PERMISSION[0]
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                REQUESTED_PERMISSION[1]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    fun showMessage(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRemoteVideo(uid: Int) {
        val density = resources.displayMetrics.density
        val wDp = (320 * density).toInt()
        val hDp = (240 * density).toInt()
        containerRemoteView = findViewById(R.id.remote_video)
        val layoutParams = RelativeLayout.LayoutParams(
            wDp,
            hDp
        )
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView?.setZOrderMediaOverlay(true)
        containerRemoteView?.addView(remoteSurfaceView)
        agoraEngine?.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
        remoteSurfaceView?.visibility = View.VISIBLE
        layoutParams.addRule(RelativeLayout.BELOW,R.id.local_video)
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        containerRemoteView?.layoutParams = layoutParams
        containerRemoteView?.visibility = View.VISIBLE
    }

    private fun setupLocalVideo() {
        containerLocalView = findViewById(R.id.local_video)
        localSurfaceView = SurfaceView(baseContext)
        containerLocalView?.addView(localSurfaceView)
        agoraEngine?.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                0
            )
        )
    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            showMessage("Remote user joined $uid")
            runOnUiThread { setupRemoteVideo(uid) }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            isJoined = true
            showMessage("Joined Channel $channel")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            showMessage("Remote User Offline $uid $reason")
            runOnUiThread { remoteSurfaceView?.visibility = View.GONE }
        }
    }

    private fun setupVideoSdkEngine() {
        try {
            val config: RtcEngineConfig = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine?.enableVideo()
        } catch (e: Exception) {
            showMessage(e.toString())
        }
    }

    private fun joinChannel(view: View) {
        if (checkSelfPermission()) {
            val options: ChannelMediaOptions = ChannelMediaOptions()
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setupLocalVideo()
            localSurfaceView?.visibility = View.VISIBLE
            agoraEngine?.startPreview()
            agoraEngine?.joinChannel(token, channelName, uid, options)
        } else {
            Toast.makeText(applicationContext, "Permission was not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun leaveChannel(view: View) {
        if (!isJoined) {
            showMessage("Join a channel first")
        } else {
            agoraEngine?.leaveChannel()
            showMessage("You left the channel")
            remoteSurfaceView?.let { it.visibility = View.GONE }
            localSurfaceView?.let { it.visibility = View.GONE }
            containerRemoteView?.let { it.visibility = View.GONE }
            isJoined = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val joinButton: Button = findViewById(R.id.joinButton)
        val leaveButton: Button = findViewById(R.id.leaveButton)

        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSION, ReqID)
        }
        setupVideoSdkEngine()

        joinButton.setOnClickListener {
            joinChannel(it)
        }

        leaveButton.setOnClickListener {
            leaveChannel(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine?.stopPreview()
        agoraEngine?.leaveChannel()

        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }
}
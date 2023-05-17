package com.example.agoratesting

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.DisplayMetrics
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agoratesting.databinding.ActivityMainBinding
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.ScreenCaptureParameters
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE
import io.agora.rtc2.video.VideoEncoderConfiguration.ORIENTATION_MODE


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val ReqID = 22
    private val REQUESTED_PERMISSION =
        arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA)
    private val appId: String = "4300d680db99440ea74c274f82d30c59"
    private val channelName: String = "CexupTesting"
    private val token: String =
        "007eJxTYLjc+O7Vb7NpPprfLj/VE/i/z/SxAvdv0QDZ4xtX+QWZGnxVYDAxNjBIMbMwSEmytDQxMUhNNDdJNjI3SbMwSjE2SDa1bMtISWkIZGRI+lnHxMgAgSA+D4NzakVpQUhqcUlmXjoDAwDNjCOI"
    private var rtcEngine: RtcEngine? = null
    private lateinit var handler: Handler
    private var surfaceViews = mutableListOf<SurfaceView>()
    private lateinit var adapter: TestAdapter
    private val uidLocal = 0
    private val uidShareScreen = 1
    private lateinit var fgService: Intent
    private val screenCaptureParameters = ScreenCaptureParameters()
    private var isShareScreen = false


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

    private fun startShareScreenPreview() {
        val surfaceView = SurfaceView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        surfaceView.tag = uidShareScreen
        binding.screenSharing.addView(surfaceView)
        rtcEngine?.setupLocalVideo(
            VideoCanvas(
                surfaceView,
                Constants.RENDER_MODE_FIT,
                Constants.VIDEO_MIRROR_MODE_DISABLED,
                Constants.VIDEO_SOURCE_SCREEN_PRIMARY,
                uidShareScreen
            )
        )
        rtcEngine?.startPreview(Constants.VideoSourceType.VIDEO_SOURCE_SCREEN_PRIMARY)
        binding.screenSharing.visibility = View.VISIBLE
    }

    private fun stopShareScreenPreview() {
        binding.screenSharing.removeAllViews()
        rtcEngine?.setupLocalVideo(
            VideoCanvas(
                null,
                Constants.RENDER_MODE_FIT,
                Constants.VIDEO_MIRROR_MODE_DISABLED,
                Constants.VIDEO_SOURCE_SCREEN_PRIMARY,
                1
            )
        )
        rtcEngine?.stopPreview(Constants.VideoSourceType.VIDEO_SOURCE_SCREEN_PRIMARY)
        binding.screenSharing.visibility = View.GONE
    }

    private fun startShareScreen() {
        if(!isShareScreen){
            rtcEngine?.setParameters("{\"che.video.mobile_1080p\":true")
            rtcEngine?.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
            rtcEngine?.setVideoEncoderConfiguration(VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
            ))
            rtcEngine?.setDefaultAudioRoutetoSpeakerphone(true)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                this.startForegroundService(fgService)
            }

            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            screenCaptureParameters.captureAudio = true
            screenCaptureParameters.captureVideo = true
            screenCaptureParameters.videoCaptureParameters.width = 720
            screenCaptureParameters.videoCaptureParameters.height= (720*1.0f/displayMetrics.widthPixels * displayMetrics.heightPixels).toInt()
            screenCaptureParameters.videoCaptureParameters.framerate = 50
            rtcEngine?.startScreenCapture(screenCaptureParameters)

            startShareScreenPreview()

            val options = ChannelMediaOptions()
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            options.autoSubscribeAudio = true
            options.autoSubscribeVideo = true
            options.publishCameraTrack = false
            options.publishMicrophoneTrack = false
            options.publishScreenCaptureVideo = true
            options.publishScreenCaptureAudio = true
            rtcEngine?.updateChannelMediaOptions(options)
        }else{
            rtcEngine?.stopScreenCapture()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                stopService(fgService)
            }
            isShareScreen = false
            stopShareScreenPreview()
        }

    }

    private fun setupLocalVideo() {
        val surfaceView = SurfaceView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        surfaceView.tag = 0
        surfaceViews.add(surfaceView)
        rtcEngine?.setupLocalVideo(
            VideoCanvas(
                surfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                uidLocal
            )
        )
        adapter.submitList(surfaceViews)
        adapter.notifyItemChanged(surfaceViews.indexOf(adapter.getItemByTag(uidLocal)))
    }


    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread { showMessage("Remote user joined $uid") }
            handler.post {
                val surfaceView = SurfaceView(baseContext)
                surfaceView.setZOrderMediaOverlay(true)
                surfaceView.tag = uid
                surfaceViews.add(surfaceView)
                rtcEngine?.setupRemoteVideo(
                    VideoCanvas(
                        surfaceView,
                        VideoCanvas.RENDER_MODE_HIDDEN,
                        uid
                    )
                )
                adapter.submitList(surfaceViews)
                adapter.notifyItemChanged(adapter.currentList.size - 1)
            }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            runOnUiThread { showMessage("Joined Channel $channel") }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread { showMessage("Remote User Offline $uid $reason") }
            handler.post {
                rtcEngine?.setupRemoteVideo(VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid))
                val position = surfaceViews.indexOf(adapter.getItemByTag(uid))
                surfaceViews.remove(adapter.getItemByTag(uid))
                adapter.notifyItemRemoved(position)
            }
        }
    }

    class MediaProjectFgService : Service() {
        override fun onBind(p0: Intent?): IBinder? = null

        override fun onCreate() {
            super.onCreate()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                createNotificationChannel()
            }
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
            START_NOT_STICKY

        override fun onDestroy() {
            super.onDestroy()
            stopForeground(STOP_FOREGROUND_REMOVE)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun createNotificationChannel() {
            val name: CharSequence = "Cexup Testing"
            val description = "Notice that we are trying to capture the screen!!"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channelId = "cexup_channel_mediaproject"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            val notifyId = 1
            val notification = NotificationCompat.Builder(this, channelId)
                .setContentText(name)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setChannelId(channelId)
                .setWhen(System.currentTimeMillis())
                .build()
            startForeground(notifyId, notification)
        }
    }

    private fun joinChannel() {
        if (checkSelfPermission()) {
            val options: ChannelMediaOptions = ChannelMediaOptions()
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setupLocalVideo()
            rtcEngine?.startPreview()
            rtcEngine?.joinChannel(token, channelName, uidLocal, options)
        } else {
            Toast.makeText(applicationContext, "Permission was not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fgService = Intent(this, MediaProjectFgService::class.java)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handler = Handler(Looper.getMainLooper())

        adapter = TestAdapter()

        val layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
        binding.videosRecycleView.layoutManager = layoutManager

        binding.videosRecycleView.adapter = adapter

        try {
            val config: RtcEngineConfig = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            config.mChannelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
            config.mAudioScenario =
                Constants.AudioScenario.getValue(Constants.AudioScenario.DEFAULT)
            config.mAreaCode =
                (this.application as MainApplication).getGlobalSettings()?.getAreaCode() ?: 0
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
            rtcEngine?.setLocalAccessPoint(
                (this.application as MainApplication).getGlobalSettings()?.getPrivateCloudConfig()
            )
            rtcEngine?.enableVideo()
        } catch (e: Exception) {
            runOnUiThread { showMessage(e.message.toString()) }
        }

        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSION, ReqID)
        }

        binding.btnJoin.setOnClickListener {
            joinChannel()
        }

        binding.btnShare.setOnClickListener {
            startShareScreen()
        }

        binding.btnLeave.setOnClickListener {
            rtcEngine?.stopPreview()
            rtcEngine?.leaveChannel()
            rtcEngine?.setupLocalVideo(VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uidLocal))
            rtcEngine?.setupRemoteVideo(VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN))
            surfaceViews.clear()
            adapter.notifyDataSetChanged()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        handler.post(RtcEngine::destroy)
        handler.removeCallbacksAndMessages(null)
        rtcEngine?.stopPreview()
        rtcEngine?.leaveChannel()
        rtcEngine?.setupLocalVideo(VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uidLocal))
        rtcEngine?.setupRemoteVideo(VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN))
        surfaceViews.clear()

        Thread {
            RtcEngine.destroy()
        }.start()
    }
}
package com.example.agoratesting.uiActivity.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PictureInPictureParams
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Rational
import android.util.TypedValue
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import com.example.agoratesting.MainApplication
import com.example.agoratesting.R
import com.example.agoratesting.uiActivity.VideoAdapter
import com.example.agoratesting.databinding.ActivityMainBinding
import com.example.agoratesting.uiActivity.chat.ChatActivity
import com.example.agoratesting.utils.VidSDK
import com.example.agoratesting.utils.videoManager
import io.agora.base.internal.BuildConfig
import io.agora.chat.ChatClient
import io.agora.chat.ChatMessage
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.ScreenCaptureParameters
import io.agora.rtc2.video.ImageTrackOptions
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE
import io.agora.rtc2.video.VideoEncoderConfiguration.ORIENTATION_MODE


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val REQUESTED_PERMISSION =
        arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA)
    private val appId: String = videoManager.APP_ID
    private val channelName: String = videoManager.channelName
    private val token: String = videoManager.rtcToken
    private var rtcEngine: RtcEngine? = null
    private lateinit var handler: Handler
    private var surfaceViews = mutableListOf<SurfaceView>()
    private lateinit var adapter: VideoAdapter
    private val uidLocal = 0
    private val uidShareScreen = 1
    private lateinit var fgService: Intent
    private val screenCaptureParameters = ScreenCaptureParameters()
    private lateinit var viewModel: MainViewModel
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var isShareScreen : Boolean = false

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
            viewModel.ChangeSharingState(true)
        }else{
            rtcEngine?.stopScreenCapture()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                stopService(fgService)
            }
            viewModel.ChangeSharingState(false)
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

        binding.member.text = "${surfaceViews.size}"
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

                binding.member.text = "${surfaceViews.size}"
                binding.videosRecycleView.adapter = adapter
                if(adapter.itemCount >= 2){
                    binding.videosRecycleView.layoutManager = gridLayoutManager
                }
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

                binding.member.text = "${surfaceViews.size}"
                binding.videosRecycleView.adapter = adapter
                if(adapter.itemCount == 2){
                    for (i in 1 until adapter.itemCount){
                        val holder = binding.videosRecycleView.findViewHolderForAdapterPosition(i)
                        if (holder != null){
                            holder.itemView.layoutParams.width = LayoutParams.MATCH_PARENT
                        }
                    }
                } else{
                    for (i in 1 until adapter.itemCount){
                        val holder = binding.videosRecycleView.findViewHolderForAdapterPosition(i)
                        if (holder != null){
                            holder.itemView.layoutParams.width = LayoutParams.MATCH_PARENT
                            holder.itemView.layoutParams.height = LayoutParams.MATCH_PARENT
                        }
                    }
                    binding.videosRecycleView.layoutManager = linearLayoutManager
                }
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
            binding.btnJoin.visibility = View.GONE
            binding.btnLeave.visibility = View.VISIBLE
            val options= ChannelMediaOptions()
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setupLocalVideo()
            rtcEngine?.startPreview()
            rtcEngine?.joinChannel(token, channelName, uidLocal, options)
        } else {
            Toast.makeText(applicationContext, "Permission was not granted", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }

    private fun getURLforResource(ResID : Int) : String{
        //Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId).toString()
        val uri = "android.resource://" + applicationContext.packageName + "/" + ResID
        return Uri.parse(uri).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fgService = Intent(this, MediaProjectFgService::class.java)
        }
        handler = Handler(Looper.getMainLooper())

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        gridLayoutManager = GridLayoutManager(this, 2, LinearLayoutManager.HORIZONTAL, false)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.videosRecycleView.layoutManager = linearLayoutManager
        adapter = VideoAdapter()

        binding.videosRecycleView.adapter = adapter

        viewModel.isScreenSharing.observe(this){isSharing ->
            isShareScreen = isSharing
            if (isSharing){
                binding.videosRecycleView.layoutManager = linearLayoutManager
            } else{
                binding.videosRecycleView.layoutManager = gridLayoutManager
            }
        }

        initVidSDK()
        joinChannel()

        ChatClient.getInstance().chatManager().addMessageListener {messages ->
            var unreadMSG = 0
            if( messages != null){
                for (msg in messages){
                    if (msg.chatType == ChatMessage.ChatType.ChatRoom){
                        unreadMSG += 1
                    }
                }
            }
            viewModel.UnreadMark(unreadMSG)
        }
//        change icon based on unread
//        viewModel.unreadMSG.observe(this){
//            if (it > 0){
//                binding.btnChat.setImageResource(R.drawable.ic_chat_unread)
//            } else{
//                binding.btnChat.setImageResource(R.drawable.ic_chat)
//            }
//        }

        binding.btnVolume.setOnClickListener {
            if (it.tag == "ic_volume"){

                rtcEngine?.muteAllRemoteAudioStreams(true)

                binding.btnVolume.setImageResource(R.drawable.ic_volume_off)
                binding.btnVolume.tag = "ic_volume_off"

            } else if (it.tag == "ic_volume_off"){

                rtcEngine?.muteAllRemoteAudioStreams(false)

                binding.btnVolume.setImageResource(R.drawable.ic_volume)
                binding.btnVolume.tag = "ic_volume"
            }
        }

        binding.btnVidcam.setOnClickListener {
            if (it.tag == "ic_videocam"){
                rtcEngine?.enableLocalVideo(false)

                binding.btnVidcam.setImageResource(R.drawable.ic_videocam_off)
                binding.btnVidcam.tag = "ic_videocam_off"
            } else if (it.tag == "ic_videocam_off"){

                rtcEngine?.enableLocalVideo(true)

//                rtcEngine?.enableVideoImageSource(true, ImageTrackOptions("url/path", 30))

                binding.btnVidcam.setImageResource(R.drawable.ic_videocam)
                binding.btnVidcam.tag = "ic_videocam"

            }
        }

        binding.btnMic.setOnClickListener {
            if (it.tag == "ic_mic"){

                rtcEngine?.enableLocalAudio(false)
                binding.btnMic.setImageResource(R.drawable.ic_mic_off)
                binding.btnMic.tag = "ic_mic_off"

            } else if (it.tag == "ic_mic_off"){

                rtcEngine?.enableLocalAudio(true)
                binding.btnMic.setImageResource(R.drawable.ic_mic)
                binding.btnMic.tag = "ic_mic"
            }
        }


        binding.btnMenu.setOnClickListener {
            if (binding.optionMenu.isVisible){
                binding.optionMenu.isVisible = false
            } else{
                binding.optionMenu.isVisible = true
            }
        }

        binding.btnShareScreen.setOnClickListener {
            startShareScreen()
        }

        binding.btnSetting.setOnClickListener {
            val moveIntent = Intent(this, SettingActivity::class.java)
            moveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(moveIntent)
        }
        binding.btnLeave.setOnClickListener {
            rtcEngine?.stopPreview()
            rtcEngine?.leaveChannel()
            rtcEngine?.setupLocalVideo(VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uidLocal))
            rtcEngine?.setupRemoteVideo(VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN))
            binding.btnLeave.visibility = View.GONE
            binding.btnJoin.visibility = View.VISIBLE
            surfaceViews.clear()
            adapter.notifyDataSetChanged()
            finish()
        }

        binding.btnChat.setOnClickListener {
            ChatClient.getInstance().chatManager().markAllConversationsAsRead()
            viewModel.UnreadMark(0)

            val moveIntent = Intent(this, ChatActivity::class.java)
            moveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(moveIntent)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                usePIP()
//            }
        }

    }


    private fun initVidSDK() {
        val mAreaCode = (this.application as MainApplication).getGlobalSettings()?.getAreaCode() ?: 0
        val mCloudConfig = (this.application as MainApplication).getGlobalSettings()?.getPrivateCloudConfig()

        try {
            VidSDK.initSDK(baseContext, appId, mRtcEventHandler, mAreaCode, mCloudConfig!!)
            rtcEngine = VidSDK.rtcEngine
            rtcEngine?.enableVideo()

        } catch (e: Exception) {
            runOnUiThread { showMessage(e.message.toString()) }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUserLeaveHint() {
        if (binding.btnLeave.isVisible){
            usePIP()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun usePIP() {
        val visibleRect = Rect()
        val ratio =
            if(binding.screenSharing.isVisible){
                Rational(binding.screenSharing.height/2, binding.screenSharing.width/2)
            }else{
                Rational(binding.videosRecycleView.width/2, binding.videosRecycleView.width/2)
            }
        val pipParams = PictureInPictureParams.Builder()
            .setAspectRatio(ratio)
            .setSourceRectHint(visibleRect)
            .build()
        enterPictureInPictureMode(pipParams)
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        val horizontal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 21.58f, resources.displayMetrics).toInt()
        val vertical = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34.52f, resources.displayMetrics).toInt()

        if (isInPictureInPictureMode){
            if(binding.screenSharing.isVisible){
                binding.videosRecycleView.visibility = View.GONE
            }
            binding.topAppBar.visibility = View.GONE
            binding.botAppBar.visibility = View.GONE
            binding.activityMain.setPadding(0)
        } else{
            binding.videosRecycleView.visibility = View.VISIBLE
            binding.topAppBar.visibility = View.VISIBLE
            binding.botAppBar.visibility = View.VISIBLE
            binding.activityMain.setPadding(horizontal, vertical, horizontal, vertical)
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

        ChatClient.getInstance().logout(true)
        Thread {
            RtcEngine.destroy()
        }.start()

        Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}
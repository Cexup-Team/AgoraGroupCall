package com.example.agoratesting.uiActivity.main

import android.app.PictureInPictureParams
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.util.TypedValue
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import com.example.agoratesting.MainApplication
import com.example.agoratesting.R
import com.example.agoratesting.data.AccountInfo
import com.example.agoratesting.uiActivity.VideoAdapter
import com.example.agoratesting.databinding.ActivityMainBinding
import com.example.agoratesting.uiActivity.chat.ChatActivity
import com.example.agoratesting.utils.MediaProjectFgService
import com.example.agoratesting.utils.VidSDK
import com.example.agoratesting.utils.chatManager
import com.example.agoratesting.utils.mChatListener
import com.example.agoratesting.utils.userManager
import com.example.agoratesting.utils.videoManager
import io.agora.chat.ChatClient
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.ScreenCaptureParameters
import io.agora.rtc2.UserInfo
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE
import io.agora.rtc2.video.VideoEncoderConfiguration.ORIENTATION_MODE
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val REQUESTED_PERMISSION =
        arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA)
    private val appId: String = videoManager.APP_ID
    private val channelName: String = videoManager.channelName
    private val token: String = videoManager.rtcToken
    private var rtcEngine: RtcEngine? = null
    private lateinit var handler: Handler
    private var listMember = mutableListOf<AccountInfo>()
    private lateinit var adapterVideo: VideoAdapter
    private var uidLocal : Int = 0
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
                uidShareScreen
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
        surfaceView.tag = uidLocal
        rtcEngine?.setupLocalVideo(
            VideoCanvas(
                surfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                uidLocal
            )
        )

        val userAccount = AccountInfo(uidLocal, "Local User", surfaceView, false)
        listMember.add(userAccount)

        adapterVideo.submitList(listMember)
        adapterVideo.notifyItemChanged(listMember.indexOf(adapterVideo.getItemByTag(uidLocal)))

        binding.member.text = "${listMember.size}"
    }


    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread { showMessage("Remote user joined $uid") }
            handler.post {
                val surfaceView = SurfaceView(baseContext)
                surfaceView.setZOrderMediaOverlay(true)
                surfaceView.tag = uid
                rtcEngine?.setupRemoteVideo(
                    VideoCanvas(
                        surfaceView,
                        VideoCanvas.RENDER_MODE_HIDDEN,
                        uid
                    )
                )

                val userAccount = AccountInfo(uid, "Remote User $uid", surfaceView,false)
                listMember.add(userAccount)

                adapterVideo.submitList(listMember)
                adapterVideo.notifyItemChanged(adapterVideo.currentList.size - 1)

                binding.member.text = "${listMember.size}"
                if(adapterVideo.itemCount >= 2){
                    binding.videosRecycleView.layoutManager = gridLayoutManager
                }
            }
        }

        override fun onLocalUserRegistered(uid: Int, userAccount: String?) {
            super.onLocalUserRegistered(uid, userAccount)
            if (userAccount != null) {
                Timer().schedule(object : TimerTask(){
                    override fun run() {
                        listMember.find { it.uid == uid }?.username = userAccount
                    }
                }, 1000)
            }
        }

        override fun onUserInfoUpdated(uid: Int, userInfo: UserInfo?) {
            super.onUserInfoUpdated(uid, userInfo)
            if (userInfo != null) {
                Timer().schedule(object : TimerTask(){
                    override fun run() {
                        listMember.find { it.uid == uid }?.username = userInfo.userAccount
                    }
                }, 1000)
            }
        }

        override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed)
            if (reason == 5){
                listMember.find { it.uid == uid }?.offCam = true
            } else if (reason == 6){
                listMember.find { it.uid == uid }?.offCam = false
            }
            runOnUiThread { adapterVideo.notifyDataSetChanged()}
        }
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            runOnUiThread { showMessage("Joined Channel $channel") }
            uidLocal = uid
            runOnUiThread { setupLocalVideo() }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread { showMessage("Remote User Offline $uid $reason") }
            handler.post {
                rtcEngine?.setupRemoteVideo(VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid))
                val position = listMember.indexOf(adapterVideo.getItemByTag(uid))
                listMember.remove(adapterVideo.getItemByTag(uid))
                adapterVideo.notifyItemRemoved(position)

                binding.member.text = "${listMember.size}"
                if(adapterVideo.itemCount == 2){
                    for (i in 1 until adapterVideo.itemCount){
                        val holder = binding.videosRecycleView.findViewHolderForAdapterPosition(i)
                        if (holder != null){
                            holder.itemView.layoutParams.width = LayoutParams.MATCH_PARENT
                        }
                    }
                } else{
                    for (i in 1 until adapterVideo.itemCount){
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

    private fun joinChannel() {
        if (checkSelfPermission()) {
            val options= ChannelMediaOptions()
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            rtcEngine?.startPreview()
            rtcEngine?.joinChannelWithUserAccount(token ,channelName, userManager.username, options)
        } else {
            Toast.makeText(applicationContext, "Permission was not granted", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }

    private fun checkUnread(){
        val unread = ChatClient.getInstance().chatManager().getConversation(chatManager.ROOM_ID).unreadMsgCount
        if ( unread > 0){
            binding.tvUnreadCount.isVisible = true
            binding.tvUnreadCount.text = unread.toString()
        }else{
            binding.tvUnreadCount.isVisible = false
        }
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
        adapterVideo = VideoAdapter()

        initVidSDK()
        joinChannel()

        binding.videosRecycleView.adapter = adapterVideo

        viewModel.isScreenSharing.observe(this){isSharing ->
            isShareScreen = isSharing
            if (isSharing){
                binding.videosRecycleView.layoutManager = linearLayoutManager
            } else{
                binding.videosRecycleView.layoutManager = gridLayoutManager
            }
        }

        ChatClient.getInstance().chatManager().addMessageListener(mChatListener)
        ChatClient.getInstance().chatManager().addMessageListener {
            runOnUiThread { checkUnread() }
        }

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
                rtcEngine?.muteLocalVideoStream(true)
                rtcEngine?.stopPreview()

                binding.btnVidcam.setImageResource(R.drawable.ic_videocam_off)
                binding.btnVidcam.tag = "ic_videocam_off"

                listMember[0].offCam = true
                adapterVideo.notifyDataSetChanged()
            } else if (it.tag == "ic_videocam_off"){

                rtcEngine?.muteLocalVideoStream(false)
                rtcEngine?.startPreview()

                binding.btnVidcam.setImageResource(R.drawable.ic_videocam)
                binding.btnVidcam.tag = "ic_videocam"

                listMember[0].offCam = false
                adapterVideo.notifyDataSetChanged()
            }
        }

        binding.btnMic.setOnClickListener {
            if (it.tag == "ic_mic"){

                rtcEngine?.muteLocalAudioStream(true)

                binding.btnMic.setImageResource(R.drawable.ic_mic_off)
                binding.btnMic.tag = "ic_mic_off"

            } else if (it.tag == "ic_mic_off"){

                rtcEngine?.muteLocalAudioStream(false)

                binding.btnMic.setImageResource(R.drawable.ic_mic)
                binding.btnMic.tag = "ic_mic"
            }
        }


        binding.btnMenu.setOnClickListener {
            binding.optionMenu.isVisible = !binding.optionMenu.isVisible
        }

        binding.btnShareScreen.setOnClickListener {
            startShareScreen()
        }

        binding.btnAudioMode.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Audio Mode")
            builder.setMessage("this mode will turn off all video from remote user")

            builder.setNeutralButton("Dismiss"){dialog : DialogInterface, which: Int ->
                dialog.cancel()
            }

            builder.setNegativeButton("Turn Off"){dialog : DialogInterface, which: Int ->
                try {
                    rtcEngine?.muteAllRemoteVideoStreams(false)
                    dialog.cancel()
                    adapterVideo.notifyDataSetChanged()
                }catch (e: Exception){
                    Log.w("muteAllRemoteVideoStreams", "muteAllRemoteVideoStreams(false)")
                }
            }

            builder.setPositiveButton("Turn On"){dialog : DialogInterface, which : Int ->
                try {
                    rtcEngine?.muteAllRemoteVideoStreams(true)
                    dialog.cancel()
                    adapterVideo.notifyDataSetChanged()
                }catch (e: Exception){
                    Log.w("muteAllRemoteVideoStreams", "muteAllRemoteVideoStreams(true)")
                }
            }

            val alert = builder.create()
            alert.show()
        }

        binding.btnSetting.setOnClickListener {
            val moveIntent = Intent(this, SettingActivity::class.java)
            moveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(moveIntent)
        }
        binding.btnLeave.setOnClickListener {
            finish()
        }

        binding.btnChat.setOnClickListener {
            try {
                ChatClient.getInstance().chatManager().getConversation(chatManager.ROOM_ID).markAllMessagesAsRead()
            } catch (e: Exception){
                Log.e("Exception", e.message.toString())
            }

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


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        try {
            checkUnread()
        } catch (e:Exception){
            Log.e("Exception", e.message.toString())
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
        listMember.clear()

        ChatClient.getInstance().chatManager().removeMessageListener(mChatListener)
        ChatClient.getInstance().chatManager().removeMessageListener {
            runOnUiThread { checkUnread() }
        }

        ChatClient.getInstance().logout(true)
        Thread {
            RtcEngine.destroy()
        }.start()

        Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}
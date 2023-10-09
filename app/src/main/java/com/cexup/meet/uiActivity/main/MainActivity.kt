package com.cexup.meet.uiActivity.main

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
import com.cexup.meet.MainApplication
import com.cexup.meet.R
import com.cexup.meet.data.AccountInfo
import com.cexup.meet.data.MeetingRoom
import com.cexup.meet.databinding.ActivityMainBinding
import com.cexup.meet.uiActivity.VideoAdapter
import com.cexup.meet.uiActivity.chat.ChatActivity
import com.cexup.meet.utils.DataPreference
import com.cexup.meet.utils.MediaProjectFgService
import com.cexup.meet.utils.SDKManager
import com.cexup.meet.utils.TempMeeting
import com.cexup.meet.utils.VidSDK
import io.agora.ValueCallBack
import io.agora.chat.ChatClient
import io.agora.chat.ChatMessage
import io.agora.chat.ChatRoom
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.ScreenCaptureParameters
import io.agora.rtc2.UserInfo
import io.agora.rtc2.video.SegmentationProperty
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE
import io.agora.rtc2.video.VideoEncoderConfiguration.ORIENTATION_MODE
import io.agora.rtc2.video.VirtualBackgroundSource
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val REQUESTED_PERMISSION =
        arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA)
    private val appId: String = SDKManager.APP_ID
    private var listMember = TempMeeting.ListMember
    private var uidLocal : Int = 0
    private val uidShareScreen = 1
    private val screenCaptureParameters = ScreenCaptureParameters()
    private var isShareScreen : Boolean = false
    private var rtcEngine: RtcEngine? = null

    private lateinit var meetingDetails : MeetingRoom
    private lateinit var handler: Handler
    private lateinit var adapterVideo: VideoAdapter
    private lateinit var fgService: Intent
    private lateinit var viewModel: MainViewModel
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var token : String
    private lateinit var channelName : String
    private lateinit var username : String
    private lateinit var roomID : String
    private lateinit var pref : DataPreference

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
        viewModel.ChangeCount()

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
                viewModel.ChangeCount()

                adapterVideo.submitList(listMember)
                adapterVideo.notifyItemChanged(adapterVideo.currentList.size - 1)

                binding.member.text = "${listMember.size}"
            }
        }

        override fun onLocalUserRegistered(uid: Int, userAccount: String?) {
            super.onLocalUserRegistered(uid, userAccount)
            if (userAccount != null) {
                Timer().schedule(object : TimerTask(){
                    override fun run() {
                        listMember.find { it.uid == uid }?.username = userAccount
                    }
                }, 2500)
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
                viewModel.ChangeCount()
                adapterVideo.notifyItemRemoved(position)

                binding.member.text = "${listMember.size}"
            }
        }
    }

    private fun joinChannel() {
        if (checkSelfPermission()) {
            val options= ChannelMediaOptions()
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            rtcEngine?.startPreview()

            if (username.isBlank()){
                rtcEngine?.joinChannel(token, channelName, uidLocal, options)
            } else{
                rtcEngine?.joinChannelWithUserAccount(token ,channelName, username, options)
            }
        } else {
            Toast.makeText(applicationContext, "Permission was not granted", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }

    private fun checkUnread(){
        val unread = ChatClient.getInstance().chatManager().getConversation(roomID).unreadMsgCount
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
        pref = DataPreference(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            meetingDetails = intent.getParcelableExtra("MeetingDetail", MeetingRoom::class.java)!!
        } else{
            @Suppress("DEPRECATION")
            meetingDetails = intent.getParcelableExtra("MeetingDetail")!!
        }

        if (meetingDetails.roomID.isNullOrBlank()){
            binding.layoutChat.isVisible = false
        }

        token = meetingDetails.rtcToken ?: ""
        channelName = meetingDetails.channelName ?: ""
        roomID = meetingDetails.roomID ?: ""
        username = ChatClient.getInstance().currentUser


        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        gridLayoutManager = GridLayoutManager(this, 2, LinearLayoutManager.HORIZONTAL, false)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.videosRecycleView.layoutManager = linearLayoutManager
        adapterVideo = VideoAdapter()

        initVidSDK()
        checkPref()
        joinChannel()
        joinChatRoom()
//        setIcon()

        binding.videosRecycleView.adapter = adapterVideo

        viewModel.memberCount.observe(this){list ->
            if (list.size < 2){
                binding.videosRecycleView.layoutManager = linearLayoutManager
            } else{
                binding.videosRecycleView.layoutManager = gridLayoutManager
            }
            binding.videosRecycleView.adapter = null
            binding.videosRecycleView.adapter = adapterVideo
        }
        viewModel.isScreenSharing.observe(this){isSharing ->
            isShareScreen = isSharing
            if (isSharing){
                binding.videosRecycleView.layoutManager = linearLayoutManager
            } else{
                if (listMember.size < 2){
                    binding.videosRecycleView.layoutManager = linearLayoutManager
                } else{
                    binding.videosRecycleView.layoutManager = gridLayoutManager
                }
            }
        }

        ChatClient.getInstance().chatManager().addMessageListener { messages ->
            if (messages != null){
                for (msg in messages){
                    if (msg.chatType == ChatMessage.ChatType.ChatRoom && msg.to == roomID){
                        Log.w("MessageListener", "onMessageReceived")

                        runOnUiThread {
                            checkUnread()
                            TempMeeting.TempChatRoom.add(msg)
                        }
                    }
                }
            }
        }

        binding.btnVolume.setOnClickListener {
            if (!TempMeeting.isVolumeOff){

                rtcEngine?.muteAllRemoteAudioStreams(true)

                binding.btnVolume.setImageResource(R.drawable.ic_volume_off)

                TempMeeting.isVolumeOff = true
            } else{

                rtcEngine?.muteAllRemoteAudioStreams(false)

                binding.btnVolume.setImageResource(R.drawable.ic_volume)

                TempMeeting.isVolumeOff = false
            }
        }

        binding.btnVidcam.setOnClickListener {
            if (!TempMeeting.isCameraOff){
                rtcEngine?.muteLocalVideoStream(true)
                rtcEngine?.stopPreview()

                binding.btnVidcam.setImageResource(R.drawable.ic_videocam_off)
                TempMeeting.isCameraOff = true

                listMember[0].offCam = true
                adapterVideo.notifyDataSetChanged()
            } else{

                rtcEngine?.muteLocalVideoStream(false)
                rtcEngine?.startPreview()

                binding.btnVidcam.setImageResource(R.drawable.ic_videocam)
                TempMeeting.isCameraOff = false

                listMember[0].offCam = false
                adapterVideo.notifyDataSetChanged()
            }
        }

        binding.btnMic.setOnClickListener {
            if (!TempMeeting.isMicOff){

                rtcEngine?.muteLocalAudioStream(true)

                binding.btnMic.setImageResource(R.drawable.ic_mic_off)

                TempMeeting.isMicOff = true
            } else{

                rtcEngine?.muteLocalAudioStream(false)

                binding.btnMic.setImageResource(R.drawable.ic_mic)

                TempMeeting.isMicOff = false
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
                ChatClient.getInstance().chatManager().getConversation(roomID).markAllMessagesAsRead()
            } catch (e: Exception){
                Log.e("Exception", e.message.toString())
            }

            val moveIntent = Intent(this, ChatActivity::class.java)
            moveIntent.putExtra("ROOM_ID", roomID)
            moveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(moveIntent)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                usePIP()
//            }
        }

    }


    private fun setIcon() {
        when(TempMeeting.isVolumeOff){
            true -> binding.btnVolume.setImageResource(R.drawable.ic_volume_off)
            false -> binding.btnVolume.setImageResource(R.drawable.ic_volume)
        }

        when(TempMeeting.isMicOff){
            true -> {
                binding.btnMic.setImageResource(R.drawable.ic_mic_off)

                rtcEngine?.muteLocalAudioStream(true)
            }
            false -> {
                binding.btnMic.setImageResource(R.drawable.ic_mic)

                rtcEngine?.muteLocalAudioStream(false)
            }
        }

        when(TempMeeting.isCameraOff){
            true -> {
                rtcEngine?.muteLocalVideoStream(true)
                rtcEngine?.stopPreview()

                binding.btnVidcam.setImageResource(R.drawable.ic_videocam_off)
            }
            false -> {
                rtcEngine?.muteLocalVideoStream(false)
                rtcEngine?.startPreview()

                binding.btnVidcam.setImageResource(R.drawable.ic_videocam)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setIcon()
        adapterVideo.notifyDataSetChanged()
    }
    private fun checkPref() {
        //resolution
        if (pref.getResolution() != null){
            when (pref.getResolution()){
                "High" -> rtcEngine?.setRemoteVideoStreamType(0, Constants.VIDEO_STREAM_HIGH)
                "Low" -> rtcEngine?.setRemoteVideoStreamType(0, Constants.VIDEO_STREAM_LOW)
            }
        }

        if (pref.getPrefBG() != null){
            val segmentationProperty = SegmentationProperty()
            segmentationProperty.modelType = SegmentationProperty.SEG_MODEL_AI
            segmentationProperty.greenCapacity = 0.5f

            val bgSource = VirtualBackgroundSource()
            when (pref.getPrefBG()){
                "Off" -> {
                    rtcEngine?.enableVirtualBackground(
                        false,
                        bgSource,
                        segmentationProperty
                    )
                }
                "Blur" -> {
                    bgSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_BLUR
                    bgSource.blurDegree = VirtualBackgroundSource.BLUR_DEGREE_HIGH

                    rtcEngine?.enableVirtualBackground(
                        true,
                        bgSource,
                        segmentationProperty
                    )
                }
                "Color" -> {
                    bgSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_COLOR
                    bgSource.color = pref.getColorBG()

                    rtcEngine?.enableVirtualBackground(
                        true,
                        bgSource,
                        segmentationProperty
                    )
                }
            }
        }
    }

    private fun joinChatRoom() {
        Log.w("CallBack ChatRoom", "Join ChatRoom")
        ChatClient.getInstance().chatroomManager().joinChatRoom(roomID, object : ValueCallBack<ChatRoom> {
            override fun onSuccess(value: ChatRoom?) {
                Log.w("CallBack ChatRoom", "Join ChatRoom Success")
            }

            override fun onError(error: Int, errorMsg: String?) {
                Log.w("CallBack ChatRoom", "$error : ${errorMsg.toString()}")
                Log.w("CallBack ChatRoom", "Retry Join ChatRoom")
                joinChatRoom()
            }

        })
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
        if (hasFocus){
            setIcon()
            try {
                checkUnread()
            } catch (e:Exception){
                Log.e("Exception", e.message.toString())
            }
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

        ChatClient.getInstance().chatroomManager().leaveChatRoom(roomID)
        TempMeeting.TempChatRoom.clear()
        Thread {
            RtcEngine.destroy()
        }.start()

        Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}
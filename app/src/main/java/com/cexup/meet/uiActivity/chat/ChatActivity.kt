package com.cexup.meet.uiActivity.chat

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.cexup.meet.R
import com.cexup.meet.data.ChatRTM
import com.cexup.meet.data.MeetingInfo
import com.cexup.meet.databinding.ActivityChatBinding
import com.cexup.meet.utils.SDKManager
import com.cexup.meet.utils.TempMeeting
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback

class ChatActivity : AppCompatActivity(){


    private lateinit var binding: ActivityChatBinding
    private lateinit var adapterChat : ChatLogAdapter
    private lateinit var meetingDetails : MeetingInfo

    private  var rtcEngine = SDKManager.rtcEngine
    private var rtmClient = SDKManager.rtmClient
    private var rtmChannel = SDKManager.rtmChannel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            meetingDetails = intent.getParcelableExtra("Meeting_Info", MeetingInfo::class.java)!!
        } else{
            @Suppress("DEPRECATION")
            meetingDetails = intent.getParcelableExtra("Meeting_Info")!!
        }

        seticon()


        binding.rvChatlog.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapterChat = ChatLogAdapter(TempMeeting.TempChatRoom)
        binding.rvChatlog.adapter = adapterChat
        binding.rvChatlog.scrollToPosition(TempMeeting.TempChatRoom.size - 1)

        binding.btnVidcamChat.setOnClickListener {
            if (!TempMeeting.isCameraOff){
                binding.btnVidcamChat.setImageResource(R.drawable.ic_videocam_off)

                rtcEngine?.muteLocalVideoStream(true)
                TempMeeting.isCameraOff = true
            } else{

                binding.btnVidcamChat.setImageResource(R.drawable.ic_videocam)

                rtcEngine?.muteLocalVideoStream(false)
                TempMeeting.isCameraOff = false
            }
        }

        binding.btnMicChat.setOnClickListener {
            if (!TempMeeting.isMicOff){
                binding.btnMicChat.setImageResource(R.drawable.ic_mic_off)

                rtcEngine?.muteLocalAudioStream(true)
                TempMeeting.isMicOff = true
            } else{

                binding.btnMicChat.setImageResource(R.drawable.ic_mic)

                rtcEngine?.muteLocalAudioStream(false)
                TempMeeting.isMicOff = false
            }
        }

        binding.btnOtherMenu.setOnClickListener {
            binding.optionMenu.isVisible = !binding.optionMenu.isVisible
        }

        binding.btnSendChat.setOnClickListener {
            val etMessage = binding.etChat.text
            if (etMessage.isNotBlank()){

                val message = rtmClient?.createMessage()
                message?.text = etMessage.toString()

                if (message != null){
                    rtmChannel?.sendMessage(message, object : ResultCallback<Void>{
                        override fun onSuccess(p0: Void?) {
                            TempMeeting.TempChatRoom.add(
                                ChatRTM(
                                    meetingDetails.username?: "UserName",
                                    meetingDetails.channelName?: "Channel Name",
                                    message
                                )
                            )

                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                            etMessage.clear()
                            adapterChat.notifyDataSetChanged()
                            binding.rvChatlog.scrollToPosition(TempMeeting.TempChatRoom.size - 1)
                        }

                        override fun onFailure(p0: ErrorInfo?) {
                            Log.e("SendMessage onFailure", p0.toString())
                            Toast.makeText(this@ChatActivity, p0?.errorDescription, Toast.LENGTH_LONG).show()
                        }

                    })
                }
            }
        }
    }

    private fun seticon() {

        when(TempMeeting.isMicOff){
            true -> {
                binding.btnMicChat.setImageResource(R.drawable.ic_mic_off)

                rtcEngine?.muteLocalAudioStream(true)
            }
            false -> {
                binding.btnMicChat.setImageResource(R.drawable.ic_mic)

                rtcEngine?.muteLocalAudioStream(false)
            }
        }

        when(TempMeeting.isCameraOff){
            true -> {
                binding.btnVidcamChat.setImageResource(R.drawable.ic_videocam_off)

                rtcEngine?.muteLocalVideoStream(true)
                rtcEngine?.stopPreview()
                TempMeeting.ListMember[0].offCam = true
            }
            false -> {
                binding.btnVidcamChat.setImageResource(R.drawable.ic_videocam)

                rtcEngine?.muteLocalVideoStream(false)
                rtcEngine?.startPreview()
                TempMeeting.ListMember[0].offCam = false
            }
        }
    }
}
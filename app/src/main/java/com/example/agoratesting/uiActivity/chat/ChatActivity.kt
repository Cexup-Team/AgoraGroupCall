package com.example.agoratesting.uiActivity.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.agoratesting.R
import com.example.agoratesting.databinding.ActivityChatBinding
import com.example.agoratesting.utils.TempMeeting
import com.example.agoratesting.utils.VidSDK
import io.agora.CallBack
import io.agora.ConnectionListener
import io.agora.chat.ChatClient
import io.agora.chat.ChatMessage
import io.agora.chat.ChatMessage.ChatType

class ChatActivity : AppCompatActivity(){


    private lateinit var binding: ActivityChatBinding
    private lateinit var roomID : String

    private  var rtcEngine = VidSDK.rtcEngine
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomID = intent.getStringExtra("ROOM_ID")!!
        loadPrevMessage()
        initListener()
        seticon()

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
                val message = ChatMessage.createTextSendMessage(etMessage.toString(), roomID)
                message.chatType = ChatType.ChatRoom
                message.setMessageStatusCallback(object : CallBack{
                    override fun onSuccess() {
                        Log.w("Sent Message", "Message Sent Success")
                        Log.w("Sent Message", "Room ID : $roomID")

                        addChatView(message)
                        TempMeeting.TempChatRoom.add(message)
                        etMessage.clear()
                        binding.scrollChat.fullScroll(View.FOCUS_DOWN)
                    }

                    override fun onError(code: Int, error: String?) {
                        Log.e("Send CallBack", "${code} : ${error.toString()}")
                    }

                })
                ChatClient.getInstance().chatManager().sendMessage(message)
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

    private fun loadPrevMessage() {
        for (i in TempMeeting.TempChatRoom){
            addChatView(i)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            ChatClient.getInstance().chatManager().getConversation(roomID).markAllMessagesAsRead()
        } catch (e: Exception){
            Log.e("Exception", e.message ?:"")
        }
    }
    private fun initListener(){

        //chat listener
        Log.w("Listener", "Chat Listener")
        ChatClient.getInstance().chatManager().addMessageListener { messages ->
            if (messages != null) {
                Log.w("Messages", "Message Received")
                Log.w("Messages", "Size : ${messages.size}")

                for (msg in messages) {
                    if (msg.chatType == ChatType.ChatRoom && msg.to == roomID){
                        addChatView(msg)
                    }
                }
            } else {
                Log.e("Message", "Message is Empty")
            }
        }

        //connection listener
        Log.w("Listener", "conn Listener")
        ChatClient.getInstance().addConnectionListener(object : ConnectionListener{
            override fun onConnected() {
                Log.w("Connection", "Connected")
            }

            override fun onDisconnected(errorCode: Int) {
                Log.e("Connection", "Disconnected")
            }
        })

    }

    private fun parseMessage(it:String): String {
        val splitMessage = it.split("")
        var contentMessage = ""
        for(i in 6 .. (splitMessage.size - 3)){
            contentMessage += splitMessage[i]
        }

        return contentMessage
    }

    private fun addChatView(message: ChatMessage){
        runOnUiThread {
            val messageContent = parseMessage(message.body.toString())
            val viewListChat = binding.chatLog
            if (message.from == ChatClient.getInstance().currentUser){
                val viewChat = layoutInflater.inflate(R.layout.item_chat_sent, null)
                viewChat.findViewById<TextView>(R.id.sent_username).text = message.from
                viewChat.findViewById<TextView>(R.id.sent_message).text = messageContent
                viewListChat.addView(viewChat)
            } else{
                val viewChat = layoutInflater.inflate(R.layout.item_chat_received, null)
                viewChat.findViewById<TextView>(R.id.received_username).text = message.from
                viewChat.findViewById<TextView>(R.id.received_message).text = messageContent

                viewListChat.addView(viewChat)

            }
        }
    }
}
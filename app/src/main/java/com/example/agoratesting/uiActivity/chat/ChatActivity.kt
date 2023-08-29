package com.example.agoratesting.uiActivity.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.agoratesting.R
import com.example.agoratesting.databinding.ActivityChatBinding
import com.example.agoratesting.utils.VidSDK
import com.example.agoratesting.utils.chatManager
import com.example.agoratesting.utils.userManager
import io.agora.CallBack
import io.agora.ConnectionListener
import io.agora.chat.ChatClient
import io.agora.chat.ChatMessage
import io.agora.chat.ChatMessage.ChatType

class ChatActivity : AppCompatActivity(){


    private lateinit var binding: ActivityChatBinding

    private var targetID :String = chatManager.targetID
    private var isChatRoom : Boolean = chatManager.isChatRoom

    private var rtcEngine = VidSDK.rtcEngine
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()

        binding.btnVidcamChat.setOnClickListener {
            if (it.tag == "ic_videocam"){
                binding.btnVidcamChat.setImageResource(R.drawable.ic_videocam_off)
                binding.btnVidcamChat.tag = "ic_videocam_off"

                rtcEngine?.enableLocalVideo(false)
            } else if (it.tag == "ic_mic_off"){

                binding.btnVidcamChat.setImageResource(R.drawable.ic_videocam)
                binding.btnVidcamChat.tag = "ic_videocam"

                rtcEngine?.enableLocalVideo(true)
            }
        }

        binding.btnMicChat.setOnClickListener {
            if (it.tag == "ic_mic"){
                binding.btnMicChat.setImageResource(R.drawable.ic_mic_off)
                binding.btnMicChat.tag = "ic_mic_off"

                rtcEngine?.enableLocalAudio(false)
            } else if (it.tag == "ic_mic_off"){

                binding.btnMicChat.setImageResource(R.drawable.ic_mic)
                binding.btnMicChat.tag = "ic_mic"

                rtcEngine?.enableLocalAudio(true)
            }
        }

        binding.btnOtherMenu.setOnClickListener {
            if (binding.optionMenu.isVisible){
                binding.optionMenu.isVisible = false
            } else{
                binding.optionMenu.isVisible = true
            }
        }
        binding.btnSendChat.setOnClickListener {
            val etMessage = binding.etChat.text
            if (etMessage.isNotBlank()){
                val message = ChatMessage.createTextSendMessage(etMessage.toString(), targetID)
                if (isChatRoom){
                    message.chatType = ChatType.ChatRoom
                } else{
                    message.chatType = ChatType.Chat
                }
                message.setMessageStatusCallback(object : CallBack{
                    override fun onSuccess() {
                        Log.w("Send CallBack", "Message Sent Successfully")
                        Log.w("Sent Message", "Message Sent Success")
                        addChatView(message)
                        etMessage.clear()
                        binding.scrollChat.fullScroll(View.FOCUS_DOWN)
                    }

                    override fun onError(code: Int, error: String?) {
                        Log.e("Send CallBack", error.toString())
                    }

                })
                ChatClient.getInstance().chatManager().sendMessage(message)
            }
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
                    if (msg.from == targetID){
                        addChatView(msg)
                    } else if (msg.chatType == ChatType.ChatRoom){
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
            if (message.from == userManager.username){
                val viewChat = layoutInflater.inflate(R.layout.chat_sent, null)
                viewChat.findViewById<TextView>(R.id.sent_username).text = message.from
                viewChat.findViewById<TextView>(R.id.sent_message).text = messageContent
                viewListChat.addView(viewChat)
            } else{
                val viewChat = layoutInflater.inflate(R.layout.chat_received, null)
                viewChat.findViewById<TextView>(R.id.received_username).text = message.from
                viewChat.findViewById<TextView>(R.id.received_message).text = messageContent

                viewListChat.addView(viewChat)

            }
        }
    }
}
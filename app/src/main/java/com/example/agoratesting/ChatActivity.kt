package com.example.agoratesting

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.agoratesting.databinding.ActivityChatBinding
import io.agora.CallBack
import io.agora.ConnectionListener
import io.agora.ValueCallBack
import io.agora.chat.ChatClient
import io.agora.chat.ChatMessage
import io.agora.chat.ChatMessage.ChatType
import io.agora.chat.ChatOptions
import io.agora.chat.ChatRoom
import java.util.Timer
import java.util.TimerTask

class ChatActivity : AppCompatActivity(){

//    val USERNAME = "tester"
//    private val TOKEN = "007eJxTYJjTX9qY8/7t8p9XXq3qPs2l3MuQuUdxed6DwKXc1x7rSr5QYEg0SkyyNDMzS0lJNjExtjRMMjRPSUtKMkk0MgdKGRp4HNmQ0hDIyCAV8I6RkYGVgREIQXwVBjOLtERDi0QDXSMDcxNdQ8PUVN0ks6REXZMUSyPzFNO0JEPDNACDvCnU"

    val USERNAME = "tester2"
    private val TOKEN = "007eJxTYLggvDSuxHnq0Zi90/boq3gyTHD7NmFpftsT52/v+htUP/kpMCQaJSZZmpmZpaQkm5gYWxomGZqnpCUlmSQamQOlDA1kWbelNAQyMuSKf2BgZGAFYkYGEF+FISUxxdjA0NxA18jIwEDX0DA1VTfJOBFImJqZGSSaJRoZWqQBAKyLJ48="

    private val APP_KEY = "611000680#1167414"
    private val ROOM_ID = "219929636831233"

    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSDK()
        initRoomChat()
        initListener()


        binding.btnSendChat.setOnClickListener {
            val etMessage = binding.etChat.text
            var isSendSuccess = false
            if (etMessage.isNotBlank()){
                val message = ChatMessage.createTextSendMessage(etMessage.toString(), ROOM_ID)
                message.chatType = ChatType.ChatRoom
                message.setMessageStatusCallback(object : CallBack{
                    override fun onSuccess() {
                        Log.w("Send CallBack", "Message Sent Successfully")
                        isSendSuccess = true
                        Toast.makeText(this@ChatActivity, "Message Sent Successfully", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(code: Int, error: String?) {
                        Log.e("Send CallBack", error.toString())
                        isSendSuccess = false
                    }

                })
                ChatClient.getInstance().chatManager().sendMessage(message)

                Timer().schedule(object :TimerTask(){
                    override fun run() {
                        if (isSendSuccess){

                            val messageContent = parseMessage(message.body.toString())
                            runOnUiThread {
                                Log.w("Sent Message", "Message Sent Success")
                                val viewListChat = findViewById<LinearLayout>(R.id.chat_log)

                                val viewChat = layoutInflater.inflate(R.layout.chat_sent, null)
                                viewChat.findViewById<TextView>(R.id.sent_username).text = "You"
                                viewChat.findViewById<TextView>(R.id.sent_message).text = messageContent

                                viewListChat.addView(viewChat)

                                binding.scrollChat.fullScroll(View.FOCUS_DOWN)
                                etMessage.clear()
                                isSendSuccess = false
                            }
                        }
                    }

                }, 500)
            }
        }
    }

    override fun onDestroy() {
        ChatClient.getInstance().logout(true)
        super.onDestroy()
    }

    private fun initSDK(){
        val options =  ChatOptions()

        // Set to log in automatically
        options.autoLogin = true

        // Sets your App Key to options.
        Log.w("App Key", APP_KEY)
        options.appKey = APP_KEY

        // Initializes the Agora Chat SDK.
        Log.w("Agora Chat SDK", "Initializes the Agora Chat SDK")
        ChatClient.getInstance().init(this, options)

        // Makes the Agora Chat SDK debuggable.
        Log.w("Agora Chat SDK", "Makes the Agora Chat SDK debuggable")
        ChatClient.getInstance().setDebugMode(true)
    }

    private fun initRoomChat(){

        //login with token
        if (ChatClient.getInstance().isLoggedIn.not()){
            Log.w("Login", "loginWithAgoraToken")
            ChatClient.getInstance().loginWithAgoraToken(USERNAME, TOKEN, object :CallBack{
                override fun onSuccess() {
                    Log.w("Login CallBack", "Login Success")
                }

                override fun onError(code: Int, error: String?) {
                    Log.e("Login CallBack", error.toString())
                }

            })
        }

        //join chatroom
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (ChatClient.getInstance().isLoggedIn){
                    Log.w("ChatRoom", "Join ChatRoom")
                    ChatClient.getInstance().chatroomManager().joinChatRoom(ROOM_ID, object : ValueCallBack<ChatRoom>{
                        override fun onSuccess(value: ChatRoom?) {
                            Log.w("CallBack<ChatRoom>", "Success Join Room")
                        }

                        override fun onError(error: Int, errorMsg: String?) {
                            Log.e("CallBack<ChatRoom>", errorMsg.toString())
                        }

                    })
                }
            }
        }, 2000)
    }
    private fun initListener(){

        //chat listener
        Log.w("Listener", "Chat Listener")
        ChatClient.getInstance().chatManager().addMessageListener { messages ->
            if (messages != null) {
                Log.w("Messages", "Message Received")
                Log.w("Messages", "Size : ${messages.size}")

                for (msg in messages) {
                    val messageContent = parseMessage(msg.body.toString())
                    val viewListChat = findViewById<LinearLayout>(R.id.chat_log)
                    val viewChat = layoutInflater.inflate(R.layout.chat_received, viewListChat)
                    viewChat.findViewById<TextView>(R.id.received_username).text = msg.from
                    viewChat.findViewById<TextView>(R.id.received_message).text = messageContent

                    viewListChat.addView(viewChat)
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
}
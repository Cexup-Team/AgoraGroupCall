package com.example.agoratesting

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.agoratesting.databinding.ActivityChatBinding
import io.agora.CallBack
import io.agora.ConnectionListener
import io.agora.MessageListener
import io.agora.ValueCallBack
import io.agora.chat.ChatClient
import io.agora.chat.ChatMessage
import io.agora.chat.ChatMessage.ChatType
import io.agora.chat.ChatOptions
import io.agora.chat.ChatRoom
import io.agora.chat.TextMessageBody
import java.util.Timer
import java.util.TimerTask

class ChatActivity : AppCompatActivity(){

//    val USERNAME = "tester"
//    private val TOKEN = "007eJxTYJjTX9qY8/7t8p9XXq3qPs2l3MuQuUdxed6DwKXc1x7rSr5QYEg0SkyyNDMzS0lJNjExtjRMMjRPSUtKMkk0MgdKGRp4HNmQ0hDIyCAV8I6RkYGVgREIQXwVBjOLtERDi0QDXSMDcxNdQ8PUVN0ks6REXZMUSyPzFNO0JEPDNACDvCnU"

    val USERNAME = "tester2"
    private val TOKEN = "007eJxTYCiZL7Fxxy2zkrXcV9IcJFz+Prieo1oe+GmfF+vr59nOJ7gUGBKNEpMszczMUlKSTUyMLQ2TDM1T0pKSTBKNzIFShgb3rmxIaQhkZFBayMXEyMDKwAiEIL4KQ0piirGBobmBrpGRgYGuoWFqqm6ScSKQMDUzM0g0SzQytEgDAMWRKAU="

    private val APP_KEY = "611000680#1167414"
    private val ROOM_ID = "219929636831233"

    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()


        binding.btnSendChat.setOnClickListener {
            val etMessage = binding.etChat.text
            val message = ChatMessage.createTextSendMessage(etMessage.toString(), ROOM_ID)
            message.chatType = ChatType.ChatRoom
            message.setMessageStatusCallback(callbackSend)
            ChatClient.getInstance().chatManager().sendMessage(message)

            runOnUiThread {
                Log.w("Sent Message", "Message Sent Success")
                val viewListChat = findViewById<LinearLayout>(R.id.chat_log)

                val viewChat = layoutInflater.inflate(R.layout.chat_sent, null)
                viewChat.findViewById<TextView>(R.id.sent_username).setText("You")
                if (message.type == ChatMessage.Type.TXT){
                }
                val a = TextMessageBody(message.body.toString())
                viewChat.findViewById<TextView>(R.id.sent_message).setText(a.message)

                viewListChat.addView(viewChat)
                binding.scrollChat.fullScroll(View.FOCUS_DOWN)
            }

            etMessage.clear()
        }
    }

    override fun onDestroy() {
        ChatClient.getInstance().logout(true)
        super.onDestroy()
    }

    private val mMessageListener : MessageListener = object : MessageListener{
        override fun onMessageReceived(messages: MutableList<ChatMessage>?) {
            if (messages != null) {
                Log.w("Messages",  "Message Received")
                Log.w("Messages",  "Size : ${messages.size}")

                for (msg in messages){
                    val viewListChat = findViewById<LinearLayout>(R.id.chat_log)
                    val viewChat = layoutInflater.inflate(R.layout.chat_received, viewListChat)
                    viewChat.findViewById<TextView>(R.id.received_username).setText(msg.from)
                    viewChat.findViewById<TextView>(R.id.received_message).setText(msg.body.toString())
                    viewListChat.addView(viewChat)
                }
            }
            else{
                Log.e("Message", "Message is Empty")
            }
        }
    }

    private val mConnectionListener : ConnectionListener = object : ConnectionListener{
        override fun onConnected() {
            runOnUiThread {
                Log.w("Connection", "Connected")
//                Toast.makeText(baseContext, "Connected", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onDisconnected(errorCode: Int) {
            Log.e("Connection", "Disconnected")
        }
    }

    private val callbackSend = object : CallBack{
        override fun onSuccess() {
            runOnUiThread {
                Log.w("Send CallBack", "Message Sent Successfully")
//                Toast.makeText(baseContext, "Message Sent Successfully", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onError(code: Int, error: String?) {
            Log.e("Send CallBack", error.toString())
        }
    }
    private val callbackLogin = object : CallBack{
        override fun onSuccess() {
            runOnUiThread {
                Log.w("Login CallBack", "Login Success")
//                Toast.makeText(baseContext, "Login Success", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onError(code: Int, error: String?) {
            Log.e("Login CallBack", error.toString())
        }
    }

    private val roomCallBack = object  :ValueCallBack<ChatRoom>{
        override fun onSuccess(value: ChatRoom?) {
            runOnUiThread {
                Log.w("CallBack<ChatRoom>", "Success Join Room")
//                Toast.makeText(baseContext, "Success Join Room", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onError(error: Int, errorMsg: String?) {
            Log.e("CallBack<ChatRoom>", errorMsg.toString())
        }

    }

    private fun init(){
        val options =  ChatOptions()

        // Sets your App Key to options.
        Log.w("App Key", APP_KEY)
        options.appKey = APP_KEY
        // Initializes the Agora Chat SDK.
        Log.w("Agora Chat SDK", "Initializes the Agora Chat SDK")
        ChatClient.getInstance().init(this, options)
        // Makes the Agora Chat SDK debuggable.
        Log.w("Agora Chat SDK", "Makes the Agora Chat SDK debuggable")
        ChatClient.getInstance().setDebugMode(true)

        //chat listener
        Log.w("Listener", "Chat Listener")
        ChatClient.getInstance().chatManager().addMessageListener(mMessageListener)

        //connection listener
        Log.w("Listener", "conn Listener")
        ChatClient.getInstance().addConnectionListener(mConnectionListener)

        //login with token
        if (ChatClient.getInstance().isLoggedIn.not()){
            Log.w("Login", "loginWithAgoraToken")
            ChatClient.getInstance().loginWithAgoraToken(USERNAME, TOKEN, callbackLogin)
        }

        //join chatroom
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (ChatClient.getInstance().isLoggedIn){
                    Log.w("ChatRoom", "Join ChatRoom")
                    ChatClient.getInstance().chatroomManager().joinChatRoom(ROOM_ID, roomCallBack)
                }
            }
        }, 2000)
    }
}
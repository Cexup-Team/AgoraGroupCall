package com.example.agoratesting

import android.app.Application
import android.util.Log
import com.example.agoratesting.data.Chat
import com.example.agoratesting.utils.ClassUtils
import com.example.agoratesting.utils.TempChat
import com.example.agoratesting.utils.VidSDK
import com.example.agoratesting.utils.chatManager
import io.agora.chat.ChatClient
import io.agora.chat.ChatOptions

class MainApplication : Application() {
    private var globalSettings:GlobalSettings?= null


    override fun onCreate() {
        super.onCreate()
        initChatSDK()
    }

    private fun initChatSDK() {

        try {
            val options =  ChatOptions()

            // Sets your App Key to options.
            Log.w("App Key", chatManager.APP_KEY)
            options.appKey = chatManager.APP_KEY
            // Initializes the Agora Chat SDK.
            Log.w("Agora Chat SDK", "Initializes the Agora Chat SDK")
            ChatClient.getInstance().init(this, options)

            // Makes the Agora Chat SDK debuggable.
            Log.w("Agora Chat SDK", "Makes the Agora Chat SDK debuggable")
            ChatClient.getInstance().setDebugMode(true)

        }catch (e: Exception) {
            Log.e("Chat SDK", "initChatSDK Error : ${e.message}")
        }
    }

    fun getGlobalSettings():GlobalSettings? {
        if(globalSettings== null) {
            globalSettings = GlobalSettings()
        }
        return globalSettings
    }

    private fun initChatListener(){
        ChatClient.getInstance().chatManager().addMessageListener { messages ->
            for (msg in messages){
                val msgDetail= Chat(
                    idChat = msg.msgId,
                    contentChat = parseMessage(msg.body.toString()),
                    senderChat = msg.from
                )
                TempChat?.put(msg.msgId, msgDetail)
            }
        }
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
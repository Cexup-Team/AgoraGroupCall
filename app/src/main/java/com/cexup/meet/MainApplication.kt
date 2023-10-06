package com.cexup.meet

import android.app.Application
import android.util.Log
import com.cexup.meet.utils.SDKManager
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
            Log.w("App Key", SDKManager.APP_KEY)
            options.appKey = SDKManager.APP_KEY
            options.requireDeliveryAck = true
            options.autoLogin = true
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

}
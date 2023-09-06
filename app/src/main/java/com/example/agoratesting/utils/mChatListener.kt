package com.example.agoratesting.utils

import android.util.Log
import io.agora.MessageListener
import io.agora.chat.ChatMessage

object mChatListener : MessageListener {
    override fun onMessageReceived(messages: MutableList<ChatMessage>?) {
        if (messages != null){
            for (msg in messages){
                if (msg.chatType == ChatMessage.ChatType.ChatRoom && msg.to == chatManager.ROOM_ID){
                    Log.w("MessageListener", "onMessageReceived")

                    TempChatRoom.add(msg)
                }
            }
        }
    }
}
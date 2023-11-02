package com.cexup.meet.uiActivity.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cexup.meet.data.ChatRTM
import com.cexup.meet.utils.TempMeeting

class ChatViewModel: ViewModel() {
    private val _chatLog = MutableLiveData<List<ChatRTM>>(TempMeeting.TempChatRoom)
    val chatLog : LiveData<List<ChatRTM>> = _chatLog
}
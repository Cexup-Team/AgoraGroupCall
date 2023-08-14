package com.example.agoratesting.uiActivity.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agoratesting.data.Chat

class ChatViewModel : ViewModel() {

    val _chatLog  = MutableLiveData<Map<String, Chat>>()

    init {

    }
}
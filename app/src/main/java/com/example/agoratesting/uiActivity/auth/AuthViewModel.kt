package com.example.agoratesting.uiActivity.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agoratesting.utils.chatManager
import io.agora.CallBack
import io.agora.ValueCallBack
import io.agora.chat.ChatClient
import io.agora.chat.ChatRoom

class AuthViewModel :ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    private val _joinedMeeting = MutableLiveData<Boolean>()
    val joinedMeeting: LiveData<Boolean> = _joinedMeeting


    private val _joinedChat = MutableLiveData<Boolean>()
    val joinedChat: LiveData<Boolean> = _joinedChat

    var errorMSG = ""
    fun joinMeeting(username: String, token:String, roomID:String){
        _isLoading.value = true
        if (ChatClient.getInstance().isLoggedIn){
            joinChatRoom(roomID)
            Log.w("CallBack Login", "UserLoggedIn")
        }
        else{
            ChatClient.getInstance().loginWithAgoraToken(username, token, object : CallBack{
                override fun onSuccess() {
                    Log.w("CallBack Login", "Login Success")
                    joinChatRoom(roomID)
                }

                override fun onError(code: Int, error: String?) {
                    Log.w("CallBack Login", "${code} : ${error.toString()}")
                    errorMSG = error.toString()
                    _isLoading.postValue(false)
                }

            })
        }
    }

    fun joinChatRoom(roomID:String){
        Log.w("CallBack ChatRoom", "Join ChatRoom")
        ChatClient.getInstance().chatroomManager().joinChatRoom(roomID, object :ValueCallBack<ChatRoom>{
            override fun onSuccess(value: ChatRoom?) {

                _joinedMeeting.postValue(true)
                _isLoading.postValue(false)
                chatManager.ROOM_ID = roomID
                chatManager.isChatRoom = true
                errorMSG = ""
                Log.w("CallBack ChatRoom", "Join ChatRoom Success")
            }

            override fun onError(error: Int, errorMsg: String?) {
                errorMSG = errorMsg.toString()
                _isLoading.postValue(false)
                Log.w("CallBack ChatRoom", "${error} : ${errorMsg.toString()}")
            }

        })
    }

}
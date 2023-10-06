package com.cexup.meet.uiActivity.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.agora.CallBack
import io.agora.chat.ChatClient

class AuthViewModel :ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loggedIn = MutableLiveData<Boolean>()
    val loggedIn : LiveData<Boolean> = _loggedIn

    var errorMSG = ""

    init {
        if (ChatClient.getInstance().isLoggedIn){
            _loggedIn.postValue(true)
        }
    }
    fun login(username: String, password:String){
        _isLoading.value = true
        if (ChatClient.getInstance().isLoggedIn){
            Log.w("CallBack Login", "UserLoggedIn")
            errorMSG = ""
            _isLoading.postValue(false)
            _loggedIn.postValue(true)
        } else{
            ChatClient.getInstance().login(username, password, object : CallBack{
                override fun onSuccess() {
                    Log.w("CallBack Login", "Login Success")
                    errorMSG = ""
                    _isLoading.postValue(false)
                    _loggedIn.postValue(true)
                }

                override fun onError(code: Int, error: String?) {
                    Log.w("CallBack Login", "$code : ${error.toString()}")
                    errorMSG = error.toString()
                    _isLoading.postValue(false)
                    _loggedIn.postValue(false)
                }

            })
        }
    }

}
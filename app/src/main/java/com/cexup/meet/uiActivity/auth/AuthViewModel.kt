package com.cexup.meet.uiActivity.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cexup.meet.clientAPI.ApiConfig
import com.cexup.meet.clientAPI.GetTokenResponse
import com.cexup.meet.utils.TempMeeting
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel :ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isTokenReceived = MutableLiveData<Boolean>()
    val isTokenReceived : LiveData<Boolean> = _isTokenReceived

    var errorMSG = ""

    fun joinChannel(channel : String, username : String){
        _isLoading.value = true

        val client = ApiConfig.getApiService().getToken(
            channelName = channel,
            role = "publisher", // "publisher" or "subscriber"
            tokenType = "userAccount", // uid or userAccount
            rtcuid = username, // rtcuid (type varies String/Int based on tokenType)
            rtmuid = username, //rtmuid which is a String
            expiry = 7200, //in second
        )
        client.enqueue(object : Callback<GetTokenResponse>{
            override fun onResponse(
                call: Call<GetTokenResponse>,
                response: Response<GetTokenResponse>
            ) {
                Log.e("Call", call.request().url().toString())
                if (response.isSuccessful){
                    val body = response.body()
                    if (body != null){
                        Log.e("Response rtc", body.rtcToken)
                        Log.e("Response rtm", body.rtmToken)

                        TempMeeting.rtcToken = body.rtcToken
                        TempMeeting.rtmToken = body.rtmToken

                        _isTokenReceived.value = true
                        _isLoading.value = false
                    }
                } else{
                    errorMSG = response.raw().toString()
                    Log.e("Error ${response.code()}", response.raw().toString())
                    _isLoading.value = false
                    _isTokenReceived.value = false
                }
            }

            override fun onFailure(call: Call<GetTokenResponse>, t: Throwable) {
                errorMSG = t.message ?: ""
                _isLoading.value = false
                _isTokenReceived.value = false
            }

        })
    }
}
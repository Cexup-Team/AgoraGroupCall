package com.example.agoratesting.uiActivity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _ureadMSG = MutableLiveData<Int>()
    val unreadMSG :LiveData<Int> = _ureadMSG

    private val _isScreenSharing = MutableLiveData<Boolean>()
    val isScreenSharing : LiveData<Boolean> = _isScreenSharing

    fun UnreadMark(count : Int){
        _ureadMSG.postValue(count)
    }

    fun ChangeSharingState(isSharing : Boolean){
        if (isSharing){
            _isScreenSharing.postValue(true)
        } else{
            _isScreenSharing.postValue(false)
        }
    }
}
package com.example.agoratesting.uiActivity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _isScreenSharing = MutableLiveData<Boolean>()
    val isScreenSharing : LiveData<Boolean> = _isScreenSharing

    fun ChangeSharingState(isSharing : Boolean){
        if (isSharing){
            _isScreenSharing.postValue(true)
        } else{
            _isScreenSharing.postValue(false)
        }
    }
}
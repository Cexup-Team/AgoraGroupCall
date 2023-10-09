package com.cexup.meet.uiActivity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cexup.meet.data.AccountInfo
import com.cexup.meet.utils.TempMeeting

class MainViewModel : ViewModel() {

    private val _isScreenSharing = MutableLiveData<Boolean>()
    val isScreenSharing : LiveData<Boolean> = _isScreenSharing

    private val _memberCount = MutableLiveData<List<AccountInfo>>()
    val memberCount : LiveData<List<AccountInfo>> = _memberCount

    fun ChangeSharingState(isSharing : Boolean){
        if (isSharing){
            _isScreenSharing.postValue(true)
        } else{
            _isScreenSharing.postValue(false)
        }
    }

    fun ChangeCount(){
        _memberCount.postValue(TempMeeting.ListMember)
    }
}
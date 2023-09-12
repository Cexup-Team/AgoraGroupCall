package com.example.agoratesting.uiActivity.listmeeting

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agoratesting.databinding.ActivityListMeetingBinding
import com.example.agoratesting.utils.dummyMeetingList

class ListMeetingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListMeetingBinding
    private lateinit var adapter : ListMeetingAdapter


    private val ReqID = 22
    private val REQUESTED_PERMISSION =
        arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ListMeetingAdapter()
        binding.rvListMeet.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvListMeet.adapter = adapter

        adapter.submitList(dummyMeetingList)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus){
            sendPermReq()
        }
    }
    private fun sendPermReq() {
        if (checkSelfPermission()){
            //nothing
        }else{
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSION, ReqID)
        }
    }

    private fun checkSelfPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                REQUESTED_PERMISSION[0]
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                REQUESTED_PERMISSION[1]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }
}
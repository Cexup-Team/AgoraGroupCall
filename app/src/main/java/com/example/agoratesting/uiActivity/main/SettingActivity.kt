package com.example.agoratesting.uiActivity.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.example.agoratesting.databinding.ActivitySettingBinding
import com.example.agoratesting.utils.VidSDK
import io.agora.rtc2.Constants

class SettingActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivitySettingBinding

    private var rtcEngine = VidSDK.rtcEngine
    private val resOption = arrayOf("High", "Low")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spinnerRes.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (resOption[position] == "High"){
            rtcEngine?.setRemoteVideoStreamType(0, Constants.VIDEO_STREAM_HIGH)
        } else if (resOption[position] == "Low"){
            rtcEngine?.setRemoteVideoStreamType(0, Constants.VIDEO_STREAM_LOW)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //DO Nothing
    }

}
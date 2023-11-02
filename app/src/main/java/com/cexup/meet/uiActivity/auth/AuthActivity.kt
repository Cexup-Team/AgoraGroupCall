package com.cexup.meet.uiActivity.auth

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.cexup.meet.data.MeetingInfo
import com.cexup.meet.databinding.ActivityAuthBinding
import com.cexup.meet.uiActivity.main.MainActivity
import com.cexup.meet.utils.DataPreference

class AuthActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAuthBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var pref : DataPreference

    private var username = ""
    private var channel = ""

    private val ReqID = 22
    private val REQUESTED_PERMISSION =
        arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = DataPreference(this)
        if (pref.getPrefString("Resolution") != null){
            pref.savePrefString("Resolution", "Auto")
        }

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        viewModel.isLoading.observe(this){isLoading ->
            if (isLoading){
                binding.authPB.isVisible = true
                binding.tvError.isVisible = false
                binding.linearJoinChannel.isVisible = false
            }
            else{
                binding.authPB.isVisible = false
                binding.linearJoinChannel.isVisible = true
                if (viewModel.errorMSG.isNotEmpty()){
                    binding.tvError.isVisible = true
                    binding.tvError.text = viewModel.errorMSG
                }
            }
        }

        viewModel.isTokenReceived.observe(this){isTokenReceived ->
            if (isTokenReceived){
                val rtmToken = viewModel.rtmToken
                val rtcToken = viewModel.rtcToken
                val moveIntent = Intent(this@AuthActivity, MainActivity::class.java)
                moveIntent.putExtra("Meeting_Info", MeetingInfo(rtcToken = rtcToken, rtmToken = rtmToken, username = username, channelName = channel))
                startActivity(moveIntent)
            }
        }

        binding.btnJoinChannel.setOnClickListener {
            username = binding.tieUsername.text.toString()
            channel = binding.tieChannel.text.toString()

           if (checkSelfPermission()){
               viewModel.joinChannel(channel, username)
           }else{
               sendPermReq()
           }
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
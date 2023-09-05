package com.example.agoratesting.uiActivity.auth

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.agoratesting.databinding.ActivityAuthBinding
import com.example.agoratesting.uiActivity.chat.ChatActivity
import com.example.agoratesting.uiActivity.main.MainActivity
import com.example.agoratesting.utils.chatManager
import com.example.agoratesting.utils.userManager
import com.example.agoratesting.utils.videoManager

class AuthActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAuthBinding
    private lateinit var viewModel: AuthViewModel

    private val ReqID = 22
    private val REQUESTED_PERMISSION =
        arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA)
    private lateinit var username :String
    private lateinit var token :String
    private lateinit var tokenRTC :String
    private lateinit var roomID : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        viewModel.isLoading.observe(this){isLoading ->
            if (isLoading){
                binding.authPB.isVisible = true
                binding.joinBtn.isVisible = false
                binding.tvError.isVisible = false

                binding.TILUsername.isVisible = false
                binding.TILRoomId.isVisible = false
                binding.TILUsertoken.isVisible = false
                binding.TILRtcToken.isVisible = false
            }
            else{
                binding.authPB.isVisible = false
                binding.joinBtn.isVisible = true

                binding.TILUsername.isVisible = true
                binding.TILRoomId.isVisible = true
                binding.TILUsertoken.isVisible = true
                binding.TILRtcToken.isVisible = true
                if (viewModel.errorMSG.isNotEmpty()){
                    binding.tvError.isVisible = true
                    binding.tvError.text = viewModel.errorMSG
                }
            }
        }

        viewModel.joinedMeeting.observe(this){Joined ->
            if (Joined){
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        viewModel.joinedChat.observe(this){Joined ->
            if (Joined){
                startActivity(Intent(this, ChatActivity::class.java))
            }
        }


        binding.joinBtn.setOnClickListener {
            if(checkSelfPermission()){
                username = binding.TIEUsername.text.toString()
                token = binding.TIEUsertoken.text.toString()
                tokenRTC = binding.TIERtcToken.text.toString()
                roomID = binding.TIERoomId.text.toString()

                if (username.isNotBlank() && token.isNotBlank() && tokenRTC.isNotBlank() && roomID.isNotBlank()){
                    userManager.username = username
                    userManager.userToken = token
                    videoManager.rtcToken = tokenRTC
                    chatManager.ROOM_ID = roomID
                }
                viewModel.joinMeeting(userManager.username, userManager.userToken, chatManager.ROOM_ID)
                chatManager.targetID = chatManager.ROOM_ID
            } else{
                sendPermReq()
            }
        }

    }

    private fun sendPermReq() {
        ActivityCompat.requestPermissions(this, REQUESTED_PERMISSION, ReqID)
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
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

class AuthActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAuthBinding
    private lateinit var viewModel: AuthViewModel

    private val ReqID = 22
    private val REQUESTED_PERMISSION =
        arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA)
    private val username = userManager.username
    private val token = userManager.userToken
    private val roomID = chatManager.ROOM_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        viewModel.isLoading.observe(this){isLoading ->
            if (isLoading){
                binding.authPB.isVisible = true
                binding.joinBtn.isVisible = false
                binding.p2pBtn.isVisible = false
            }
            else{
                binding.authPB.isVisible = false
                binding.joinBtn.isVisible = true
                binding.p2pBtn.isVisible = true
                if (viewModel.errorMSG.isNotEmpty()){
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
                viewModel.joinMeeting(username, token, roomID)
                chatManager.targetID = roomID
            } else{
                sendPermReq()
            }
        }

        binding.p2pBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Peer to Peer Chat")
            builder.setMessage("To :")
            val targetUser = EditText(this)
            targetUser.hint = "(Username)"
            builder.setView(targetUser)

            builder.setPositiveButton("Join"){dialog : DialogInterface, which:Int ->
                if (!targetUser.text.isNullOrEmpty()){
                    val targetID = targetUser.text.toString()

                    viewModel.joinChat_1p(username, token, targetID)
                    chatManager.targetID = targetID
                }
            }

            builder.setNegativeButton("Cancel"){dialog: DialogInterface, which:Int ->
                dialog.cancel()
            }
            builder.show()
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
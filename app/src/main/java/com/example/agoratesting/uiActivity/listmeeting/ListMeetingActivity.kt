package com.example.agoratesting.uiActivity.listmeeting

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agoratesting.R
import com.example.agoratesting.data.MeetingRoom
import com.example.agoratesting.databinding.ActivityListMeetingBinding
import com.example.agoratesting.uiActivity.main.MainActivity
import com.example.agoratesting.utils.dummyMeetingList
import io.agora.chat.ChatClient

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

        binding.btnLogout.setOnClickListener {
            ChatClient.getInstance().logout(true)
            finish()
        }

        binding.addMeetingBtn.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_meeting, null)
            AlertDialog.Builder(this)
                .setView(dialogView)
                .setNegativeButton("Cancel"){dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton("Join Meeting"){dialog, which ->
                    val roomID = dialogView.findViewById<EditText>(R.id.room_id)
                    val channelName = dialogView.findViewById<EditText>(R.id.channel_name)
                    val rtcToken = dialogView.findViewById<EditText>(R.id.rtc_token)


                    if (roomID.text.isNotBlank() && channelName.text.isNotBlank() && rtcToken.text.isNotBlank()){
                        val meetingRoom = MeetingRoom(
                            roomID = roomID.text.toString(),
                            channelName = channelName.text.toString(),
                            rtcToken = rtcToken.text.toString()
                        )

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("MeetingDetail", meetingRoom)

                        startActivity(intent)
                    }
                }

                .create()
                .show()
        }
    }

    override fun onBackPressed() {
        if (ChatClient.getInstance().isLoggedIn){
            AlertDialog.Builder(this)
                .setTitle("Close App")
                .setMessage("Close Application ?")
                .setNegativeButton("Cancel"){dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton("Yes"){dialog, which ->
                    finishAffinity()
                    System.exit(0)
                }
                .create()
                .show()
        } else{
            super.onBackPressed()
        }
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
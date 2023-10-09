package com.cexup.meet.uiActivity.listmeeting

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cexup.meet.R
import com.cexup.meet.data.MeetingRoom
import com.cexup.meet.databinding.ActivityListMeetingBinding
import com.cexup.meet.uiActivity.main.MainActivity
import com.cexup.meet.utils.dummyMeetingList
import io.agora.chat.ChatClient
import kotlin.system.exitProcess

class ListMeetingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListMeetingBinding
    private lateinit var adapter : ListMeetingAdapter

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
                    exitProcess(0)
                }
                .create()
                .show()
        } else{
            super.onBackPressed()
        }
    }
}
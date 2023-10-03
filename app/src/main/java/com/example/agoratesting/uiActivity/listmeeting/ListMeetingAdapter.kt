package com.example.agoratesting.uiActivity.listmeeting

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.agoratesting.data.MeetingInfo
import com.example.agoratesting.databinding.ItemListMeetingBinding
import com.example.agoratesting.uiActivity.main.MainActivity

class ListMeetingAdapter : ListAdapter<MeetingInfo, ListMeetingAdapter.ViewHolder>(DiffCallBack) {

    class ViewHolder(private val itemViewBinding: ItemListMeetingBinding): RecyclerView.ViewHolder(itemViewBinding.root) {

        fun onbind(meetingInfo: MeetingInfo){
            itemViewBinding.titleMeetingItem.text = meetingInfo.title
            itemViewBinding.scheduleMeetingItem.text = "${meetingInfo.hari}, ${meetingInfo.tanggal}"

            itemViewBinding.btnJoinMeeting.setOnClickListener {
                val intent = Intent(itemView.context, MainActivity::class.java)
                intent.putExtra("MeetingDetail", meetingInfo.roomMeeting)

                itemView.context.startActivity(intent)
            }

            itemViewBinding.btnPersonalChat.setOnClickListener {
//                val intent = Intent(itemView.context, MainActivity::class.java)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListMeetingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meetingInfo = getItem(position)
        holder.onbind(meetingInfo)
    }
}

object DiffCallBack : DiffUtil.ItemCallback<MeetingInfo>() {
    override fun areItemsTheSame(oldItem: MeetingInfo, newItem: MeetingInfo): Boolean {
        return oldItem === newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: MeetingInfo, newItem: MeetingInfo): Boolean {
        return oldItem == newItem
    }
}
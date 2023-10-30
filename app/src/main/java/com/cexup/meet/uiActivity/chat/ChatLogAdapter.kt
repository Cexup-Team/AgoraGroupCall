package com.cexup.meet.uiActivity.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cexup.meet.data.ChatRTM
import com.cexup.meet.databinding.ItemChatReceivedBinding
import com.cexup.meet.databinding.ItemChatSentBinding
import com.cexup.meet.utils.TempMeeting

class ChatLogAdapter(private val listMessage : List<ChatRTM>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_MESSAGE_SENT = 0
    private val VIEW_TYPE_MESSAGE_RECEIVED = 1

    class SentMessage(private val sentBinding: ItemChatSentBinding) : RecyclerView.ViewHolder(sentBinding.root){
        fun onBind(message : ChatRTM){
            sentBinding.sentUsername.text = message.username
            sentBinding.sentMessage.text = message.message.text
        }

    }

    class ReceivedMessage(private val receivedBinding: ItemChatReceivedBinding) : RecyclerView.ViewHolder(receivedBinding.root){
        fun onBind(message : ChatRTM){
            receivedBinding.receivedUsername.text = message.message.text
            receivedBinding.receivedMessage.text = message.message.text

        }

    }

    override fun getItemViewType(position : Int) : Int{
        val message = listMessage[position]
        return if (message.username == TempMeeting.ListMember[0].username){
            VIEW_TYPE_MESSAGE_SENT
        } else{
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                val binding = ItemChatSentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SentMessage(binding)
            }

            else -> {
                val binding = ItemChatReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ReceivedMessage(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            VIEW_TYPE_MESSAGE_SENT -> {
                (holder as SentMessage).onBind(listMessage[position])
            }

            VIEW_TYPE_MESSAGE_RECEIVED ->{
                (holder as ReceivedMessage).onBind(listMessage[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return listMessage.size
    }
}
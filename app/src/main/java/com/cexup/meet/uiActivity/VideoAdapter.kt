package com.cexup.meet.uiActivity

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cexup.meet.data.AccountInfo
import com.cexup.meet.databinding.ItemVideoBinding

class VideoAdapter: ListAdapter<AccountInfo, VideoAdapter.ViewHolder>(VideoDiffCallBack) {

    class ViewHolder(val itemViewBinding: ItemVideoBinding) : RecyclerView.ViewHolder(itemViewBinding.root){
        fun onBind(account : AccountInfo){
            if (!account.offCam){
                if (account.surfaceView.parent != null){
                    (account.surfaceView.parent as ViewGroup).removeView(account.surfaceView)
                }
                itemViewBinding.videoFrame.addView(account.surfaceView)
            } else{
                val placeholder = ImageView(itemView.context)
                Glide
                    .with(itemView.context)
                    .load("https://ui-avatars.com/api/?background=random&name=${account.username}")
                    .into(placeholder)

                itemViewBinding.videoFrame.addView(placeholder)
            }
        }

        fun unBind(){
            itemViewBinding.videoFrame.removeAllViews()
        }
    }

    fun getItemByTag(uid:Int) : AccountInfo? {

        for (i in 0 until itemCount) {
            val item = getItem(i)
            if(item is AccountInfo && item.uid == uid){
                return item
            }
        }
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val lp = binding.videoFrame.layoutParams
        lp.height = LayoutParams.MATCH_PARENT
        if (itemCount > 2){
            lp.width = parent.measuredWidth / 2
        } else{
            lp.width = LayoutParams.MATCH_PARENT
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val account = getItem(position)
        holder.onBind(account)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.unBind()
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        try {
            val account = getItem(holder.layoutPosition)
            holder.onBind(account)
        } catch (e: Exception){
            //to-do
        }
    }

}

object VideoDiffCallBack : DiffUtil.ItemCallback<AccountInfo>() {
    override fun areItemsTheSame(oldItem: AccountInfo, newItem: AccountInfo): Boolean {
        return oldItem === newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: AccountInfo, newItem: AccountInfo): Boolean {
        return oldItem == newItem
    }
}
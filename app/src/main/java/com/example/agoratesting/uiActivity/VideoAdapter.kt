package com.example.agoratesting.uiActivity

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.agoratesting.databinding.VideoItemBinding

class VideoAdapter : ListAdapter<SurfaceView, VideoAdapter.ViewHolder>(VideoDiffCallBack) {

    class ViewHolder(val itemViewBinding: VideoItemBinding) : RecyclerView.ViewHolder(itemViewBinding.root){
        fun onBind(surface : SurfaceView){
            itemViewBinding.videoFrame.addView(surface)
        }

        fun unBind(){
            itemViewBinding.videoFrame.removeAllViews()
        }
    }

    fun getItemByTag(uid:Int) : SurfaceView? {

        for (i in 0 until itemCount) {
            val item = getItem(i)
            if(item is SurfaceView && item.tag == uid){
                return item
            }
        }
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        val frame = getItem(position)
        holder.onBind(frame)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.unBind()
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        try {
            holder.onBind(getItem(holder.layoutPosition))
        } catch (e: Exception){
            //to-do
        }
    }

}

object VideoDiffCallBack : DiffUtil.ItemCallback<SurfaceView>() {
    override fun areItemsTheSame(oldItem: SurfaceView, newItem: SurfaceView): Boolean {
        return oldItem === newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: SurfaceView, newItem: SurfaceView): Boolean {
        return oldItem == newItem
    }
}
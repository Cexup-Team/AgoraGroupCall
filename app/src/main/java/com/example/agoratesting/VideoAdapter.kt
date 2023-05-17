//package com.example.agoratesting
//
//import android.view.LayoutInflater
//import android.view.SurfaceView
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import io.agora.rtc2.RtcEngine
//import io.agora.rtc2.video.VideoCanvas
//
//class VideoAdapter() :
//    ListAdapter<Int,VideoAdapter.ViewHolder>(VideoDiffCallBack) {
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private var container : FrameLayout = itemView.findViewById(R.id.videoFrame)
//        private var currentContainer: FrameLayout? = null
//
//        init {
//            itemView.setOnClickListener {
//
//            }
//        }
//
//
//        fun bind(frameLayout: FrameLayout) {
//            currentContainer = frameLayout
//            container = frameLayout
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_item,parent,false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val container = getItem(position)
//        holder.bind()
//    }
//}
//
//object VideoDiffCallBack : DiffUtil.ItemCallback<Int>() {
//    override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
//        return oldItem == newItem
//    }
//
//    override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
//        return oldItem == newItem
//    }
//
//}
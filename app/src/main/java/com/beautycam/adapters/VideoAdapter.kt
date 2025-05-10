package com.beautycam.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beautycam.R
import com.google.android.material.imageview.ShapeableImageView

class VideoAdapter : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    private val videos = mutableListOf<VideoItem>()

    fun submitList(newVideos: List<VideoItem>) {
        videos.clear()
        videos.addAll(newVideos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount() = videos.size

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnailView: ImageView = itemView.findViewById(R.id.thumbnailView)
        private val channelImageView: ShapeableImageView = itemView.findViewById(R.id.channelImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val channelNameTextView: TextView = itemView.findViewById(R.id.channelNameTextView)
        private val metaDataTextView: TextView = itemView.findViewById(R.id.metaDataTextView)
        private val menuButton: ImageButton = itemView.findViewById(R.id.menuButton)

        fun bind(video: VideoItem) {
            // Set thumbnail
            thumbnailView.setImageURI(video.thumbnailUri)
            
            // Set channel image
            channelImageView.setImageURI(video.channelImageUri)
            
            // Set text fields
            titleTextView.text = video.title
            channelNameTextView.text = video.channelName
            metaDataTextView.text = "${video.views} views • ${video.timeAgo}"
            
            // Set menu click listener
            menuButton.setOnClickListener {
                // Handle menu click
            }
        }
    }

    data class VideoItem(
        val thumbnailUri: android.net.Uri,
        val channelImageUri: android.net.Uri,
        val title: String,
        val channelName: String,
        val views: String,
        val timeAgo: String
    )
}

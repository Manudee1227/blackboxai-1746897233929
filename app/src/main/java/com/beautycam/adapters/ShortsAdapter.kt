package com.beautycam.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.beautycam.R

class ShortsAdapter : RecyclerView.Adapter<ShortsAdapter.ShortViewHolder>() {
    private val shorts = mutableListOf<ShortItem>()

    fun submitList(newShorts: List<ShortItem>) {
        shorts.clear()
        shorts.addAll(newShorts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_short, parent, false)
        return ShortViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShortViewHolder, position: Int) {
        holder.bind(shorts[position])
    }

    override fun getItemCount() = shorts.size

    class ShortViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val videoView: VideoView = itemView.findViewById(R.id.videoView)
        private val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)
        private val likesCount: TextView = itemView.findViewById(R.id.likesCount)
        private val commentButton: ImageButton = itemView.findViewById(R.id.commentButton)
        private val commentsCount: TextView = itemView.findViewById(R.id.commentsCount)
        private val shareButton: ImageButton = itemView.findViewById(R.id.shareButton)
        private val shareCount: TextView = itemView.findViewById(R.id.shareCount)

        fun bind(short: ShortItem) {
            // Set video
            videoView.setVideoURI(short.videoUri)
            videoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                videoView.start()
            }

            // Set counts
            likesCount.text = short.likes.toString()
            commentsCount.text = short.comments.toString()
            shareCount.text = "Share"

            // Set click listeners
            likeButton.setOnClickListener {
                // Handle like action
            }

            commentButton.setOnClickListener {
                // Handle comment action
            }

            shareButton.setOnClickListener {
                // Handle share action
            }
        }
    }

    data class ShortItem(
        val videoUri: Uri,
        val likes: Int,
        val comments: Int
    )
}

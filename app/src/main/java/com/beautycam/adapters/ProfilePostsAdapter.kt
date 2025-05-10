package com.beautycam.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beautycam.R
import com.beautycam.models.Post
import com.bumptech.glide.Glide

class ProfilePostsAdapter(
    private val onPostClick: (Post) -> Unit
) : ListAdapter<Post, ProfilePostsAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postImage: ImageView = itemView.findViewById(R.id.postImage)
        private val videoIndicator: ImageView = itemView.findViewById(R.id.videoIndicator)
        private val multiplePhotosIndicator: ImageView = itemView.findViewById(R.id.multiplePhotosIndicator)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPostClick(getItem(position))
                }
            }
        }

        fun bind(post: Post) {
            // Load the first image or thumbnail
            val imageUrl = when {
                post.type == Post.PostType.VIDEO && post.thumbnailUrl != null -> post.thumbnailUrl
                post.imageUrls.isNotEmpty() -> post.imageUrls.first()
                else -> null
            }

            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_video_placeholder)
                .error(R.drawable.ic_video_placeholder)
                .centerCrop()
                .into(postImage)

            // Show appropriate indicators
            videoIndicator.visibility = if (post.type == Post.PostType.VIDEO) View.VISIBLE else View.GONE
            multiplePhotosIndicator.visibility = if (post.type == Post.PostType.MULTIPLE_PHOTOS) View.VISIBLE else View.GONE
        }
    }

    private class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}

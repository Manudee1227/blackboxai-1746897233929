package com.beautycam.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beautycam.R
import com.beautycam.models.Message
import com.beautycam.models.User
import com.bumptech.glide.Glide
import java.util.Date

class ChatAdapter(
    private val currentUserId: String,
    private val otherUser: User,
    private val onImageClick: (String) -> Unit
) : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderId == currentUserId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_message_sent, parent, false)
                SentMessageViewHolder(view)
            }
            VIEW_TYPE_RECEIVED -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_message_received, parent, false)
                ReceivedMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message, otherUser)
        }
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        private val timestampText: TextView = itemView.findViewById(R.id.timestampText)
        private val mediaImage: ImageView = itemView.findViewById(R.id.mediaImage)

        fun bind(message: Message) {
            messageText.text = message.content
            timestampText.text = getRelativeTimeSpan(message.timestamp)

            // Handle media content
            if (message.type == Message.MessageType.IMAGE && !message.mediaUrl.isNullOrEmpty()) {
                mediaImage.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(message.mediaUrl)
                    .into(mediaImage)
                mediaImage.setOnClickListener { onImageClick(message.mediaUrl) }
            } else {
                mediaImage.visibility = View.GONE
            }
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        private val timestampText: TextView = itemView.findViewById(R.id.timestampText)
        private val mediaImage: ImageView = itemView.findViewById(R.id.mediaImage)
        private val usernameText: TextView = itemView.findViewById(R.id.usernameText)

        fun bind(message: Message, user: User) {
            messageText.text = message.content
            timestampText.text = getRelativeTimeSpan(message.timestamp)

            // Load profile image
            Glide.with(itemView.context)
                .load(user.profileImageUrl)
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .circleCrop()
                .into(profileImage)

            // Handle media content
            if (message.type == Message.MessageType.IMAGE && !message.mediaUrl.isNullOrEmpty()) {
                mediaImage.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(message.mediaUrl)
                    .into(mediaImage)
                mediaImage.setOnClickListener { onImageClick(message.mediaUrl) }
            } else {
                mediaImage.visibility = View.GONE
            }

            // Show username in group chats (if needed)
            usernameText.visibility = View.GONE
        }
    }

    private fun getRelativeTimeSpan(date: Date): String {
        return DateUtils.getRelativeTimeSpanString(
            date.time,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        ).toString()
    }

    private class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}

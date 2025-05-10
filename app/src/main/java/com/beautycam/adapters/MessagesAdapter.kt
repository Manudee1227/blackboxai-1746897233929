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

class MessagesAdapter(
    private val onMessageClick: (String) -> Unit
) : ListAdapter<MessageThread, MessagesAdapter.MessageViewHolder>(MessageDiffCallback()) {

    data class MessageThread(
        val otherUser: User,
        val lastMessage: Message,
        val unreadCount: Int,
        val isOnline: Boolean
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        private val usernameText: TextView = itemView.findViewById(R.id.usernameText)
        private val lastMessageText: TextView = itemView.findViewById(R.id.lastMessageText)
        private val timestampText: TextView = itemView.findViewById(R.id.timestampText)
        private val unreadCountBadge: TextView = itemView.findViewById(R.id.unreadCountBadge)
        private val onlineStatusIndicator: View = itemView.findViewById(R.id.onlineStatusIndicator)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMessageClick(getItem(position).otherUser.id)
                }
            }
        }

        fun bind(thread: MessageThread) {
            // Load profile image
            Glide.with(itemView.context)
                .load(thread.otherUser.profileImageUrl)
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .circleCrop()
                .into(profileImage)

            // Set user info
            usernameText.text = thread.otherUser.username
            lastMessageText.text = thread.lastMessage.content
            timestampText.text = getRelativeTimeSpan(thread.lastMessage.timestamp)

            // Show/hide unread count
            if (thread.unreadCount > 0) {
                unreadCountBadge.visibility = View.VISIBLE
                unreadCountBadge.text = if (thread.unreadCount > 99) "99+" else thread.unreadCount.toString()
            } else {
                unreadCountBadge.visibility = View.GONE
            }

            // Show/hide online status
            onlineStatusIndicator.visibility = if (thread.isOnline) View.VISIBLE else View.GONE
        }

        private fun getRelativeTimeSpan(date: Date): String {
            return DateUtils.getRelativeTimeSpanString(
                date.time,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString()
        }
    }

    private class MessageDiffCallback : DiffUtil.ItemCallback<MessageThread>() {
        override fun areItemsTheSame(oldItem: MessageThread, newItem: MessageThread): Boolean {
            return oldItem.otherUser.id == newItem.otherUser.id
        }

        override fun areContentsTheSame(oldItem: MessageThread, newItem: MessageThread): Boolean {
            return oldItem == newItem
        }
    }
}

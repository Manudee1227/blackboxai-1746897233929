package com.beautycam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beautycam.R
import com.beautycam.adapters.ChatAdapter
import com.beautycam.models.Message
import com.beautycam.models.User
import com.bumptech.glide.Glide
import java.util.Date

class ChatFragment : Fragment() {
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var attachButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var profileImage: ImageView
    private lateinit var usernameText: TextView
    private lateinit var onlineStatusText: TextView

    private var otherUser: User? = null
    private val currentUserId = "current_user" // TODO: Replace with actual user ID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get user data from arguments
        arguments?.let {
            // TODO: Get user data from arguments
            otherUser = User(
                id = "other_user",
                username = "john_doe",
                displayName = "John Doe",
                profileImageUrl = ""
            )
        }

        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        loadMessages()
        updateUserInfo()
    }

    private fun initializeViews(view: View) {
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        messageInput = view.findViewById(R.id.messageInput)
        sendButton = view.findViewById(R.id.sendButton)
        attachButton = view.findViewById(R.id.attachButton)
        backButton = view.findViewById(R.id.backButton)
        profileImage = view.findViewById(R.id.profileImage)
        usernameText = view.findViewById(R.id.usernameText)
        onlineStatusText = view.findViewById(R.id.onlineStatusText)
    }

    private fun setupRecyclerView() {
        otherUser?.let { user ->
            chatAdapter = ChatAdapter(
                currentUserId = currentUserId,
                otherUser = user,
                onImageClick = { imageUrl ->
                    // TODO: Show full-screen image viewer
                }
            )
        }

        chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            // TODO: Navigate back
        }

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageInput.text.clear()
            }
        }

        attachButton.setOnClickListener {
            // TODO: Show attachment options
        }
    }

    private fun loadMessages() {
        // TODO: Replace with actual messages from backend
        val mockMessages = listOf(
            Message(
                id = "1",
                senderId = "other_user",
                receiverId = currentUserId,
                content = "Hey, how are you?",
                timestamp = Date(System.currentTimeMillis() - 3600000)
            ),
            Message(
                id = "2",
                senderId = currentUserId,
                receiverId = "other_user",
                content = "I'm good, thanks! How about you?",
                timestamp = Date(System.currentTimeMillis() - 3000000)
            )
        )

        chatAdapter.submitList(mockMessages)
        chatRecyclerView.scrollToPosition(mockMessages.size - 1)
    }

    private fun updateUserInfo() {
        otherUser?.let { user ->
            usernameText.text = user.username
            onlineStatusText.text = "Online" // TODO: Get actual online status

            Glide.with(this)
                .load(user.profileImageUrl)
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .circleCrop()
                .into(profileImage)
        }
    }

    private fun sendMessage(content: String) {
        // TODO: Send message to backend
        val newMessage = Message(
            id = System.currentTimeMillis().toString(),
            senderId = currentUserId,
            receiverId = otherUser?.id ?: "",
            content = content,
            timestamp = Date()
        )

        val currentList = chatAdapter.currentList.toMutableList()
        currentList.add(newMessage)
        chatAdapter.submitList(currentList) {
            chatRecyclerView.scrollToPosition(currentList.size - 1)
        }
    }

    companion object {
        fun newInstance(userId: String) = ChatFragment().apply {
            arguments = Bundle().apply {
                putString("userId", userId)
            }
        }
    }
}

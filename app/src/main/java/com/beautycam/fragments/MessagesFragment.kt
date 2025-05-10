package com.beautycam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beautycam.R
import com.beautycam.adapters.MessagesAdapter
import com.beautycam.models.Message
import com.beautycam.models.User
import java.util.Date

class MessagesFragment : Fragment() {
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var emptyStateContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupRecyclerView()
        setupSearch()
        loadMessages()
    }

    private fun initializeViews(view: View) {
        messagesRecyclerView = view.findViewById(R.id.messagesRecyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)
        emptyStateContainer = view.findViewById(R.id.emptyStateContainer)

        view.findViewById<View>(R.id.newMessageButton).setOnClickListener {
            // TODO: Navigate to new message screen
        }
    }

    private fun setupRecyclerView() {
        messagesAdapter = MessagesAdapter { userId ->
            // TODO: Navigate to chat screen with userId
        }

        messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = messagesAdapter
        }
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener { text ->
            // TODO: Filter messages based on search text
            filterMessages(text?.toString() ?: "")
        }
    }

    private fun loadMessages() {
        // TODO: Replace with actual data from backend
        val mockThreads = listOf(
            MessagesAdapter.MessageThread(
                otherUser = User(
                    id = "1",
                    username = "john_doe",
                    displayName = "John Doe",
                    profileImageUrl = ""
                ),
                lastMessage = Message(
                    id = "1",
                    senderId = "1",
                    receiverId = "current_user",
                    content = "Hey, how are you?",
                    timestamp = Date()
                ),
                unreadCount = 2,
                isOnline = true
            ),
            MessagesAdapter.MessageThread(
                otherUser = User(
                    id = "2",
                    username = "jane_smith",
                    displayName = "Jane Smith",
                    profileImageUrl = ""
                ),
                lastMessage = Message(
                    id = "2",
                    senderId = "current_user",
                    receiverId = "2",
                    content = "Thanks for the photos!",
                    timestamp = Date(System.currentTimeMillis() - 3600000)
                ),
                unreadCount = 0,
                isOnline = false
            )
        )

        updateMessages(mockThreads)
    }

    private fun updateMessages(threads: List<MessagesAdapter.MessageThread>) {
        if (threads.isEmpty()) {
            messagesRecyclerView.visibility = View.GONE
            emptyStateContainer.visibility = View.VISIBLE
        } else {
            messagesRecyclerView.visibility = View.VISIBLE
            emptyStateContainer.visibility = View.GONE
            messagesAdapter.submitList(threads)
        }
    }

    private fun filterMessages(query: String) {
        // TODO: Implement message filtering
    }

    companion object {
        fun newInstance() = MessagesFragment()
    }
}

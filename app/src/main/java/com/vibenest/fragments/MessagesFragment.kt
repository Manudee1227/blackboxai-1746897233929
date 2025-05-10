package com.vibenest.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vibenest.R
import com.vibenest.databinding.FragmentMessagesBinding
import com.vibenest.firebase.FirebaseManager
import com.vibenest.models.Chat

class MessagesFragment : Fragment() {
    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private val firebaseManager = FirebaseManager.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadChats()
    }

    private fun setupViews() {
        try {
            // Setup toolbar
            binding.toolbar.apply {
                title = getString(R.string.title_messages)
                inflateMenu(R.menu.messages_menu)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_new_chat -> {
                            // Handle new chat
                            true
                        }
                        else -> false
                    }
                }
            }

            // Setup RecyclerView
            binding.chatsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
            }

            // Setup refresh
            binding.swipeRefresh.setOnRefreshListener {
                loadChats()
            }

            // Setup retry button
            binding.retryButton.setOnClickListener {
                loadChats()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up views", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun loadChats() {
        try {
            binding.swipeRefresh.isRefreshing = true

            firebaseManager.getCurrentUser()?.let { user ->
                firebaseManager.getChats { chats, error ->
                    binding.swipeRefresh.isRefreshing = false

                    if (error != null) {
                        Log.e(TAG, "Error loading chats: $error")
                        showError()
                        return@getChats
                    }

                    if (chats.isEmpty()) {
                        showEmptyState()
                    } else {
                        showContent(chats)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading chats", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            binding.swipeRefresh.isRefreshing = false
            showError()
        }
    }

    private fun showContent(chats: List<Chat>) {
        binding.contentGroup.visibility = View.VISIBLE
        binding.emptyGroup.visibility = View.GONE
        binding.errorGroup.visibility = View.GONE

        // Update RecyclerView with chats
    }

    private fun showEmptyState() {
        binding.contentGroup.visibility = View.GONE
        binding.emptyGroup.visibility = View.VISIBLE
        binding.errorGroup.visibility = View.GONE
    }

    private fun showError() {
        binding.contentGroup.visibility = View.GONE
        binding.emptyGroup.visibility = View.GONE
        binding.errorGroup.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "MessagesFragment"
    }
}

package com.vibenest.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vibenest.R
import com.vibenest.databinding.FragmentHomeBinding
import com.vibenest.firebase.FirebaseManager

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val firebaseManager = FirebaseManager.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadContent()
    }

    private fun setupViews() {
        try {
            // Setup toolbar
            binding.toolbar.apply {
                title = getString(R.string.app_name)
                inflateMenu(R.menu.home_menu)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_search -> {
                            // Handle search
                            true
                        }
                        R.id.action_notifications -> {
                            // Handle notifications
                            true
                        }
                        else -> false
                    }
                }
            }

            // Setup refresh
            binding.swipeRefresh.setOnRefreshListener {
                loadContent()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up views", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun loadContent() {
        try {
            binding.swipeRefresh.isRefreshing = true

            firebaseManager.getCurrentUser()?.let { user ->
                // Load feed content
                firebaseManager.getFeed { posts, error ->
                    binding.swipeRefresh.isRefreshing = false

                    if (error != null) {
                        Log.e(TAG, "Error loading feed: $error")
                        showError()
                        return@getFeed
                    }

                    if (posts.isEmpty()) {
                        showEmptyState()
                    } else {
                        showContent()
                        // Update RecyclerView with posts
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading content", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            binding.swipeRefresh.isRefreshing = false
            showError()
        }
    }

    private fun showContent() {
        binding.contentGroup.visibility = View.VISIBLE
        binding.emptyGroup.visibility = View.GONE
        binding.errorGroup.visibility = View.GONE
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
        private const val TAG = "HomeFragment"
    }
}

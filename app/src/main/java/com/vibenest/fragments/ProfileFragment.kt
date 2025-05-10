package com.vibenest.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vibenest.R
import com.vibenest.databinding.FragmentProfileBinding
import com.vibenest.firebase.FirebaseManager
import com.vibenest.models.User

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val firebaseManager = FirebaseManager.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadProfile()
    }

    private fun setupViews() {
        try {
            // Setup toolbar
            binding.toolbar.apply {
                inflateMenu(R.menu.profile_menu)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_settings -> {
                            // Handle settings
                            true
                        }
                        R.id.action_logout -> {
                            logout()
                            true
                        }
                        else -> false
                    }
                }
            }

            // Setup posts grid
            binding.postsRecyclerView.apply {
                layoutManager = GridLayoutManager(context, 3)
                setHasFixedSize(true)
            }

            // Setup refresh
            binding.swipeRefresh.setOnRefreshListener {
                loadProfile()
            }

            // Setup retry button
            binding.retryButton.setOnClickListener {
                loadProfile()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up views", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun loadProfile() {
        try {
            binding.swipeRefresh.isRefreshing = true

            firebaseManager.getCurrentUser()?.let { user ->
                firebaseManager.getUserProfile(user.uid) { profile, error ->
                    binding.swipeRefresh.isRefreshing = false

                    if (error != null) {
                        Log.e(TAG, "Error loading profile: $error")
                        showError()
                        return@getUserProfile
                    }

                    profile?.let {
                        showContent(it)
                    } ?: showError()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading profile", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            binding.swipeRefresh.isRefreshing = false
            showError()
        }
    }

    private fun showContent(user: User) {
        binding.contentGroup.visibility = View.VISIBLE
        binding.errorGroup.visibility = View.GONE

        // Update profile info
        binding.toolbar.title = user.username
        binding.usernameTv.text = user.username
        binding.bioTv.text = user.bio
        binding.postCountTv.text = user.postCount.toString()
        binding.followerCountTv.text = user.followerCount.toString()
        binding.followingCountTv.text = user.followingCount.toString()

        // Load profile image
        Glide.with(this)
            .load(user.profileImageUrl)
            .placeholder(R.drawable.ic_account_circle)
            .circleCrop()
            .into(binding.profileImage)

        // Load posts
        loadPosts(user.id)
    }

    private fun loadPosts(userId: String) {
        firebaseManager.getUserPosts(userId) { posts, error ->
            if (error != null) {
                Log.e(TAG, "Error loading posts: $error")
                return@getUserPosts
            }

            // Update RecyclerView with posts
        }
    }

    private fun showError() {
        binding.contentGroup.visibility = View.GONE
        binding.errorGroup.visibility = View.VISIBLE
    }

    private fun logout() {
        try {
            firebaseManager.signOut { success ->
                if (success) {
                    activity?.finish()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}

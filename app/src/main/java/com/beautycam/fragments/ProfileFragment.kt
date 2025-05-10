package com.beautycam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.beautycam.R
import com.beautycam.adapters.ProfilePostsAdapter
import com.beautycam.models.Post
import com.beautycam.models.User
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import java.util.Date

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var usernameText: TextView
    private lateinit var bioText: TextView
    private lateinit var postsCount: TextView
    private lateinit var followersCount: TextView
    private lateinit var followingCount: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var postsAdapter: ProfilePostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupTabLayout()
        setupViewPager()
        setupClickListeners()
        loadUserData()
        loadUserPosts()
    }

    private fun initializeViews(view: View) {
        profileImage = view.findViewById(R.id.profileImage)
        usernameText = view.findViewById(R.id.usernameText)
        bioText = view.findViewById(R.id.bioText)
        postsCount = view.findViewById(R.id.postsCount)
        followersCount = view.findViewById(R.id.followersCount)
        followingCount = view.findViewById(R.id.followingCount)
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        // Initialize RecyclerView and adapter
        postsAdapter = ProfilePostsAdapter { post ->
            // Handle post click
            // TODO: Navigate to post detail
        }
    }

    private fun setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_grid_view))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_video))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tagged))
    }

    private fun setupViewPager() {
        viewPager.adapter = postsAdapter
        
        // Link TabLayout with ViewPager2
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab?.position ?: 0
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

    private fun setupClickListeners() {
        view?.findViewById<View>(R.id.editProfileButton)?.setOnClickListener {
            // TODO: Navigate to edit profile
        }

        view?.findViewById<View>(R.id.messageButton)?.setOnClickListener {
            // TODO: Navigate to messages
        }

        view?.findViewById<View>(R.id.followersContainer)?.setOnClickListener {
            // TODO: Navigate to followers list
        }

        view?.findViewById<View>(R.id.followingContainer)?.setOnClickListener {
            // TODO: Navigate to following list
        }
    }

    private fun loadUserData() {
        // TODO: Replace with actual user data from backend
        val mockUser = User(
            id = "1",
            username = "johndoe",
            displayName = "John Doe",
            bio = "Photography enthusiast | Travel lover",
            profileImageUrl = "",
            postsCount = 42,
            followersCount = 1234,
            followingCount = 567
        )

        updateUI(mockUser)
    }

    private fun loadUserPosts() {
        // TODO: Replace with actual posts from backend
        val mockPosts = listOf(
            Post(
                id = "1",
                userId = "1",
                caption = "Beautiful sunset",
                imageUrls = listOf("https://example.com/image1.jpg"),
                timestamp = Date(),
                type = Post.PostType.PHOTO
            ),
            Post(
                id = "2",
                userId = "1",
                caption = "My first video",
                videoUrl = "https://example.com/video1.mp4",
                thumbnailUrl = "https://example.com/thumb1.jpg",
                timestamp = Date(),
                type = Post.PostType.VIDEO
            )
        )

        postsAdapter.submitList(mockPosts)
    }

    private fun updateUI(user: User) {
        usernameText.text = user.username
        bioText.text = user.bio
        postsCount.text = user.postsCount.toString()
        followersCount.text = user.followersCount.toString()
        followingCount.text = user.followingCount.toString()

        if (user.profileImageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(user.profileImageUrl)
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .circleCrop()
                .into(profileImage)
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}

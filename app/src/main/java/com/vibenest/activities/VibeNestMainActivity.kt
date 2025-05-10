package com.vibenest.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vibenest.R
import com.vibenest.ads.AdManager
import com.vibenest.databinding.ActivityMainBinding
import com.vibenest.firebase.FirebaseManager

class VibeNestMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val firebaseManager = FirebaseManager.getInstance()
    private val adManager = AdManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupAds()
    }

    private fun setupNavigation() {
        try {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            
            // Setup Bottom Navigation
            findViewById<BottomNavigationView>(R.id.bottom_navigation)
                .setupWithNavController(navController)

            // Handle navigation item reselection
            binding.bottomNavigation.setOnItemReselectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        // Scroll to top or refresh
                    }
                    R.id.navigation_messages -> {
                        // Scroll to top or refresh
                    }
                    R.id.navigation_profile -> {
                        // Scroll to top or refresh
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up navigation", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun setupAds() {
        try {
            // Show interstitial ad after some user interaction
            adManager.showInterstitialAd(this) {
                Log.d(TAG, "Interstitial ad closed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up ads", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onResume() {
        super.onResume()
        updateUserStatus(true)
    }

    override fun onPause() {
        super.onPause()
        updateUserStatus(false)
    }

    private fun updateUserStatus(online: Boolean) {
        try {
            firebaseManager.getCurrentUser()?.let { user ->
                firebaseManager.updateUserProfile(
                    mapOf("isOnline" to online)
                ) { success, error ->
                    if (!success) {
                        Log.e(TAG, "Failed to update online status: $error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user status", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    companion object {
        private const val TAG = "VibeNestMainActivity"
    }
}

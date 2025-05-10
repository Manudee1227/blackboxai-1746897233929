package com.beautycam.activities

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.beautycam.R
import com.beautycam.ads.AdManager
import com.beautycam.fragments.HomeFragment
import com.beautycam.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class VibeNestMainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var adContainer: FrameLayout
    private var adCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_new)

        initViews()
        setupBottomNav()
        setupAds()
        
        // Load initial fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun initViews() {
        bottomNav = findViewById(R.id.bottomNav)
        adContainer = findViewById(R.id.adContainer)
    }

    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { menuItem ->
            // Increment counter for each navigation action
            adCounter++
            
            // Show interstitial ad every 5 navigation actions
            if (adCounter % 5 == 0) {
                AdManager.getInstance().showInterstitialAd(this) {
                    // Load next interstitial ad after showing
                    AdManager.getInstance().loadInterstitialAd(this)
                }
            }
            
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                R.id.nav_camera -> {
                    // Show rewarded ad before accessing premium camera features
                    AdManager.getInstance().showRewardedAd(
                        this,
                        onRewarded = {
                            // Handle premium feature access
                            openCameraFeatures()
                        }
                    ) {
                        // Load next rewarded ad after showing
                        AdManager.getInstance().loadRewardedAd(this)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setupAds() {
        // Load and show banner ad
        AdManager.getInstance().loadBannerAd(this, adContainer)
        
        // Pre-load interstitial and rewarded ads
        AdManager.getInstance().loadInterstitialAd(this)
        AdManager.getInstance().loadRewardedAd(this)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun openCameraFeatures() {
        // TODO: Implement camera features access
        // This will be called after user watches rewarded ad
    }

    override fun onDestroy() {
        super.onDestroy()
        AdManager.getInstance().destroy()
    }
}

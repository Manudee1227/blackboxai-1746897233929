package com.beautycam.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.beautycam.LoginActivity
import com.beautycam.R
import com.beautycam.VibeNestMainActivity
import com.beautycam.utils.PreferenceManager

class MainSplashActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var logoImage: ImageView
    private lateinit var appNameText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_new)

        preferenceManager = PreferenceManager(this)
        
        initViews()
        startSplashAnimations()
    }

    private fun initViews() {
        logoImage = findViewById(R.id.logoImage)
        appNameText = findViewById(R.id.appNameText)
    }

    private fun startSplashAnimations() {
        // Start animations
        logoImage.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.logo_enter))
        appNameText.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.text_fade_in))

        // Check login state after splash delay
        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginState()
        }, SPLASH_DELAY)
    }

    private fun checkLoginState() {
        // Hide splash screen elements with fade out animation
        logoImage.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_out))
        appNameText.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_out))
        
        Handler(Looper.getMainLooper()).postDelayed({
            logoImage.visibility = View.GONE
            appNameText.visibility = View.GONE
            
            when {
                // If user is logged in, go to main activity
                preferenceManager.getBoolean("is_logged_in", false) -> {
                    startActivity(Intent(this, VibeNestMainActivity::class.java))
                }
                // If not logged in, go to login activity
                else -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }, ANIMATION_DURATION)
    }

    companion object {
        private const val SPLASH_DELAY = 2000L
        private const val ANIMATION_DURATION = 500L
    }
}

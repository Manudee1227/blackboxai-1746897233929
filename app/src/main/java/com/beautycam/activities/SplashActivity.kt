package com.beautycam.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.beautycam.R
import com.beautycam.VibeNestMainActivity
import com.beautycam.fragments.TermsAndConditionsFragment
import com.beautycam.utils.PreferenceManager

class SplashActivity : AppCompatActivity() {

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

        // Check terms acceptance after splash delay
        Handler(Looper.getMainLooper()).postDelayed({
            if (preferenceManager.isTermsAccepted()) {
                proceedToMainActivity()
            } else {
                showTermsAndConditions()
            }
        }, SPLASH_DELAY)
    }

    private fun showTermsAndConditions() {
        // Hide splash screen elements with fade out animation
        logoImage.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_out))
        appNameText.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_out))
        
        Handler(Looper.getMainLooper()).postDelayed({
            logoImage.visibility = View.GONE
            appNameText.visibility = View.GONE
            
            val termsFragment = TermsAndConditionsFragment.newInstance().apply {
                setOnTermsAcceptedListener {
                    proceedToMainActivity()
                }
            }

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                add(R.id.fragmentContainer, termsFragment)
            }
        }, ANIMATION_DURATION)
    }

    private fun proceedToMainActivity() {
        startActivity(Intent(this, VibeNestMainActivity::class.java))
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    companion object {
        private const val SPLASH_DELAY = 2000L
        private const val ANIMATION_DURATION = 500L
    }
}

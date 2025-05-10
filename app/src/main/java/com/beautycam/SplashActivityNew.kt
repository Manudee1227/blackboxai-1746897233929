package com.beautycam

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.beautycam.fragments.TermsAndConditionsFragment
import com.beautycam.utils.PreferenceManager

class SplashActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        preferenceManager = PreferenceManager(this)

        val logoImage = findViewById<ImageView>(R.id.logoImage)
        val appNameText = findViewById<TextView>(R.id.appNameText)

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
        // Hide splash screen elements
        findViewById<ImageView>(R.id.logoImage).visibility = View.GONE
        findViewById<TextView>(R.id.appNameText).visibility = View.GONE

        val termsFragment = TermsAndConditionsFragment.newInstance().apply {
            setOnTermsAcceptedListener {
                proceedToMainActivity()
            }
        }

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragmentContainer, termsFragment)
        }
    }

    private fun proceedToMainActivity() {
        startActivity(Intent(this, VibeNestMainActivity::class.java))
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    companion object {
        private const val SPLASH_DELAY = 2000L
    }
}

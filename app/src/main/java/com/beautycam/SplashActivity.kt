package com.beautycam

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.beautycam.transitions.TransitionManager

class SplashActivity : AppCompatActivity(), Animation.AnimationListener {

    private lateinit var logoImage: ImageView
    private lateinit var appNameText: TextView
    private lateinit var taglineText: TextView
    private lateinit var pulseEffect: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize views
        logoImage = findViewById(R.id.logoImage)
        appNameText = findViewById(R.id.appNameText)
        taglineText = findViewById(R.id.taglineText)
        pulseEffect = findViewById(R.id.pulseEffect)

        // Set initial visibility
        appNameText.visibility = View.INVISIBLE
        taglineText.visibility = View.INVISIBLE
        pulseEffect.visibility = View.INVISIBLE

        // Start logo animation
        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_enter)
        logoAnim.setAnimationListener(this)
        logoImage.startAnimation(logoAnim)
    }

    override fun onAnimationEnd(animation: Animation?) {
        // Start text animations after logo animation ends
        appNameText.visibility = View.VISIBLE
        val appNameAnim = AnimationUtils.loadAnimation(this, R.anim.text_fade_in)
        appNameAnim.startOffset = 200
        appNameText.startAnimation(appNameAnim)

        taglineText.visibility = View.VISIBLE
        val taglineAnim = AnimationUtils.loadAnimation(this, R.anim.text_fade_in)
        taglineAnim.startOffset = 400
        taglineText.startAnimation(taglineAnim)

        // Start pulse effect
        pulseEffect.visibility = View.VISIBLE
        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)
        pulseEffect.startAnimation(pulseAnim)

        // Navigate to main activity after delay with transition
        logoImage.postDelayed({
            val intent = Intent(this, VibeNestMainActivity::class.java)
            val options = TransitionManager.createSplashTransition(
                this,
                logoImage,
                appNameText,
                taglineText
            )
            TransitionManager.startActivityWithTransition(this, intent, options)
            TransitionManager.finishActivityWithTransition(this)
        }, 2500)
    }

    override fun onAnimationStart(animation: Animation?) {
        // Not needed
    }

    override fun onAnimationRepeat(animation: Animation?) {
        // Not needed
    }

    override fun onDestroy() {
        // Clean up animations
        logoImage.clearAnimation()
        appNameText.clearAnimation()
        taglineText.clearAnimation()
        pulseEffect.clearAnimation()
        super.onDestroy()
    }
}

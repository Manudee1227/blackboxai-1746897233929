package com.beautycam

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.transition.Transition
import android.transition.TransitionInflater

class VibeNestMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable activity transitions
        window.requestFeature(android.view.Window.FEATURE_ACTIVITY_TRANSITIONS)
        window.requestFeature(android.view.Window.FEATURE_CONTENT_TRANSITIONS)
        
        // Set enter and return transitions
        window.enterTransition = TransitionInflater.from(this)
            .inflateTransition(R.anim.activity_transition)
        window.returnTransition = TransitionInflater.from(this)
            .inflateTransition(R.anim.activity_transition)
            
        setContentView(R.layout.activity_main)

        // Find shared elements
        val logoImage = findViewById<ImageView>(R.id.mainLogoImage)
        val appNameText = findViewById<TextView>(R.id.mainAppNameText)
        val taglineText = findViewById<TextView>(R.id.mainTaglineText)

        // Set transition names to match splash screen
        logoImage.transitionName = "logo_transition"
        appNameText.transitionName = "app_name_transition"
        taglineText.transitionName = "tagline_transition"

        // Add transition listener to animate other UI elements after transition
        window.enterTransition?.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                // Animate other UI elements here
                // Remove listener to prevent memory leaks
                window.enterTransition?.removeListener(this)
            }

            override fun onTransitionStart(transition: Transition) {}
            override fun onTransitionCancel(transition: Transition) {}
            override fun onTransitionPause(transition: Transition) {}
            override fun onTransitionResume(transition: Transition) {}
        })
    }

    override fun onBackPressed() {
        // Use custom exit transition when back button is pressed
        finishAfterTransition()
    }
}

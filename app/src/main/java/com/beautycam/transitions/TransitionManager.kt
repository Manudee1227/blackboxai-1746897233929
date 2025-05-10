package com.beautycam.transitions

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.beautycam.R

object TransitionManager {
    
    fun createSplashTransition(
        activity: Activity,
        logo: View,
        appName: View,
        tagline: View
    ): ActivityOptionsCompat {
        return ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity,
            Pair(logo, "logo_transition"),
            Pair(appName, "app_name_transition"),
            Pair(tagline, "tagline_transition")
        )
    }

    fun startActivityWithTransition(
        activity: Activity,
        intent: Intent,
        options: ActivityOptionsCompat
    ) {
        activity.window.exitTransition = android.transition.TransitionInflater.from(activity)
            .inflateTransition(R.anim.activity_transition)
        activity.startActivity(intent, options.toBundle())
    }

    fun finishActivityWithTransition(activity: Activity) {
        activity.finishAfterTransition()
    }
}

package com.vibenest

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vibenest.ads.AdManager
import com.vibenest.firebase.FirebaseManager

class VibeNestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase initialized successfully")

            // Initialize Crashlytics
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            Log.d(TAG, "Crashlytics initialized successfully")

            // Initialize Firebase Manager
            FirebaseManager.getInstance()
            Log.d(TAG, "FirebaseManager initialized successfully")

            // Initialize AdMob
            AdManager.getInstance().initialize(this)
            Log.d(TAG, "AdMob initialized successfully")

            // Pre-load interstitial ad for better user experience
            AdManager.getInstance().loadInterstitialAd(this)
            Log.d(TAG, "Started loading interstitial ad")

            // Get FCM token for push notifications
            FirebaseManager.getInstance().getFCMToken { token, error ->
                token?.let { fcmToken ->
                    Log.d(TAG, "FCM token retrieved: ${fcmToken.take(10)}...")
                    // Save FCM token to user profile if needed
                    if (FirebaseManager.getInstance().getCurrentUser() != null) {
                        FirebaseManager.getInstance().updateUserProfile(
                            mapOf("fcmToken" to fcmToken)
                        ) { success, updateError ->
                            if (success) {
                                Log.d(TAG, "FCM token updated in user profile")
                            } else {
                                Log.e(TAG, "Failed to update FCM token: $updateError")
                            }
                        }
                    }
                } ?: error?.let {
                    Log.e(TAG, "Failed to get FCM token: $it")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during app initialization", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        
        try {
            // Clean up ad resources
            AdManager.getInstance().destroy()
            Log.d(TAG, "AdManager resources cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "Error during app termination", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        instance = null
    }

    companion object {
        private const val TAG = "VibeNestApplication"
        
        @Volatile
        private var instance: VibeNestApplication? = null

        fun getInstance(): VibeNestApplication {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }
}

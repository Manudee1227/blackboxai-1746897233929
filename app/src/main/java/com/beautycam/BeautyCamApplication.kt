package com.beautycam

import android.app.Application
import com.beautycam.ads.AdManager

class BeautyCamApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize AdMob
        AdManager.getInstance().initialize(this)
        
        // Pre-load interstitial ad for better user experience
        AdManager.getInstance().loadInterstitialAd(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        
        // Clean up ad resources
        AdManager.getInstance().destroy()
    }
}

package com.beautycam.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager private constructor() {
    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedAd: RewardedAd? = null
    private var bannerAd: AdView? = null
    
    companion object {
        private const val TAG = "AdManager"
        
        // Test ad unit IDs (replace with real ones in production)
        private const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
        private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
        private const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
        
        @Volatile
        private var instance: AdManager? = null
        
        fun getInstance(): AdManager {
            return instance ?: synchronized(this) {
                instance ?: AdManager().also { instance = it }
            }
        }
    }

    fun initialize(context: Context) {
        // Initialize the Mobile Ads SDK
        MobileAds.initialize(context) { initializationStatus ->
            val statusMap = initializationStatus.adapterStatusMap
            for ((adapterClass, status) in statusMap) {
                Log.d(TAG, "Adapter: $adapterClass, Status: ${status.initializationState}")
            }
        }
    }

    fun loadBannerAd(activity: Activity, adContainer: ViewGroup) {
        // Create banner ad with safe ad content
        bannerAd = AdView(activity).apply {
            adUnitId = BANNER_AD_UNIT_ID
            setAdSize(AdSize.BANNER)
            adListener = createAdListener()
        }

        // Add banner to layout
        adContainer.addView(bannerAd)

        // Load banner with non-personalized ads request
        val adRequest = createNonPersonalizedRequest()
        bannerAd?.loadAd(adRequest)
    }

    fun loadInterstitialAd(context: Context, onAdLoaded: () -> Unit = {}) {
        val adRequest = createNonPersonalizedRequest()

        InterstitialAd.load(context, INTERSTITIAL_AD_UNIT_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    mInterstitialAd = ad
                    mInterstitialAd?.fullScreenContentCallback = createFullScreenCallback()
                    onAdLoaded()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d(TAG, "Interstitial ad failed to load: ${error.message}")
                    mInterstitialAd = null
                }
            })
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit = {}) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(activity)
            mInterstitialAd?.fullScreenContentCallback = createFullScreenCallback(onAdDismissed)
        } else {
            Log.d(TAG, "Interstitial ad not ready")
            onAdDismissed()
        }
    }

    fun loadRewardedAd(context: Context, onAdLoaded: () -> Unit = {}) {
        val adRequest = createNonPersonalizedRequest()

        RewardedAd.load(context, REWARDED_AD_UNIT_ID, adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    mRewardedAd = ad
                    mRewardedAd?.fullScreenContentCallback = createFullScreenCallback()
                    onAdLoaded()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d(TAG, "Rewarded ad failed to load: ${error.message}")
                    mRewardedAd = null
                }
            })
    }

    fun showRewardedAd(activity: Activity, onRewarded: () -> Unit, onAdDismissed: () -> Unit = {}) {
        if (mRewardedAd != null) {
            mRewardedAd?.show(activity) { rewardItem ->
                Log.d(TAG, "User rewarded: ${rewardItem.amount} ${rewardItem.type}")
                onRewarded()
            }
            mRewardedAd?.fullScreenContentCallback = createFullScreenCallback(onAdDismissed)
        } else {
            Log.d(TAG, "Rewarded ad not ready")
            onAdDismissed()
        }
    }

    private fun createNonPersonalizedRequest(): AdRequest {
        return AdRequest.Builder()
            .setRequestAgent("android_studio:ad_template")
            .build()
    }

    private fun createAdListener(): AdListener {
        return object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "Ad loaded successfully")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.d(TAG, "Ad failed to load: ${error.message}")
            }

            override fun onAdOpened() {
                Log.d(TAG, "Ad opened")
            }

            override fun onAdClosed() {
                Log.d(TAG, "Ad closed")
            }
        }
    }

    private fun createFullScreenCallback(onAdDismissed: () -> Unit = {}): FullScreenContentCallback {
        return object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed fullscreen content")
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e(TAG, "Ad failed to show fullscreen content: ${error.message}")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content")
            }
        }
    }

    fun destroy() {
        bannerAd?.destroy()
        bannerAd = null
        mInterstitialAd = null
        mRewardedAd = null
    }
}

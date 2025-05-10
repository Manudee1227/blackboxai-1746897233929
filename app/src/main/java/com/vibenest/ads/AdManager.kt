package com.vibenest.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager private constructor() {
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var isInitialized = false

    fun initialize(context: Context) {
        if (!isInitialized) {
            MobileAds.initialize(context) {
                isInitialized = true
                Log.d(TAG, "Mobile Ads initialized successfully")
                preloadAds(context)
            }
        }
    }

    private fun preloadAds(context: Context) {
        loadInterstitialAd(context)
        loadRewardedAd(context)
    }

    private fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d(TAG, "Interstitial ad loaded successfully")
                    setupInterstitialCallbacks()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Interstitial ad failed to load: ${error.message}")
                    interstitialAd = null
                }
            }
        )
    }

    private fun loadRewardedAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    Log.d(TAG, "Rewarded ad loaded successfully")
                    setupRewardedCallbacks()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Rewarded ad failed to load: ${error.message}")
                    rewardedAd = null
                }
            }
        )
    }

    private fun setupInterstitialCallbacks() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial ad dismissed")
                interstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e(TAG, "Interstitial ad failed to show: ${error.message}")
                interstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Interstitial ad showed successfully")
            }
        }
    }

    private fun setupRewardedCallbacks() {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Rewarded ad dismissed")
                rewardedAd = null
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e(TAG, "Rewarded ad failed to show: ${error.message}")
                rewardedAd = null
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Rewarded ad showed successfully")
            }
        }
    }

    fun showInterstitialAd(activity: Activity, onAdClosed: () -> Unit) {
        if (interstitialAd != null) {
            interstitialAd?.show(activity)
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    onAdClosed()
                    interstitialAd = null
                }
            }
        } else {
            Log.d(TAG, "Interstitial ad not ready yet")
            onAdClosed()
        }
    }

    fun showRewardedAd(
        activity: Activity,
        onRewarded: () -> Unit,
        onAdClosed: () -> Unit
    ) {
        if (rewardedAd != null) {
            rewardedAd?.show(activity) { rewardItem ->
                Log.d(TAG, "User rewarded: ${rewardItem.amount} ${rewardItem.type}")
                onRewarded()
            }
            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    onAdClosed()
                    rewardedAd = null
                }
            }
        } else {
            Log.d(TAG, "Rewarded ad not ready yet")
            onAdClosed()
        }
    }

    companion object {
        private const val TAG = "AdManager"
        private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-xxxxxxxxxxxxxxxx/yyyyyyyyyy"
        private const val REWARDED_AD_UNIT_ID = "ca-app-pub-xxxxxxxxxxxxxxxx/yyyyyyyyyy"

        @Volatile
        private var instance: AdManager? = null

        fun getInstance(): AdManager {
            return instance ?: synchronized(this) {
                instance ?: AdManager().also { instance = it }
            }
        }
    }
}

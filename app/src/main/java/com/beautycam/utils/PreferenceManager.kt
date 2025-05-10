package com.beautycam.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    fun setTermsAccepted(accepted: Boolean) {
        preferences.edit().putBoolean(KEY_TERMS_ACCEPTED, accepted).apply()
    }

    fun isTermsAccepted(): Boolean {
        return preferences.getBoolean(KEY_TERMS_ACCEPTED, false)
    }

    fun setTermsAcceptanceTimestamp(timestamp: Long) {
        preferences.edit().putLong(KEY_TERMS_ACCEPTANCE_TIME, timestamp).apply()
    }

    fun getTermsAcceptanceTimestamp(): Long {
        return preferences.getLong(KEY_TERMS_ACCEPTANCE_TIME, 0)
    }

    companion object {
        private const val PREF_NAME = "beautycam_preferences"
        private const val KEY_TERMS_ACCEPTED = "terms_accepted"
        private const val KEY_TERMS_ACCEPTANCE_TIME = "terms_acceptance_time"
    }
}

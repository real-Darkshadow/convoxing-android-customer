package com.convoxing.convoxing_customer.utils.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import com.amplitude.android.Amplitude
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class AnalyticsHelperUtil @Inject constructor(
    private val amplitude: Amplitude,
    private val firebaseAnalytics: FirebaseAnalytics,
) {

    fun logEvent(eventName: String, payload: Map<String, Any?>) {

        val bundle = Bundle().apply {
            for ((key, value) in payload) {
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Boolean -> putBoolean(key, value)
                }
            }
        }

        firebaseAnalytics.logEvent(
            eventName,
            bundle
        )

        amplitude.track(
            eventName,
            payload
        )

    }

    fun logAmplitudeEvent(eventName: String, payload: MutableMap<String, Any>) {
        amplitude.track(
            eventName,
            payload
        )
    }


    fun trackScreenView(screenName: String) {
        amplitude.track(screenName, mutableMapOf("isViewed" to true))
        firebaseAnalytics.logEvent(screenName, bundleOf("isViewed" to true))
    }

    fun trackButtonClick(buttonName: String) {
        firebaseAnalytics.logEvent(buttonName, bundleOf("is_click" to true))
        amplitude.track(buttonName, mutableMapOf("is_click" to true))
    }


}
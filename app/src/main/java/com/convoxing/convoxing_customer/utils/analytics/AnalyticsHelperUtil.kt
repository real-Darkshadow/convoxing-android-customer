package com.convoxing.convoxing_customer.utils.analytics

import android.os.Bundle
import com.amplitude.android.Amplitude
import com.google.firebase.analytics.FirebaseAnalytics
import com.posthog.PostHog
import javax.inject.Inject

class AnalyticsHelperUtil @Inject constructor(
    private val amplitude: Amplitude,
    private val firebaseAnalytics: FirebaseAnalytics,
) {

    fun logEvent(eventName: String, payload: Map<String, Any>) {
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
        PostHog.capture(event = eventName, properties = payload)
        amplitude.track(
            eventName,
            payload
        )
    }


    fun trackScreenView(screenName: String) {
        amplitude.track(
            screenName,
            mutableMapOf("isViewed" to true)
        )
        PostHog.screen(screenName, mutableMapOf("isViewed" to true))
    }

    fun logAmplitudeEvent(eventName: String, payload: MutableMap<String, Any>) {
        amplitude.track(
            eventName,
            payload
        )
    }

    fun trackButtonClick(buttonName: String) {
        amplitude.track(
            buttonName,
            mutableMapOf("is_click" to true)
        )
        PostHog.capture(buttonName, properties = mutableMapOf("is_click" to true))
    }


}
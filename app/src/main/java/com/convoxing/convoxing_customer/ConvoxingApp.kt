package com.convoxing.convoxing_customer

import android.app.Application
import com.convoxing.convoxing_customer.BuildConfig.POSTHOG_API_KEY
import com.convoxing.convoxing_customer.BuildConfig.POSTHOG_HOST
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel

@HiltAndroidApp
class ConvoxingApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Purchases.debugLogsEnabled = true
        Purchases.configure(
            PurchasesConfiguration.Builder(this, BuildConfig.REVENUE_CAT_API).build()
        )

        val config = PostHogAndroidConfig(
            apiKey = POSTHOG_API_KEY,
            host = POSTHOG_HOST
        )
        PostHogAndroid.setup(this, config)


        OneSignal.initWithContext(this, BuildConfig.ONESIGNAL_APP_ID)
    }
}
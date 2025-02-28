package com.convoxing.convoxing_customer.di

import com.amplitude.android.Configuration
import com.convoxing.convoxing_customer.BuildConfig
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import com.convoxing.convoxing_customer.ConvoxingApp
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.amplitude.android.Amplitude

@Module
@InstallIn(SingletonComponent::class)
object EngagementModule {

    @Provides
    @Singleton
    fun providesAnalyticsUtil(
        firebaseAnalytics: FirebaseAnalytics,
        amplitude: Amplitude,
    ): AnalyticsHelperUtil {
        return AnalyticsHelperUtil(amplitude, firebaseAnalytics)
    }


    @Provides
    @Singleton
    fun providesFirebaseAnalytics(context: ConvoxingApp): FirebaseAnalytics {
        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
        return FirebaseAnalytics.getInstance(context)
    }


    @Provides
    @Singleton
    fun providesAmplitudeAnalytics(context: ConvoxingApp): Amplitude {
        return Amplitude(
            Configuration(
                apiKey = BuildConfig.AMPLITUDE_API_KEY,
                context = context,
            )
        )
    }


//    @Provides
//    @Singleton
//    fun providesDataDogLogger(context: ConvoxingApp, appPrefManager: AppPrefManager): Logger {
//        val configuration = com.datadog.android.core.configuration.Configuration.Builder(
//            clientToken = BuildConfig.DATADOG_API_KEY,
//            env = BuildConfig.BUILD_TYPE,
//            variant = BuildConfig.VERSION_NAME
//        ).build()
//        Datadog.initialize(context, configuration, TrackingConsent.GRANTED)
////        try {
////            Datadog.setUserInfo(
////                appPrefManager.user.mId,
////                appPrefManager.user.userName,
////                appPrefManager.user.email ?: "N/A"
////            )
////
////        } catch (_: Exception) {
////        }
//
//        val logger = Logger.Builder()
//            .setNetworkInfoEnabled(true)
//            .setLogcatLogsEnabled(true)
//            .setRemoteSampleRate(100f)
//            .setBundleWithTraceEnabled(true)
//            .setName("P2P")
//            .build()
//        logger.addTag("build_type", BuildConfig.BUILD_TYPE)
//        logger.addAttribute("version_code", BuildConfig.VERSION_CODE)
//        logger.addAttribute("version_name", BuildConfig.VERSION_NAME)
//        return logger
//    }

//    @Provides
//    @Singleton
//    fun provideFacebookAppEventsLogger(
//        context: ConvoxingApp,
//        appPrefManager: AppPrefManager,
//    ): AppEventsLogger {
//
//        FacebookSdk.setApplicationId(BuildConfig.FACEBOOK_APP_ID)
//        FacebookSdk.setClientToken(BuildConfig.FACEBOOK_CLIENT_TOKEN)
//        FacebookSdk.setAutoInitEnabled(true)
//        FacebookSdk.fullyInitialize()
//        FacebookSdk.setAdvertiserIDCollectionEnabled(true)
//        FacebookSdk.setAutoLogAppEventsEnabled(flag = true)
//        FacebookSdk.setIsDebugEnabled(BuildConfig.DEBUG)
//        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)
//        try {
//            val learner = appPrefManager.user
//            AppEventsLogger.setUserData(
//                email = learner.email,
//                learner.userName?.substringBefore(" "),
//                lastName = learner.userName?.substringAfterLast(" "),
//                phone = learner.phoneNumber,
//                dateOfBirth = learner.dob,
//                gender = learner.gender,
//                city = learner.city,
//                state = "",
//                zip = learner.postalCode,
//                country = ""
//            )
//
//            AppEventsLogger.setUserID(learner.mId)
//            AppEventsLogger.setPushNotificationsRegistrationId(learner.notificationid)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//
//        AppEventsLogger.activateApp(context, BuildConfig.FACEBOOK_APP_ID)
//        return AppEventsLogger.newLogger(context, BuildConfig.FACEBOOK_APP_ID)
//    }
//

}
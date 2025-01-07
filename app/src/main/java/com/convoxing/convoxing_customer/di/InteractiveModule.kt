package com.clapingo.speakana.di

import com.amplitude.android.Amplitude
import com.clapingo.speakana.ConvoxingApp
import com.clapingo.speakana.data.local.AppPrefManager
import com.clapingo.speakana.data.remote.api.ApiService
import com.clapingo.speakana.data.remote.websocket.WebSocketManager
import com.clapingo.speakana.data.repository.auth.AuthRepository
import com.clapingo.speakana.data.repository.auth.AuthRepositoryInterface
import com.clapingo.speakana.data.repository.home.MainRepository
import com.clapingo.speakana.data.repository.home.MainRepositoryInterface
import com.clapingo.speakana.data.repository.installreferrer.EncryptionHelper
import com.clapingo.speakana.data.repository.installreferrer.InstallReferrerRepository
import com.clapingo.speakana.data.repository.installreferrer.InstallReferrerRepositoryInterface
import com.clapingo.speakana.data.repository.payment.PlanAndPaymentRepository
import com.clapingo.speakana.data.repository.payment.PlanAndPaymentRepositoryInterface
import com.clapingo.speakana.data.repository.peeranalysis.AiRepository
import com.clapingo.speakana.data.repository.peeranalysis.AiRepositoryInterface
import com.clapingo.speakana.data.repository.websocket.WebSocketRepository
import com.clapingo.speakana.data.repository.websocket.WebSocketRepositoryInterface
import com.clapingo.speakana.util.WebSocketLifecycleObserver
import com.clapingo.speakana.util.analytics.AnalyticsHelperUtil
import com.datadog.android.log.Logger
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.WebSocketListener
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InteractiveModule {


    @Provides
    @Singleton
    fun provideAppPrefManager(context: ConvoxingApp): AppPrefManager = AppPrefManager(context)


    @Provides
    @Singleton
    fun provideAuthRepository(
        context: ConvoxingApp,
        @Named("api_client")
        api: Retrofit,
        appPrefManager: AppPrefManager,
    ): AuthRepositoryInterface {
        val apiInterface = api.create(ApiService::class.java)
        return AuthRepository(context, apiInterface, appPrefManager)

    }

    @Provides
    @Singleton
    fun providePlanAndPaymentRepo(
        context: ConvoxingApp,
        @Named("api_client")
        api: Retrofit,
        appPrefManager: AppPrefManager,
        @Named("device_id")
        deviceID: String,
    ): PlanAndPaymentRepositoryInterface {
        val apiInterface = api.create(ApiService::class.java)
        return PlanAndPaymentRepository(
            context,
            apiInterface,
            appPrefManager,
            deviceID
        )

    }

    @Provides
    @Singleton
    fun provideMainRepository(
        context: ConvoxingApp,
        @Named("api_client")
        api: Retrofit,
        appPrefManager: AppPrefManager,
        @Named("device_id")
        deviceId: String,

        logger: Logger,
    ): MainRepositoryInterface {
        val apiInterface = api.create(ApiService::class.java)
        return MainRepository(
            context,
            apiInterface,
            appPrefManager,
            deviceId,
            logger
        )
    }

    @Provides
    @Singleton
    fun provideAiRepository(
        context: ConvoxingApp,
        appPrefManager: AppPrefManager,
        @Named("ai_api_client")
        api: Retrofit,
    ): AiRepositoryInterface {
        val apiService = api.create(ApiService::class.java)
        return AiRepository(context, apiService, appPrefManager)
    }

    @Provides
    @Singleton
    fun provideWebsocketRepository(
        client: OkHttpClient,
        listener: WebSocketManager,
        appPrefManager: AppPrefManager,
        logger: Logger,
    ): WebSocketRepositoryInterface {
        return WebSocketRepository(
            client,
            listener,
            appPrefManager,
            logger
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
            .newBuilder()
            // Disable read timeout for WebSocket
            .readTimeout(0, TimeUnit.MILLISECONDS)
            // Send ping frame every 15 seconds to keep the connection alive
            .pingInterval(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true) // Enable automatic reconnection
            .build()
    }

    @Provides
    @Singleton
    fun provideWebSocketListener(
        client: OkHttpClient,
        logger: Logger,
    ): WebSocketListener {
        return WebSocketManager(client, logger)
    }

    @Provides
    @Singleton
    fun provideWebSocketLifecycleObserver(
        repository: WebSocketRepositoryInterface,
        logger: Logger,
    ): WebSocketLifecycleObserver {
        return WebSocketLifecycleObserver(repository, logger)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideInstallReferrerRepository(
        context: ConvoxingApp,
        appPrefManager: AppPrefManager,
        amplitude: Amplitude,
        analyticsHelperUtil: AnalyticsHelperUtil,
        encryptionHelper: EncryptionHelper,
        loggger: Logger,
    ): InstallReferrerRepositoryInterface {
        return InstallReferrerRepository(
            context,
            appPrefManager,
            amplitude,
            analyticsHelperUtil,
            encryptionHelper,
            loggger
        )
    }
}
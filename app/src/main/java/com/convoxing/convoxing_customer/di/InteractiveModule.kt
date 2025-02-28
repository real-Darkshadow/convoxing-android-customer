package com.convoxing.convoxing_customer.di

import com.convoxing.convoxing_customer.data.repository.auth.AuthRepository
import com.convoxing.convoxing_customer.data.repository.auth.AuthRepositoryInterface
import com.convoxing.convoxing_customer.data.repository.home.MainRepository
import com.convoxing.convoxing_customer.data.repository.home.MainRepositoryInterface
import com.convoxing.convoxing_customer.data.repository.installreferrer.EncryptionHelper
import com.convoxing.convoxing_customer.data.repository.installreferrer.InstallReferrerRepository
import com.convoxing.convoxing_customer.data.repository.installreferrer.InstallReferrerRepositoryInterface
import com.convoxing.convoxing_customer.ConvoxingApp
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import com.convoxing.convoxing_customer.data.remote.api.ApiService
import com.convoxing.convoxing_customer.data.repository.chat.ChatRepository
import com.convoxing.convoxing_customer.data.repository.chat.ChatRepositoryInterface
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import com.datadog.android.log.Logger
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
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
    fun provideChatRepository(
        context: ConvoxingApp,
        appPrefManager: AppPrefManager,
        @Named("api_client")
        api: Retrofit,
    ): ChatRepositoryInterface {
        val apiService = api.create(ApiService::class.java)
        return ChatRepository(context, appPrefManager, apiService)
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
//        logger: Logger,
    ): MainRepositoryInterface {
        val apiInterface = api.create(ApiService::class.java)
        return MainRepository(
            context,
            apiInterface,
            appPrefManager,
            deviceId,
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
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideInstallReferrerRepository(
        context: ConvoxingApp,
        appPrefManager: AppPrefManager,
        analyticsHelperUtil: AnalyticsHelperUtil,
        encryptionHelper: EncryptionHelper,
//        loggger: Logger,
    ): InstallReferrerRepositoryInterface {
        return InstallReferrerRepository(
            context,
            appPrefManager,
            analyticsHelperUtil,
            encryptionHelper,
        )
    }
}
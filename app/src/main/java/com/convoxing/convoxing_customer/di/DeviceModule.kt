package com.convoxing.convoxing_customer.di

import android.provider.Settings
import com.convoxing.convoxing_customer.ConvoxingApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DeviceModule {

    @Provides
    @Singleton
    @Named("device_id")
    fun providesDeviceId(context: ConvoxingApp): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}

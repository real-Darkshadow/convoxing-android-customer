package com.convoxing.convoxing_customer.di

import android.content.Context
import com.convoxing.convoxing_customer.ConvoxingApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext app: Context): ConvoxingApp {
        return app as ConvoxingApp
    }
}
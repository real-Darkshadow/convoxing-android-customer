package com.convoxing.convoxing_customer.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.convoxing.convoxing_customer.BuildConfig
import com.convoxing.convoxing_customer.ConvoxingApp
import com.convoxing.convoxing_customer.utils.ExtensionFunctions
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private var BASE_URL = BuildConfig.BASE_URL
    private val okHttpClient = OkHttpClient()
    private val convertor = GsonConverterFactory.create(GsonBuilder().setLenient().create())


    @Singleton
    @Provides
    fun hasInternetConnection(context: ConvoxingApp): Boolean {

        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

        return result

    }

    @Provides
    @Singleton
    @Named("api_client")
    fun provideRetrofitClient(): Retrofit {
        val interceptor = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BODY
        }

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(convertor)
            .client(
                okHttpClient.newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addNetworkInterceptor { chain ->
                        val request = chain.request()
                            .newBuilder()
                            .addHeader("platform", "android")
                            .addHeader("app-version", BuildConfig.VERSION_NAME)
                            .addHeader("app-code", BuildConfig.VERSION_CODE.toString())
                            .addHeader("timezone", TimeZone.getDefault().id)
                            .build()
                        return@addNetworkInterceptor chain.proceed(request)
                    }
                    .addInterceptor(
                        interceptor
                    )
                    .build()
            )
            .build()
    }


    @Provides
    @Singleton
    @Named("locale")
    fun provideLocale(app: ConvoxingApp): String = ExtensionFunctions.getLocaleFromContext(app)

}
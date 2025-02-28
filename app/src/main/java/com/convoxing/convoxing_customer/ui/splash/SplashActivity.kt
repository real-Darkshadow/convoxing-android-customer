package com.convoxing.convoxing_customer.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import com.convoxing.convoxing_customer.ui.auth.activity.AuthActivity
import com.convoxing.convoxing_customer.ui.home.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var appPrefManager: AppPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Flag to control splash screen dismissal
        var isReady = false

        // Install splash screen and keep it on screen until we're ready
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isReady }

        setContentView(R.layout.activity_splash)
        super.onCreate(savedInstanceState)


        Handler(Looper.getMainLooper()).postDelayed({
            isReady = true
            // Determine the target activity based on the user's login state
            val targetActivity =
                if (appPrefManager.isUserLoggedIn) MainActivity::class.java else AuthActivity::class.java
            val targetIntent = Intent(this, targetActivity)
            startActivity(targetIntent)
            finishAffinity() // Close only SplashActivity
        }, 500) // Adjust delay as needed (currently 500ms)
    }
}
package com.convoxing.convoxing_customer.ui.auth.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    @Inject
    lateinit var appPrefManager: AppPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (appPrefManager.isUserLoggedIn) {
//            startActivity(Intent(this, MainActivity::class.java))
//            finishAffinity()
//        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
    }
}
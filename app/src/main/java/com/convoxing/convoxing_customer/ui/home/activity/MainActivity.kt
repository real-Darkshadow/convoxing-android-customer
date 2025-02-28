package com.convoxing.convoxing_customer.ui.home.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.databinding.ActivityMainBinding
import com.convoxing.convoxing_customer.ui.home.viewmodel.HomeViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.hapticFeedback
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.showToast
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import android.net.Uri
import android.util.Log
import com.convoxing.convoxing_customer.data.models.DeepLinkValues
import com.convoxing.convoxing_customer.utils.DeepLinkHandler

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val navController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.home_fragment_container) as NavHostFragment
        navHostFragment.navController
    }
    lateinit var binding: ActivityMainBinding
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var appUpdateManager: AppUpdateManager
    private val REQUEST_CODE_UPDATE = 1234

    // Listener to receive update state changes.
    private val updateListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                // For flexible updates, when update is downloaded, notify user and complete the update.
                showToast("Update downloaded. Restarting the app to complete installation.")
                appUpdateManager.completeUpdate()
            }

            InstallStatus.FAILED -> {
                showToast("Update failed. Please try again later.")
            }
            // Handle additional states if desired.
            else -> {
                // Log or react to other statuses as needed.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setupWithNavController(navController)

        viewModel.navToPractice.observe(this) {
            if (it) {
                binding.bottomNav.selectedItemId = R.id.practiceFragment
                viewModel.navToPractice.postValue(false)
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.settingsFragment, R.id.callFragment -> binding.bottomNav.gone()
                else -> binding.bottomNav.visible()
            }
            binding.root.hapticFeedback()
        }
        // Initialize the in-app update manager and register the update listener
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.registerListener(updateListener)
        checkForAppUpdate()
        viewModel.deepLinkScreen.observe(this) { deepLinkValue ->
            navController.navigate(DeepLinkHandler.getDeepLinkId(deepLinkValue))
        }
    }

    override fun onStart() {
        super.onStart()
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.data != null && intent.data?.scheme.equals("https")) {
            val data: Uri? = intent.data
            data?.let {
                val pathSegments = it.pathSegments
                if (pathSegments.size > 0) {
                    when (DeepLinkValues.fromUri(pathSegments.joinToString(separator = "/"))) {

                        DeepLinkValues.HOME -> viewModel.deepLinkScreen.postValue(DeepLinkValues.HOME)
                        DeepLinkValues.PLANS -> {

                        }

                        DeepLinkValues.PRACTICE -> viewModel.deepLinkScreen.postValue(
                            DeepLinkValues.PRACTICE
                        )

                        DeepLinkValues.LEADERBOARD -> viewModel.deepLinkScreen.postValue(
                            DeepLinkValues.LEADERBOARD
                        )

                        DeepLinkValues.PROFILE -> viewModel.deepLinkScreen.postValue(
                            DeepLinkValues.PROFILE
                        )
                    }
                }
            }
        }
        intent?.data = null
    }


    private fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Prefer immediate update if allowed (for critical updates)
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        REQUEST_CODE_UPDATE
                    )
                }
                // Otherwise, if flexible update is allowed, start that flow.
                else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        this,
                        REQUEST_CODE_UPDATE
                    )
                }
            }
        }.addOnFailureListener { exception ->
            showToast("Update check failed: ${exception.localizedMessage}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the update listener to prevent memory leaks.
        appUpdateManager.unregisterListener(updateListener)
    }

    // Optionally, keep onActivityResult to catch cancellations or immediate update flow results.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE && resultCode != RESULT_OK) {
            showToast("It is recommended to update the app for a smooth experience!")
        }
    }
}
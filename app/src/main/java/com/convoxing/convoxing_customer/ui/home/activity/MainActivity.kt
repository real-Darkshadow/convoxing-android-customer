package com.convoxing.convoxing_customer.ui.home.activity

import android.app.Activity
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
    private val updateInfoTask by lazy { appUpdateManager.appUpdateInfo }
    private val REQUEST_CODE_UPDATE = 1234
    private var updateInProgress = false

    // Listener to receive update state changes.
    private val updateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            // If flexible update, notify user to complete installation
            showToast("Update downloaded. Restarting the app to complete installation.")
            appUpdateManager.completeUpdate()
        } else if (state.installStatus() == InstallStatus.CANCELED ||
            state.installStatus() == InstallStatus.FAILED
        ) {
            // User canceled or update failed - check again if it was immediate
            updateInProgress = false
            if (state.installErrorCode() != com.google.android.play.core.install.model.InstallErrorCode.NO_ERROR) {
                Log.e("AppUpdate", "Update error: ${state.installErrorCode()}")
            }
            // Re-check for immediate updates
            checkForImmediateUpdate(this)
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
        registerUpdateMonitor()
        checkForUpdates(this)
        viewModel.deepLinkScreen.observe(this) { deepLinkValue ->
            navController.navigate(DeepLinkHandler.getDeepLinkId(deepLinkValue))
        }
    }

    private fun registerUpdateMonitor() {
        // For immediate updates, we need to monitor if user tries to cancel
        appUpdateManager.registerListener(updateListener)
    }

    private fun checkForUpdates(activity: Activity) {
        if (updateInProgress) return

        updateInfoTask.addOnSuccessListener { appUpdateInfo ->
            // First check for immediate update availability
//            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
            updateInProgress = true
            startImmediateUpdate(activity, appUpdateInfo)
//            }
//            // Only check for flexible if immediate is not available
//            else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
//                updateInProgress = true
//                startFlexibleUpdate(activity, appUpdateInfo)
//            }
        }
    }

    private fun startImmediateUpdate(activity: Activity, appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE,
            activity,
            REQUEST_CODE_UPDATE
        )
    }

    private fun startFlexibleUpdate(activity: Activity, appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.FLEXIBLE,
            activity,
            REQUEST_CODE_UPDATE
        )
    }

    private fun checkForImmediateUpdate(activity: Activity) {
        updateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Force immediate update again
                updateInProgress = true
                startImmediateUpdate(activity, appUpdateInfo)
            }
        }
    }

    private fun resumeUpdates(activity: Activity) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            // If an in-app update is already running, resume the update.
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                updateInProgress = true
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE,
                    activity,
                    REQUEST_CODE_UPDATE
                )
            } else if (!updateInProgress) {
                // Check for updates if no update is in progress
                checkForUpdates(activity)
            }
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


    override fun onResume() {
        super.onResume()
        // Always resume/check for updates when the app comes to foreground
        resumeUpdates(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the update listener to prevent memory leaks.
        appUpdateManager.unregisterListener(updateListener)
    }

    // Optionally, keep onActivityResult to catch cancellations or immediate update flow results.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode != RESULT_OK) {
                // Update flow failed or was cancelled
                updateInProgress = false
                // If immediate update was requested but user somehow cancelled, check again
                checkForImmediateUpdate(this)
            }
        }
    }
}
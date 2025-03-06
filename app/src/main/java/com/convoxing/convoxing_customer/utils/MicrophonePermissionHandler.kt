package com.example.yourapp.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Object handling all aspects of microphone permission
 */
object MicrophonePermissionHandler {

    private const val MICROPHONE_PERMISSION = Manifest.permission.RECORD_AUDIO

    /**
     * Checks if the microphone permission is granted
     * @param context The application context
     * @return true if permission is granted, false otherwise
     */
    fun hasMicrophonePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            MICROPHONE_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if rationale should be shown for microphone permission
     * @param activity The current activity
     * @return true if rationale should be shown, false otherwise
     */
    fun shouldShowRationale(activity: Activity): Boolean {
        return activity.shouldShowRequestPermissionRationale(MICROPHONE_PERMISSION)
    }

    /**
     * Register permission launcher with the activity
     * @param activity The activity that will handle the permission result
     * @param onPermissionGranted Callback when permission is granted
     * @param onPermissionDenied Callback when permission is denied
     * @return ActivityResultLauncher for permission request
     */
    fun registerPermissionLauncher(
        activity: FragmentActivity,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                // Check if user clicked "Don't ask again"
                if (!shouldShowRationale(activity)) {
                    showSettingsDialog(activity, onPermissionDenied)
                } else {
                    showRationaleDialog(activity, onPermissionGranted, onPermissionDenied)
                }
            }
        }
    }

    /**
     * Request microphone permission
     * @param activity The current activity
     * @param permissionLauncher The launcher to request permission
     * @param onPermissionGranted Callback when permission is granted
     */
    fun requestMicrophonePermission(
        activity: Activity,
        permissionLauncher: ActivityResultLauncher<String>,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        when {
            // Permission already granted
            hasMicrophonePermission(activity) -> {
                onPermissionGranted()
            }
            // Should show rationale first
            shouldShowRationale(activity) -> {
                showRationaleDialog(
                    activity,
                    onRequestPermission = { permissionLauncher.launch(MICROPHONE_PERMISSION) },
                    onDeny = onPermissionDenied
                )
            }
            // Request permission directly
            else -> {
                permissionLauncher.launch(MICROPHONE_PERMISSION)
            }
        }
    }

    /**
     * Opens application settings for the app
     * @param context Application context
     */
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * Dialog showing rationale for requesting microphone permission
     */
    private fun showRationaleDialog(
        activity: Activity,
        onRequestPermission: () -> Unit,
        onDeny: () -> Unit
    ) {
        AlertDialog.Builder(activity)
            .setTitle("Microphone Permission Required")
            .setMessage("This app needs access to your microphone to record audio. Please grant the permission to continue.")
            .setPositiveButton("Grant Permission") { dialog, _ ->
                dialog.dismiss()
                onRequestPermission()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                onDeny()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Dialog guiding user to app settings when permission is permanently denied
     */
    private fun showSettingsDialog(
        activity: Activity,
        onDeny: () -> Unit
    ) {
        AlertDialog.Builder(activity)
            .setTitle("Permission Permanently Denied")
            .setMessage("Microphone permission has been permanently denied. Please enable it in app settings to use this feature.")
            .setPositiveButton("Open Settings") { dialog, _ ->
                dialog.dismiss()
                openAppSettings(activity)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                onDeny()
            }
            .setCancelable(false)
            .show()
    }
}
package com.convoxing.convoxing_customer.utils

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.convoxing.convoxing_customer.BuildConfig
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.models.CrashlyticsCustomLog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

object ExtensionFunctions {

    fun Fragment.getLocale(): String = getLocaleFromContext(
        context = requireContext() //get context from the fragment scope
    )

    fun Activity.getLocale(): String = getLocaleFromContext(
        context = this //get context from the activity scope
    )

    fun getLocaleFromContext(context: Context): String {
        if (BuildConfig.BUILD_TYPE == "internationalDebug")
            return "us"
        else if (BuildConfig.BUILD_TYPE == "debug")
            return "in"
        val tm =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.networkCountryIso

    }

    fun Activity.showToast(msg: String?) {
        try {
            Toast.makeText(
                this, msg, Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Log.d("TAG", "showToast: ${e.message}")
        }
    }

    fun Activity.showToast(msg: String?, isLong: Boolean = false) {
        try {
            if (isLong)
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            else
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.d("TAG", "showToast: ${e.message}")
        }
    }

//    fun View.startBlinkingAnimation() {
//        val blinkAnimation = AlphaAnimation(1.0f, 0.8f).apply {
//            duration = 800 // Duration for each blink (in milliseconds)
//            repeatMode = Animation.REVERSE
//            repeatCount = INFINITE
//        }
//        this.startAnimation(blinkAnimation)
//    }
//
//    fun View.stopBlinkingAnimation() {
//        this.clearAnimation()
//    }

    fun Fragment.showToast(msg: String?, isLong: Boolean = false) {
        try {
            if (isLong)
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            else
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.d("TAG", "showToast: ${e.message}")
        }
    }

    fun View.gone() {
        this.visibility = View.GONE
    }

    fun View.visible() {
        this.visibility = View.VISIBLE
    }


//    fun ImageView.loadImage(url: Any) {
//        Glide.with(this).load(url).placeholder(R.drawable.ic_user_placeholder).into(this)
//    }

    fun ArrayList<String>.clearList(): ArrayList<String> {
        val re = Regex("[^\\d.]")
        for (i in 0 until size) {
            this[i] = re.replace(this[i], "")
        }
        return this
    }

    fun Context.checkPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        val audioPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )
        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                audioPermission == PackageManager.PERMISSION_GRANTED
    }

    fun View.hapticFeedback() = this.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

//
//    fun Number.roundTo(
//        numFractionDigits: Int,
//    ): Double = "%.${numFractionDigits}f".format(this, Locale.ENGLISH).toDouble()


    fun Float.roundOffDecimal(): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.HALF_UP
        return df.format(this).toDouble()
    }

    fun Double.roundOffDecimal(): Double {
        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.roundingMode = RoundingMode.HALF_UP
        return try {
            decimalFormat.format(this).toDouble()
        } catch (e: NumberFormatException) {
            // If NumberFormatException occurs, clean the string and try again
            val cleanedString = this.toString().replace(Regex("[^\\d.]"), "")
            decimalFormat.format(cleanedString.toDouble()).toDouble()
        }
    }

    fun Int.isHttpSuccessCode(): Boolean {
        return this in 200..299
    }

    @SuppressLint("UnsafeNavigation")
    fun NavController.navigateSafe(@IdRes action: Int, args: Bundle? = null): Boolean {
        return try {
            navigate(action, args)
            true
        } catch (t: Throwable) {
            Log.e("NAVIGATION SAFE", "navigation error for action $action")
            false
        }
    }


    fun NavController.navigateSafe(
        @IdRes resId: Int, args: Bundle?, navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras,
    ): Boolean {
        return try {
            navigate(resId, args, navOptions, navigatorExtras)
            true
        } catch (e: Exception) {
            false

        }

    }

    fun NavController.navigateSafe(directions: NavDirections): Boolean {
        return try {
            navigate(directions)
            true
        } catch (t: Throwable) {
            Log.e("NAVIGATION SAFE", "navigation error for action $directions")
            false
        }
    }


    fun logError(lambda: CrashlyticsCustomLog.() -> Unit) =
        CrashlyticsCustomLog().apply(lambda).apply {
            exception?.let {
                try {
                    Firebase.crashlytics.log(
                        message.toJson() ?: ("" + (exception?.message
                            ?: "") + "\nfunction = ${function.toString()}")
                    )
                    Firebase.crashlytics.recordException(it)

                } catch (e: Exception) {
                    //nothing
                }

            }
        }


    fun <T> T.toJson(): String? {

        return try {
            Gson().toJson(this)
        } catch (e: Exception) {
            return ""
        }
    }

    /**
     * @param mode 0 for primary
     * @param mode 1 for white
     */
//    fun Fragment.changeStatusBar(mode: Int) {
//
//        if (mode == 0) {
//            requireActivity().window.decorView.systemUiVisibility = 0
//            requireActivity().window.statusBarColor =
//                resources.getColor(R.color.colorPrimary)
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//                requireActivity().window.decorView.systemUiVisibility =
//                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                requireActivity().window.statusBarColor =
//                    resources.getColor(R.color.background_off_white1)
//            }
//        }
//    }

    /**
     * @param mode 0 for darker color
     * @param mode 1 for light color
     */
    fun Fragment.changeStatusBarColor(color: Int, mode: Int) {
        if (mode == 0) {
            requireActivity().window.decorView.systemUiVisibility = 0
            requireActivity().window.statusBarColor =
                resources.getColor(R.color.background)
        } else {
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            requireActivity().window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            requireActivity().window.statusBarColor = resources.getColor(color)
        }
    }

    /**
     * @param mode 0 for dark,1 for white
     * @param color color of status bar
     */
    fun Activity.changeStatusBarColor(
        @ColorRes
        color: Int,
        iconColor: Int = 1,
    ) {
        if (iconColor == 1) {
            window.decorView.systemUiVisibility = 0
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }


        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, color)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun Activity.changeLightStatusBarColor(color: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = color
    }

    fun Activity.setLocaleToEnglish() {
        try {
            val locale = Locale.US
            Locale.setDefault(locale)
            val resources: Resources = this.resources
            val config: Configuration = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        } catch (e: Exception) {
            //nothing
        }
    }

    fun Fragment.setLocaleToEnglish() {
        try {
            val locale = Locale.US
            Locale.setDefault(locale)
            val resources: Resources = this.resources
            val config: Configuration = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        } catch (e: Exception) {
            //nothing
        }
    }

    fun Exception.parseErrorMessage(): String {
        var errorMessage = message
        return when (this) {
            is HttpException -> {
                errorMessage = response()?.errorBody()?.string()
                try {
                    JSONObject(errorMessage ?: "").getString("message")
                } catch (e: Exception) {
                    errorMessage.toString()
                }
            }

            else -> {
                errorMessage.toString()
            }
        }

    }

    fun View.toggleVisibility(visible: Boolean? = null) {
        if (visible != null) {
            /**
             * change visiblity based on value
             */
            if (visible) {
                setAnimatedVisible()
            } else {
                setAnimatedGone()
            }
        } else {
            /**
             * toggle visiblity
             */
            if (isVisible) {
                setAnimatedGone()
            } else {
                setAnimatedVisible()
            }
        }


    }

    fun View.setAnimatedGone() {
        animate()
            .alpha(0f)
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                }
            })
    }

    fun View.setAnimatedVisible() {
        animate()
            .alpha(1f)
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.VISIBLE
                }

                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    visibility = View.VISIBLE
                }
            })
    }

    fun Fragment.hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    fun Fragment.showKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    fun String.isValidEmail(): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun timeZone() = TimeZone.getDefault().id

    fun isNull(vararg values: String?): Boolean {
        values.forEach { value ->
            if (value.isNullOrBlank())
                return true
        }
        return false
    }

    fun String?.isNotNullOrBlank(): Boolean {
        return (this != null && this != "")
    }

    fun <T : Any> T.TAG() = this::class.simpleName

    fun ViewPager2.addCarouselEffect(enableZoom: Boolean = true) {
        clipChildren = false    // No clipping the left and right items
        clipToPadding = false   // Show the viewpager in full width without clipping the padding
        offscreenPageLimit = 3  // Render the left and right items
        (getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER // Remove the scroll effect

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer((20 * Resources.getSystem().displayMetrics.density).toInt()))
        if (enableZoom) {
            compositePageTransformer.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = (0.80f + r * 0.20f)
            }
        }
        setPageTransformer(compositePageTransformer)
    }

    fun capitalizeFirstLetter(str: String): String {
        return when {
            str.isEmpty() -> ""
            else -> str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1)
        }
    }


    fun Activity.showSnackBarWithRetry(
        parent: View,
        message: String? = "Something went wrong!",
        retryAction: () -> Unit,
    ) {
        message?.let {
            val snackBar = Snackbar.make(parent, message, Snackbar.LENGTH_INDEFINITE)
            snackBar.setAction("Retry") {
                snackBar.dismiss()
                retryAction()
            }
            snackBar.setActionTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.primary_text_dark
                )
            )
            snackBar.show()
        }
    }

    fun String.convertMongoUtcToDateTime(): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat(" dd MMM, yyyy", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()

        val date = inputFormat.parse(this)
        return outputFormat.format(date)
    }

    fun Int.convertSecondsToMinSec(): String {
        val minutes = this / 60
        val remainingSeconds = this % 60
        return String.format("%d min %02d sec", minutes, remainingSeconds)
    }
}

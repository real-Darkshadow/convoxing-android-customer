package com.convoxing.convoxing_customer.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amplitude.android.Amplitude
import com.amplitude.core.events.Identify
import com.convoxing.convoxing_customer.BuildConfig
import com.convoxing.convoxing_customer.ui.home.activity.MainActivity
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import com.convoxing.convoxing_customer.databinding.FragmentLoginBinding
import com.convoxing.convoxing_customer.ui.auth.viewmodel.AuthViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.logError
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.navigateSafe
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.showToast
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.convoxing.convoxing_customer.utils.Status
import com.convoxing.convoxing_customer.utils.Utils.hashNonce
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.onesignal.OneSignal
import com.posthog.PostHog
import com.revenuecat.purchases.Purchases
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var analyticsHelper: AnalyticsHelperUtil

    @Inject
    lateinit var amplitude: Amplitude

    @Inject
    lateinit var appPrefManager: AppPrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initFcm()
        analyticsHelper.trackScreenView("Login Screen")
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        binding.signInWithGoogle.setOnClickListener {
            showProgressBar(true)
            googleSignIn()
        }
        binding.signInWithFacebook.setOnClickListener {
//            viewModel.socialLogin()
            if (!appPrefManager.user.isProfileSetup) {
                findNavController().navigateSafe(R.id.onboardingFragment)
            } else {
                appPrefManager.isUserLoggedIn = true
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
        }
    }

    private fun setObservers() {
        viewModel.socialLoginResult.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    showProgressBar(false)
                    it.data?.user?.let { user ->
                        viewModel.setUserPref(user)
                        val identify = Identify().set("name", user.name)
                            .set("email", user.email)
                        identify.set("user_id", user.mId)
                        amplitude.identify(identify)
                        firebaseAnalytics.setUserId(user.mId)
                        firebaseAnalytics.setUserProperty("name", user.name)
                        firebaseAnalytics.setUserProperty("email", user.email)
                        Purchases.sharedInstance.logIn(user.mId)
                        PostHog.identify(
                            user.mId,
                            userProperties = mapOf("email" to user.email, "name" to user.name)
                        )
                        OneSignal.User.addEmail(user.email)
                        OneSignal.login(user.mId)
                        OneSignal.User.addTags(
                            mutableMapOf(
                                "user_name" to user.name,
                                "is_subscribed" to user.isUserSubscribed.toString()
                            )
                        )

                        analyticsHelper.logEvent(
                            "login_success",
                            mutableMapOf("email" to user.email)
                        )
                        if (!user.isProfileSetup) {
                            findNavController().navigateSafe(R.id.onboardingFragment)
                        } else {
                            appPrefManager.isUserLoggedIn = true
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finishAffinity()
                        }
                    }
                }

                Status.IDLE -> Unit
                Status.ERROR -> {
                    analyticsHelper.logEvent(
                        "login_failed",
                        mutableMapOf()
                    )
                    showProgressBar(false)
                    showToast("Something Went Wrong!")
                }

                Status.LOADING -> {}
            }

        }

    }

    private fun googleSignIn() {
        val rawNonce = UUID.randomUUID().toString()
        val hashedNonce = hashNonce(rawNonce)
        val credentialManager = CredentialManager.create(requireContext())

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.SIGN_IN_CLIENT) // Ensure this matches Google Cloud Console
            .setAutoSelectEnabled(true)
            .setNonce(hashedNonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                // Add logging to help debug
                Log.d("GoogleSignIn", "Starting credential request")
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireActivity()
                )
                Log.d("GoogleSignIn", "Got credential response: ${result.credential.type}")
                handleGoogleSignIn(result)
            } catch (e: NoCredentialException) {
                Log.e("GoogleSignIn", "No credential available", e)
                requireContext().startActivity(getAddGoogleAccountIntent())
                showToast("Please add a Google account to continue")
                analyticsHelper.logEvent(
                    getString(R.string.analytic_event_login_failed),
                    mutableMapOf(
                        "error_type" to "NoCredentialException",
                        "error_message" to (e.localizedMessage ?: "No message"),
                        "build_type" to BuildConfig.BUILD_TYPE
                    )
                )
            } catch (e: GetCredentialException) {
                Log.e("GoogleSignIn", "GetCredentialException", e)
                logError {
                    message = e.message
                    exception = e
                    function = "googleSignIn"
                }
                showToast("Authentication error: Please try again")
                analyticsHelper.logEvent(
                    getString(R.string.analytic_event_login_failed),
                    mutableMapOf(
                        "error_type" to "GetCredentialException",
                        "error_message" to (e.localizedMessage ?: "No message"),
                        "build_type" to BuildConfig.BUILD_TYPE,
                        "client_id" to BuildConfig.SIGN_IN_CLIENT
                    )
                )
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Unexpected exception", e)
                showToast("An error occurred: ${e.localizedMessage ?: "Unknown error"}")
                analyticsHelper.logEvent(
                    getString(R.string.analytic_event_login_failed),
                    mutableMapOf(
                        "error_type" to e.javaClass.simpleName,
                        "error_message" to (e.localizedMessage ?: "No message"),
                        "build_type" to BuildConfig.BUILD_TYPE,
                        "client_id" to BuildConfig.SIGN_IN_CLIENT
                    )
                )
            } finally {
                showProgressBar(false)
            }
        }
    }

    private fun getAddGoogleAccountIntent(): Intent {
        val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        return intent
    }

    private fun handleGoogleSignIn(result: GetCredentialResponse) {
        try {
            when (val credential = result.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            Log.d(
                                "GoogleSignIn",
                                "Token successfully parsed, email: ${googleIdTokenCredential.id}"
                            )

                            googleIdTokenCredential.let {
                                val displayName = it.displayName
                                val email = it.id
                                val idToken = it.idToken

                                Log.d(
                                    "GoogleSignIn",
                                    "Calling social login with email: $email, name: $displayName"
                                )
                                viewModel.socialLogin(
                                    email = email,
                                    name = displayName ?: "",
                                    token = idToken,
                                    authType = "google"
                                )
                            }
                        } catch (e: GoogleIdTokenParsingException) {
                            Log.e("GoogleSignIn", "Failed to parse Google ID token", e)
                            showToast("Failed to parse Google ID token. Please try again.")
                            analyticsHelper.logEvent(
                                getString(R.string.analytic_event_login_failed),
                                mutableMapOf(
                                    "error" to "GoogleIdTokenParsingException",
                                    "message" to e.toString()
                                )
                            )
                        }
                        return
                    }
                    handleLoginFailure("Unexpected credential type: ${credential.type}")
                }

                else -> handleLoginFailure("Unexpected credential class: ${credential::class.java.simpleName}")
            }
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Unexpected exception in handleGoogleSignIn", e)
            handleLoginFailure("Exception: ${e.message}")
        }
    }

    private fun handleLoginFailure(message: String) {
        showToast("Something Went Wrong")
        showProgressBar(false)
        analyticsHelper.logEvent(
            getString(R.string.analytic_event_login_failed),
            mutableMapOf("email" to "NA", "message" to message)
        )
    }

    private fun showProgressBar(show: Boolean) {
        binding.apply {
            if (show) {
                progressBar.visible()
                overlay.visible()
            } else {
                progressBar.gone()
                overlay.gone()
            }
        }
    }


}
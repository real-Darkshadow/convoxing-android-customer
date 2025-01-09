package com.convoxing.convoxing_customer.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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
    lateinit var analyticsHelper: AnalyticsHelperUtil

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
                        if (!user.isProfileSetup) {
                            findNavController().navigateSafe(R.id.onboardingFragment)
                        } else {
                            appPrefManager.isUserLoggedIn = true
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                        }
                    }

                }

                Status.IDLE -> Unit
                Status.ERROR -> {}
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
            .setServerClientId(BuildConfig.SIGN_IN_CLIENT)
            .setAutoSelectEnabled(true)
            .setNonce(hashedNonce)
            .build()

        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        lifecycleScope.launch {
            try {
                val result =
                    credentialManager.getCredential(request = request, context = requireActivity())
                handleGoogleSignIn(result)
            } catch (e: NoCredentialException) {
                analyticsHelper.logEvent(
                    "google_sign_in_cancelled", mutableMapOf(
                        "error_type" to "NO_CREDENTIAL",
                        "error_msg" to e.message,
                    )
                )
                requireContext().startActivity(getAddGoogleAccountIntent())
            } catch (e: GetCredentialException) {
                logError {
                    message = e.message
                    exception = e
                    function = "googleSignIn"
                }
                analyticsHelper.logEvent(
                    "google_sign_in_cancelled", mutableMapOf(
                        "error_type" to e.type,
                        "error_msg" to e.message,
                    )
                )
            } catch (e: Exception) {
                analyticsHelper.logEvent(
                    "google_sign_in_cancelled", mutableMapOf(
                        "error_type" to "EXCEPTION",
                        "error_msg" to e.message,
                    )
                )
                showToast("An error occurred: ${e.localizedMessage}")
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
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        googleIdTokenCredential.let {
                            val displayName = it.displayName
                            val email = it.id
                            viewModel.socialLogin(
                                email = email,
                                name = displayName ?: "",
                                token = it.idToken,
                                authType = "google"
                            )
                        }

                    } catch (e: GoogleIdTokenParsingException) {
                        showToast("Failed to parse Google ID token. Please try again.")
                        analyticsHelper.logEvent(
                            getString(R.string.analytic_event_login_failed),
                            mutableMapOf("email" to "NA")
                        )
                    } catch (e: Exception) {
                        showToast("An error occurred: ${e.localizedMessage}")
                        analyticsHelper.logEvent(
                            getString(R.string.analytic_event_login_failed),
                            mutableMapOf("email" to "NA")
                        )
                    }
                    return
                }
                handleLoginFailure("Unexpected type of credential")
            }

            else -> handleLoginFailure("Unexpected type of credential")
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
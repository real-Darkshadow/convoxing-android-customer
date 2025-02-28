package com.convoxing.convoxing_customer.ui.progress.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import com.convoxing.convoxing_customer.databinding.FragmentSettingsBinding
import com.convoxing.convoxing_customer.ui.auth.activity.AuthActivity
import com.convoxing.convoxing_customer.ui.home.viewmodel.HomeViewModel
import com.convoxing.convoxing_customer.ui.progress.adapter.SettingsAdapter
import com.convoxing.convoxing_customer.ui.progress.viewmodel.ProgressViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.showToast
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import com.onesignal.OneSignal
import com.posthog.PostHog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private val viewModel: ProgressViewModel by viewModels()
    private var _binding: FragmentSettingsBinding? = null
    val appPrefManager by lazy { AppPrefManager(requireContext()) }
    private val binding get() = _binding!!

    @Inject
    lateinit var analyticHelper: AnalyticsHelperUtil

    // Define settings sections
    private val accountSettings = mapOf(
        0 to Pair("Profile", "profile"),
        1 to Pair("Notifications", "notifications"),
        2 to Pair("Logout", "logout")
    )

    private val supportSettings = mapOf(
        0 to Pair("Help & FAQ", "help"),
        1 to Pair("Contact Us", "contact"),
        2 to Pair("Share App", "share")
    )

    private val legalSettings = mapOf(
        0 to Pair("Privacy Policy", "privacy"),
        1 to Pair("Terms of Use", "terms"),
        2 to Pair("Delete Account", "delete"),
        3 to Pair("Logout", "logout")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analyticHelper.trackScreenView("settings")

        // Set version name
        try {
            val packageInfo =
                requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            binding.versionInfo.text = "Version ${packageInfo.versionName}"
        } catch (e: Exception) {
            binding.versionInfo.text = "Version 1.0.0"
        }

        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        // Setup Account section
//        binding.accountOptions.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = SettingsAdapter(accountSettings) { position ->
//                handleAccountSection(position)
//            }
//            isNestedScrollingEnabled = false
//        }

        // Setup Support section
        binding.supportOptions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = SettingsAdapter(supportSettings) { position ->
                handleSupportSection(position)
            }
            isNestedScrollingEnabled = false
        }

        // Setup Legal section
        binding.legalOptions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = SettingsAdapter(legalSettings) { position ->
                handleLegalSection(position)
            }
            isNestedScrollingEnabled = false
        }
    }

    private fun handleAccountSection(position: Int) {
        when (position) {
//            0 -> findNavController().navigate(R.id.action_settingsFragment_to_profileFragment)
//            1 -> findNavController().navigate(R.id.action_settingsFragment_to_notificationsFragment)
            2 -> {
                analyticHelper.logEvent(
                    "logout_button", mutableMapOf(
                        "email" to appPrefManager.user.email,
                        "id" to appPrefManager.user.mId
                    )
                )
                PostHog.reset()
                OneSignal.logout()
                viewModel.logoutUser()
                requireActivity().finish()
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
            }
        }
    }

    private fun handleSupportSection(position: Int) {
        when (position) {
            0 -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.convoxing.com/faq")
                )
            )

            1 -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.convoxing.com/contact")
                )
            )

            2 -> {
                shareText("Check out Convoxing! It's helping me improve my language skills. Download it now: https://www.convoxing.com/app")
                analyticHelper.logEvent(
                    "Share_Button", mutableMapOf(
                        "email" to appPrefManager.user.email,
                    )
                )
            }
        }
    }

    private fun handleLegalSection(position: Int) {
        when (position) {
            0 -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.convoxing.com/privacy")
                )
            )

            1 -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.convoxing.com/terms")
                )
            )

            2 -> {
                analyticHelper.logEvent(
                    "Account_Delete", mutableMapOf(
                        "email" to appPrefManager.user.email,
                        "id" to appPrefManager.user.mId
                    )
                )
            }

            3 -> {
                analyticHelper.logEvent(
                    "logout_button", mutableMapOf(
                        "email" to appPrefManager.user.email,
                        "id" to appPrefManager.user.mId
                    )
                )
                PostHog.reset()
                OneSignal.logout()
                viewModel.logoutUser()
                requireActivity().finish()
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
            }
        }
    }

    private fun shareText(text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)

        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
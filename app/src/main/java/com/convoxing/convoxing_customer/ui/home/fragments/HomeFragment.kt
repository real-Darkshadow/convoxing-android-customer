package com.convoxing.convoxing_customer.ui.home.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import com.convoxing.convoxing_customer.databinding.FragmentHomeBinding
import com.convoxing.convoxing_customer.ui.chat.activity.ChatActivity
import com.convoxing.convoxing_customer.ui.home.bottomsheet.SessionRatingBottomSheet
import com.convoxing.convoxing_customer.ui.home.viewmodel.HomeViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.changeStatusBarColor
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.navigateSafe
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.showToast
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.convoxing.convoxing_customer.utils.Resource
import com.convoxing.convoxing_customer.utils.Status
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.onesignal.OneSignal
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitLogIn
import com.revenuecat.purchases.ui.revenuecatui.ExperimentalPreviewRevenueCatUIPurchasesAPI
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallActivityLauncher
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallResult
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallResultHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(ExperimentalPreviewRevenueCatUIPurchasesAPI::class)
class HomeFragment : Fragment(), TextToSpeech.OnInitListener, PaywallResultHandler {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var appPrefManager: AppPrefManager

    @Inject
    lateinit var analyticsHelperUtil: AnalyticsHelperUtil

    private var freeSessionDialog: Dialog? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var paywallActivityLauncher: PaywallActivityLauncher
    private var notificationDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeStatusBarColor(R.color.white, 1)
        analyticsHelperUtil.trackScreenView("Home Screen")
        paywallActivityLauncher = PaywallActivityLauncher(this, this)
        viewModel.getLastSessionRating()
        viewModel.getRandomVocab()
        textToSpeech = TextToSpeech(requireContext(), this)
        setListeners()
        setObservers()
    }

    private fun launchPaywallActivity() {
        analyticsHelperUtil.trackScreenView("paywall")
        paywallActivityLauncher.launch()
    }

    private fun setListeners() {
        binding.apply {
            proButton.setOnClickListener {
                analyticsHelperUtil.trackButtonClick("pro_button")
                launchPaywallActivity()
            }
            startLessonBtn.setOnClickListener {
                if (viewModel.isSubscriptionActive || viewModel.isFreeSessionsLeft) {
                    analyticsHelperUtil.trackButtonClick("start_lesson")
                    startActivity(Intent(requireContext(), ChatActivity::class.java).also {
                        it.putExtra("category", "Casual")
                        it.putExtra("isCasual", true)
                    })
                } else {
                    analyticsHelperUtil.trackButtonClick("start_lesson")
                    launchPaywallActivity()
                }
            }
            rolePlayingCard.setOnClickListener {
                analyticsHelperUtil.trackButtonClick("role_play_card")
                viewModel.navToPractice.postValue(true)
            }
            vocabSound.setOnClickListener {
                textToSpeech.speak(binding.tvVocab.text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
            vocabShare.setOnClickListener {
                val vocabText = binding.tvVocab.text.toString()
                if (vocabText.isNotBlank()) {
                    shareContent("Check out this word: $vocabText")
                } else {
                    showToast("No content to share!")
                }
            }
        }
    }

    /**
     * Show a TapTargetView on the startLesson button to guide new users.
     */
    private fun showStartLessonTapTarget() {
        TapTargetView.showFor(
            requireActivity(),
            TapTarget.forView(
                binding.startLessonBtn,
                "Start Your Lesson",
                "Tap here to begin your lesson and explore our interactive chat features."
            )
                .outerCircleColor(R.color.light_yellow)
                .titleTextSize(20)
                .descriptionTextSize(16)
                .textColor(R.color.secondary)
                .cancelable(false)
                .transparentTarget(true)
                .drawShadow(false),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    analyticsHelperUtil.trackButtonClick("overview_start_lesson")
                    startActivity(Intent(requireContext(), ChatActivity::class.java).also {
                        it.putExtra("category", "Casual")
                        it.putExtra("isCasual", true)
                        it.putExtra("isOverview", true)
                    })
                }
            }
        )
    }

    private fun showSessionFeedbackBottomSheet(
        sessionId: String,
        isCall: Boolean = false,
        isChat: Boolean = false
    ) {
        val bottomSheet = SessionRatingBottomSheet(
            sessionId = sessionId,
            isCallExit = isCall,
            isChatExit = isChat
        ) { id, rating, comment, reasonsForLeaving ->
            viewModel.submitSessionRating(id, rating.toInt(), comment ?: "")
            if (rating > 3) {
                if (!OneSignal.Notifications.permission) {
                    showNotificationPermissionDialog(requireContext())
                }
            }
            if (rating >= 4) {
                launchInAppReview()
            }
        }
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun launchInAppReview() {
        val reviewManager = ReviewManagerFactory.create(requireContext())
        val request: Task<ReviewInfo> = reviewManager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow: Task<Void> =
                    reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener { }
            }
        }
    }

    private fun calculateGreetingMessage(userName: String) {
        val currentTime = LocalTime.now()
        val greetingMessage = when {
            currentTime.hour < 12 -> "Good Morning, $userName!\nReady to learn? 🌞"
            currentTime.hour < 18 -> "Good Afternoon, $userName!\nLet's practice some English! 🌟"
            else -> "Good Evening, $userName!\nTime for a chat? 🌙"
        }
        binding.tvGreeting.text = greetingMessage
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.US
        } else {
            showToast("TTS Initialization failed!")
        }
    }

    private fun shareContent(content: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun showFreeSessionDialog(context: Context) {
        // If the dialog is already visible, do nothing (or dismiss it if needed)
        if (freeSessionDialog?.isShowing == true) {
            return
        }

        freeSessionDialog = Dialog(context).apply {
            setContentView(R.layout.fragment_custom_dialog)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(true)
            setOnDismissListener {
                analyticsHelperUtil.logEvent(
                    "free_session_banner",
                    mutableMapOf("is_shown" to true)
                )
                appPrefManager.isUserShowFreeSession = false
                if (!OneSignal.Notifications.permission) {
                    showNotificationPermissionDialog(requireContext())
                }
                if (viewModel.user.isNewUser && appPrefManager.isFirstTimeUser) {
                    showStartLessonTapTarget()
                    appPrefManager.isFirstTimeUser = false
                }

                // Reset the dialog variable when dismissed
                freeSessionDialog = null
            }
        }

        val btnClaim = freeSessionDialog?.findViewById<TextView>(R.id.btnClaim)
        btnClaim?.setOnClickListener {
            freeSessionDialog?.dismiss()
        }

        freeSessionDialog?.show()

        // Optional: Adjust dialog window size
        freeSessionDialog?.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }


    private fun showNotificationPermissionDialog(context: Context) {
        // Prevent multiple dialogs from showing simultaneously
        if (notificationDialog?.isShowing == true) return

        notificationDialog = Dialog(context).apply {
            setContentView(R.layout.dialog_notification_permission)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(true)
            // Optionally, you can set animations or window attributes here
        }

        // Get references to the views defined in the custom layout
        val btnAllow = notificationDialog?.findViewById<TextView>(R.id.tvAllowNotifications)
        val btnDismiss = notificationDialog?.findViewById<TextView>(R.id.tvMaybeLater)

        // When the user agrees, request notification permission
        btnAllow?.setOnClickListener {
            // Launch the permission request in a coroutine on an appropriate dispatcher
            CoroutineScope(Dispatchers.IO).launch {
                OneSignal.Notifications.requestPermission(true)
            }
            notificationDialog?.dismiss()
        }

        // If the user dismisses the dialog, simply close it
        btnDismiss?.setOnClickListener {
            notificationDialog?.dismiss()
        }

        // Show the dialog and adjust its size (90% of the screen width)
        notificationDialog?.show()
        notificationDialog?.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserById()
    }


    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }

    override fun onActivityResult(result: PaywallResult) {
        when (result) {
            PaywallResult.Cancelled -> {
                analyticsHelperUtil.logEvent(
                    "paywall_cancelled",
                    mutableMapOf("reason" to "cancelled")
                )
            }

            is PaywallResult.Error -> {
                analyticsHelperUtil.logEvent(
                    "paywall_error",
                    mutableMapOf("reason" to result.error)
                )
                showToast("Something went wrong")
            }

            is PaywallResult.Purchased -> {
                analyticsHelperUtil.logEvent(
                    "subscription_purchased",
                    mutableMapOf("customer_info" to result.customerInfo)
                )
                viewModel.getUserById()
            }

            else -> Unit
        }
    }

    private fun setObservers() {
        viewModel.userDetailResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    binding.loadingLayout.background =
                        ColorDrawable(requireContext().getColor(R.color.off_white))
                    response?.data?.user?.let { user ->

                    binding.loadingLayout.gone()
                        viewModel.setUserData(user)
                        if (user.isFreeSessionsLeft) {
                            binding.sessionsLeftTv.text = "Sessions Left: ${user.freeSessions}"
                        } else if (user.isUserSubscribed) {
                            binding.sessionsLeftTv.gone()
                        } else {
                            binding.sessionsLeftTv.text = "LIMITED"
                        }
                        binding.streak.text = user.sessionStreak.toString()
                        calculateGreetingMessage(appPrefManager.user.name)
                        viewModel.getSubscriptionStatus()

                        // Show free session dialog if applicable
                        if (appPrefManager.isUserShowFreeSession && user.isNewUser) {
                            showFreeSessionDialog(requireContext())
                        }

                    }
                }

                Status.ERROR -> {
                    showToast(response.data?.message)
                }

                Status.LOADING -> {
                    binding.loadingLayout.visible()
                }

                else -> {}
            }
        }

        viewModel.randomVocabResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    response?.data?.randomVocab?.let { data ->
                        binding.apply {
                            tvVocab.text = data.word
                            tvVocabMean.text = data.definition
                            tvVocabPhonetic.text = data.partOfSpeech
                            randomVocabCard.visible()
                        }
                    }
                }

                Status.ERROR -> {
                    showToast(response.data?.message)
                    binding.randomVocabCard.gone()
                }

                Status.LOADING -> {
                    binding.loadingLayout.visible()
                }

                else -> {}
            }
        }

        viewModel.lastSessionRatingResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    response?.data?.sessionRating?.let { data ->
                        if (!data.isReviewed) {
                            when (data.sessionType) {
                                "call" -> showSessionFeedbackBottomSheet(
                                    data.sessionId,
                                    isCall = true
                                )

                                "chat" -> showSessionFeedbackBottomSheet(
                                    data.sessionId,
                                    isChat = true
                                )

                                else -> showSessionFeedbackBottomSheet(data.sessionId)
                            }
                        }
                    }
                    viewModel.lastSessionRatingResponse.postValue(Resource.idle())
                }

                Status.ERROR -> Unit
                Status.LOADING -> Unit
                Status.IDLE -> Unit
            }
        }

        viewModel.subscriptionStatusResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    response?.data?.activeSubscription?.let { data ->
                        binding.loadingLayout.gone()
                        if (data.status == "active") {
                            binding.sessionsLeftTv.gone()
                            viewModel.isFreeSessionsLeft = data.isFreeSessionsLeft
                            binding.proButton.gone()
                            viewModel.isSubscriptionActive = true
                        } else {
                            viewModel.isFreeSessionsLeft = data.isFreeSessionsLeft
                            binding.proButton.visible()
                            viewModel.isSubscriptionActive = false
                        }
                    }
                    viewModel.subscriptionStatusResponse.postValue(Resource.idle())
                }

                Status.ERROR -> {
                    binding.loadingLayout.gone()
                    viewModel.subscriptionStatusResponse.postValue(Resource.idle())
                    showToast("something went wrong")
                }

                Status.LOADING -> {
                    binding.apply {
                        binding.loadingLayout.background =
                            ColorDrawable(requireContext().getColor(R.color.transparent))
                        loadingLayout.visible()
                    }
                }

                Status.IDLE -> Unit
            }
        }
    }
}
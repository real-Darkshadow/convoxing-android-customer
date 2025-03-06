package com.convoxing.convoxing_customer.ui.chat.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import co.daily.internal.camera.f
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.remote.models.ChatMessage
import com.convoxing.convoxing_customer.databinding.FragmentChatBinding
import com.convoxing.convoxing_customer.ui.analysis.fragments.AnalysisTabViewFragment
import com.convoxing.convoxing_customer.ui.analysis.viewmodel.AnalysisViewModel
import com.convoxing.convoxing_customer.ui.chat.activity.ChatActivity
import com.convoxing.convoxing_customer.ui.chat.adapters.ChatAdapter
import com.convoxing.convoxing_customer.ui.chat.adapters.ScenarioButtonAdapter
import com.convoxing.convoxing_customer.ui.chat.sheets.ExitConfirmationBottomSheet
import com.convoxing.convoxing_customer.ui.chat.viewmodel.ChatViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.changeStatusBarColor
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.showToast
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.convoxing.convoxing_customer.utils.Resource
import com.convoxing.convoxing_customer.utils.Status
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import com.example.yourapp.permission.MicrophonePermissionHandler
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.masoudss.lib.WaveformSeekBar
import com.revenuecat.purchases.ui.revenuecatui.ExperimentalPreviewRevenueCatUIPurchasesAPI
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallActivityLauncher
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallResult
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallResultHandler
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalPreviewRevenueCatUIPurchasesAPI::class)
@AndroidEntryPoint
class ChatFragment : Fragment(), PaywallResultHandler {

    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: ChatAdapter
    private lateinit var waveformSeekBar: WaveformSeekBar
    private lateinit var textToSpeech: TextToSpeech
    private var isTtsReady = false
    private lateinit var microphonePermissionLauncher: ActivityResultLauncher<String>

    // Track the currently playing message ID and state
    private var currentlyPlayingId: String? = null
    private var isPlaying = false

    private val viewModel by activityViewModels<ChatViewModel>()
    private val analysisViewModel by activityViewModels<AnalysisViewModel>()

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private val waveformSamples = mutableListOf<Int>()
    private lateinit var paywallActivityLauncher: PaywallActivityLauncher

    @Inject
    lateinit var analyticsHelperUtil: AnalyticsHelperUtil


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeStatusBarColor(R.color.white, 1)
        paywallActivityLauncher = PaywallActivityLauncher(this, this)
        if (!viewModel.isHistory) viewModel.getSubscriptionStatus()
        microphonePermissionLauncher = MicrophonePermissionHandler.registerPermissionLauncher(
            activity = requireActivity(),
            onPermissionGranted = {},
            onPermissionDenied = {}
        )
        setupAdapter()
        setupListeners()
        setupObservers()
        initSpeechRecognizer()
        initTextToSpeech()
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.isHistory.not()) showExitConfirmationBottomSheet()
                    else {
                        isEnabled = false
                        activity?.onBackPressed()
                    }
                }

            })


        // Bind waveformSeekBar
        waveformSeekBar = binding.waveformSeekBar

        // Initiate the first message
        if (!viewModel.isHistory && !viewModel.isCasual) {
            analyticsHelperUtil.trackScreenView("Chat Screen")
//            viewModel.initiateChat("Hello")
            analyticsHelperUtil.logEvent(
                "chat_initiated",
                mutableMapOf(
                    "isFreeSession" to viewModel.isFreeSession,
                    "isOverview" to viewModel.isOverview,
                    "isCasual" to viewModel.isCasual,
                    "topicId" to viewModel.topicId,
                    "category" to viewModel.category,
                    "topicName" to viewModel.topicName
                )
            )
        } else if (viewModel.isCasual) {
            analyticsHelperUtil.trackScreenView("Chat Screen")
            analyticsHelperUtil.logEvent(
                "chat_initiated",
                mutableMapOf(
                    "isFreeSession" to viewModel.isFreeSession,
                    "isOverview" to viewModel.isOverview,
                    "isCasual" to viewModel.isCasual,
                    "topicId" to viewModel.topicId,
                    "category" to viewModel.category,
                    "topicName" to viewModel.topicName
                )
            )
            if (viewModel.isOverview) showSelectTopicTapTarget()
            binding.topicLayout.visible()
            binding.textContainer.gone()
            binding.msgRecyclerView.gone()
        } else {
            analyticsHelperUtil.trackScreenView("Chat History Screen")
            viewModel.getChatHistory()
            binding.textContainer.gone()
            binding.micContainer.gone()
            binding.aiContainer.gone()
            binding.summaryContainer.visible()
        }
    }

    private fun launchPaywallActivity() {
        analyticsHelperUtil.trackScreenView("paywall")
        paywallActivityLauncher.launch()
    }

    private fun setupObservers() {
        viewModel.chatHistoryResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    binding.mainLayout.visible()
                    binding.loadingLayout.gone()
                    response.data?.chatSession?.let { session ->
                        binding.progressBar.gone()
                        binding.messagesProgressBar.progress = 20
                        if (session.messages.isNotEmpty()) adapter.updateChatList(
                            session.messages,
                            true
                        )
                        else binding.errorLayout.visible()
                    }
                }

                Status.ERROR -> {
                    binding.loadingLayout.gone()
                    binding.errorLayout.visible()
                    showToast("Error: ${response.message}")
                }

                Status.LOADING -> {
                    binding.loadingLayout.visible()
                }

                Status.IDLE -> Unit
            }
        }
        viewModel.chatResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    response.data?.let { aiMessage ->
                        if (aiMessage.isSessionCompleted == true) {
                            findNavController().popBackStack().also {
                                findNavController().navigate(
                                    R.id.chatSummary,
                                    bundleOf("sessionId" to aiMessage.sessionId)
                                )
                            }
                            return@observe
                        }
                        binding.aiContainer.gone()
                        binding.textContainer.visible()
                        viewModel.lastAiPrompt = aiMessage.content.toString()
                        viewModel.currentSessionId = aiMessage.sessionId.toString()
                        binding.messagesProgressBar.max = aiMessage.maxProgress ?: 20
                        binding.messagesProgressBar.progress = aiMessage.userMessageCount!!
                        if (adapter.itemCount > 0) {
                            analysisViewModel.sessionId = aiMessage.sessionId.toString()
                            analysisViewModel.userMessageId = aiMessage.userMessageId.toString()
                            analysisViewModel.getGrammarAnalysis()
                            analysisViewModel.getVocabAnalysis()
                            adapter.updateUserMessage(
                                adapter.itemCount - 1,
                                aiMessage
                            )
                        }
                        adapter.updateList(aiMessage, viewModel.isOverview)
                        scrollToBottom()
                    }
                }

                Status.ERROR -> {
                    binding.micContainer.gone()
                    binding.aiContainer.gone()
                    binding.textContainer.visible()
                    showToast("Error: ${response.message}")
                }

                Status.LOADING -> {
                    binding.apply {
                        aiMsgTv.text = "Eva is typing.."
                        aiContainer.visible()
                        textContainer.gone()
                        micContainer.gone()
                    }
                }

                Status.IDLE -> Unit
            }
        }
        viewModel.hintResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    response.data?.let { aiMessage ->
                        binding.aiContainer.gone()
                        binding.textContainer.visible()
                        binding.apply {
                            userMsgEt.text?.clear()
                            userMsgEt.setText(aiMessage.hint.toString())
                        }
                    }
                }

                Status.ERROR -> {
                    binding.micContainer.gone()
                    binding.aiContainer.gone()
                    binding.textContainer.visible()
                    showToast("Error: ${response.message}")
                }

                Status.LOADING -> {
                    binding.apply {
                        aiMsgTv.text = "Generating hint.."
                        aiContainer.visible()
                        textContainer.gone()
                        micContainer.gone()
                    }
                }

                Status.IDLE -> Unit
            }
        }

        viewModel.subscriptionStatusResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    binding.loadingLayout.gone()
                    response?.data?.activeSubscription?.let { data ->
                        if (data.status == "active" || data.isFreeSessionsLeft) {
                            if (!viewModel.isHistory && !viewModel.isCasual) {
                                viewModel.initiateChat("Hello")
                            }
                            binding.mainLayout.visible()
                            viewModel.isSubscriptionActive = true
                            viewModel.isFreeSession = data.isFreeSessionsLeft
                        } else {
                            viewModel.isSubscriptionActive = false
                            viewModel.isFreeSession = false
                            launchPaywallActivity()
                        }
                    }
                    viewModel.subscriptionStatusResponse.postValue(Resource.idle())
                }

                Status.ERROR -> {
                    showToast("something went wrong")
                    binding.errorLayout.visible()
                    binding.loadingLayout.gone()
                }

                Status.LOADING -> {
                    binding.loadingLayout.visible()
                }

                Status.IDLE -> Unit
            }
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            if (viewModel.isHistory.not()) showExitConfirmationBottomSheet()
            else {
                requireActivity().finish()
            }
        }
        binding.summaryContainer.setOnClickListener {
            findNavController().navigate(
                R.id.chatSummary,
                bundleOf(
                    "sessionId" to viewModel.currentSessionId,
                    "isHistory" to viewModel.isHistory
                )
            )
        }
        binding.sendBtn.setOnClickListener {
            analyticsHelperUtil.trackButtonClick("send_button")
            val userMessage = binding.userMsgEt.text.toString().trim()
            if (userMessage.isNotBlank()) {
                viewModel.lastAiPromptForBetterAnswer = viewModel.lastAiPrompt
                analysisViewModel.currentMessageForAnalysis = userMessage
                binding.userMsgEt.text?.clear()
                adapter.updateList(ChatMessage(content = userMessage, role = "user"))
                scrollToBottom()
                viewModel.initiateChat(userMessage)
            }
        }

        binding.micBtn.setOnClickListener {
            analyticsHelperUtil.trackButtonClick("mic_button")
            MicrophonePermissionHandler.requestMicrophonePermission(
                activity = requireActivity(),
                permissionLauncher = microphonePermissionLauncher,
                onPermissionGranted = {
                    startSpeechToText()
                },
                onPermissionDenied = {
                    showToast("Permission Denied")
                }
            )
        }

        binding.audioCancelBtn.setOnClickListener {
            speechRecognizer?.cancel()
            resetSpeechRecognizerState()
        }
        binding.audioDoneBtn.setOnClickListener {
            stopSpeechRecognizer()
        }
        binding.hintBtn.setOnClickListener {
            analyticsHelperUtil.trackButtonClick("hint_button")
            viewModel.getPromptHint()
        }

        binding.userMsgEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.sendBtn.visible()
                    binding.hintMicLayout.gone()
                } else {
                    binding.sendBtn.gone()
                    binding.hintMicLayout.visible()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupAdapter() {
        adapter = ChatAdapter(
            context = requireContext(),
            activity = requireActivity(),
            messages = arrayListOf(),
            onAnalysisClick = { message, _ ->
                stopMessageAudio()
                val analysisBaseViewBottomSheet =
                    if (!viewModel.isHistory) AnalysisTabViewFragment.newInstance(
                        message.content!!,
                        viewModel.lastAiPromptForBetterAnswer,
                        message.userMessageId!!,
                        message.sessionId!!
                    )
                    else AnalysisTabViewFragment.newInstance(
                        message.content!!,
                        viewModel.lastAiPromptForBetterAnswer,
                        message.id!!,
                        viewModel.currentSessionId
                    )
                analysisBaseViewBottomSheet.show(
                    requireActivity().supportFragmentManager,
                    analysisBaseViewBottomSheet.tag
                )
            },
            collapseKeyboard = {
                hideKeyboard()
            },
            onPlayAudio = { message ->
                playMessageAudio(message)
            }
        )
        val mAdapter = ScenarioButtonAdapter(
            arrayListOf(
                "Casual",
                "Movies",
                "Friendship",
                "Work"
            )
        ) {
            binding.topicLayout.gone()
            binding.msgRecyclerView.visible()
            viewModel.topicName = it
            analyticsHelperUtil.logEvent(
                "chat_initiated",
                mutableMapOf(
                    "isFreeSession" to viewModel.isFreeSession,
                    "isOverview" to viewModel.isOverview,
                    "isCasual" to viewModel.isCasual,
                    "topicId" to viewModel.topicId,
                    "category" to viewModel.category,
                    "topicName" to viewModel.topicName
                )
            )
            viewModel.initiateChat("Hello")
        }
        binding.recyclerViewButtons.adapter = mAdapter
        binding.recyclerViewButtons.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.msgRecyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.msgRecyclerView.adapter = adapter
    }

    private fun scrollToBottom() {
        binding.msgRecyclerView.post {
            binding.msgRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }



    private fun initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext()).apply {
            setRecognitionListener(createRecognitionListener())
        }
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    showToast("Text to speech language not supported")
                } else {
                    isTtsReady = true
                    textToSpeech.setOnUtteranceProgressListener(object :
                        UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            // Speech started
                            isPlaying = true
                        }

                        override fun onDone(utteranceId: String?) {
                            // Speech completed
                            activity?.runOnUiThread {
                                isPlaying = false
                                currentlyPlayingId = null
                                // Update all message views to reset play button
                                adapter.notifyDataSetChanged()
                            }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            // Error occurred
                            activity?.runOnUiThread {
                                isPlaying = false
                                currentlyPlayingId = null
                                // Update all message views to reset play button
                                adapter.notifyDataSetChanged()
                                showToast("Error in text to speech")
                            }
                        }
                    })
                }
            } else {
                showToast("Text to speech initialization failed")
            }
        }
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "Speech recognizer is ready.")
                isListening = true
                waveformSamples.clear()
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "Speech input has started.")
            }

            override fun onRmsChanged(rmsdB: Float) {
                if (isListening) updateWaveform(rmsdB)
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d(TAG, "Speech input has ended.")
                isListening = false
            }

            override fun onError(error: Int) {
                handleSpeechError(error)
            }

            override fun onResults(results: Bundle?) {
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                    ?.let {
                        binding.userMsgEt.setText(it)
                    }
                showTextContainer()
                results?.let {
                    for (key in it.keySet()) {
                        Log.d(TAG, "Final Results - Key: $key, Value: ${it.get(key)}")
                    }
                    val finalData = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    Log.d(
                        TAG,
                        "Final recognized text: ${finalData?.joinToString(", ") ?: "No final data"}"
                    )
                } ?: Log.d(TAG, "Final results bundle is null")
            }

            override fun onPartialResults(partialResults: Bundle?) {
                partialResults?.let {
                    for (key in it.keySet()) {
                        Log.d(TAG, "Partial Results - Key: $key, Value: ${it.get(key)}")
                    }
                    val partialData = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    Log.d(
                        TAG,
                        "Partial recognized text: ${partialData?.joinToString(", ") ?: "No partial data"}"
                    )
                    binding.userMsgEt.setText(partialData?.joinToString(" "))
                } ?: Log.d(TAG, "Partial results bundle is null")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d(TAG, "Event received: type = $eventType")
            }
        }
    }

    private fun startSpeechToText() {
        Log.d(TAG, "Starting speech-to-text process.")
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 60000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 8000)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer?.startListening(intent)
        showMicContainer()
    }

    private fun stopSpeechRecognizer() {
        speechRecognizer?.stopListening()
    }

    private fun updateWaveform(rmsdB: Float) {
        val amplitude = rmsdB.toInt().coerceAtLeast(0)
        waveformSamples.add(amplitude)
        waveformSeekBar.setSampleFrom(waveformSamples.toIntArray())
    }

    private fun handleSpeechError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client-side error"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_NO_MATCH -> "No recognition result matched"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech input timeout"
            else -> "Unknown error"
        }
        Log.e(TAG, "Speech recognizer error: $errorMessage")
        resetSpeechRecognizerState()
    }

    private fun resetSpeechRecognizerState() {
        binding.userMsgEt.text?.clear()
        showTextContainer()
    }

    private fun showMicContainer() {
        binding.textContainer.gone()
        binding.micContainer.visible()
    }

    private fun showTextContainer() {
        binding.micContainer.gone()
        binding.textContainer.visible()
    }

    private fun playMessageAudio(message: ChatMessage) {
        val messageId = message.id ?: UUID.randomUUID().toString()

        // If already playing this message, stop it
        if (isPlaying && currentlyPlayingId == messageId) {
            stopMessageAudio()
            return
        }

        // If another message is playing, stop it first
        if (isPlaying) {
            stopMessageAudio()
        }

        // Play the new message
        if (isTtsReady) {
            val text = message.content ?: return
            val utteranceId = messageId
            currentlyPlayingId = messageId

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            analyticsHelperUtil.trackButtonClick("play_audio_button")

            // Update the UI to show playing state
            adapter.notifyDataSetChanged()
        } else {
            showToast("Text to speech is not ready yet")
        }
    }

    private fun stopMessageAudio() {
        if (isTtsReady && isPlaying) {
            textToSpeech.stop()
            isPlaying = false
            currentlyPlayingId = null

            // Update the UI to show stopped state
            adapter.notifyDataSetChanged()
            analyticsHelperUtil.trackButtonClick("stop_audio_button")
        }
    }

    override fun onPause() {
        super.onPause()
        stopMessageAudio()
        speechRecognizer?.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer?.apply {
            cancel()
            destroy()
        }
        speechRecognizer = null
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    private fun showSelectTopicTapTarget() {
        TapTargetView.showFor(
            requireActivity(),
            TapTarget.forView(
                binding.recyclerViewButtons,
                "Select Your Topic",
                "Tap here to select a topic and start your interactive learning journey!"
            )
                .outerCircleColor(R.color.light_yellow)
                .titleTextSize(20)
                .descriptionTextSize(16)
                .textColor(R.color.secondary)
                .cancelable(false)
                .transparentTarget(true)
                .targetRadius(120)
                .drawShadow(false),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                }
            }
        )
    }

    fun hideKeyboard() {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Get the current focused view
        val view = activity?.currentFocus
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        } else {
            // If no view is focused, hide the keyboard
            inputMethodManager.hideSoftInputFromWindow(activity?.window?.decorView?.windowToken, 0)
        }
    }

    private fun showExitConfirmationBottomSheet() {
        val bottomSheet = ExitConfirmationBottomSheet {
            requireActivity().finish()
        }
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.markSessionClosed()
    }

    companion object {
        val TAG = "tag"
    }

    override fun onActivityResult(result: PaywallResult) {
        when (result) {
            PaywallResult.Cancelled -> {
                analyticsHelperUtil.logEvent(
                    "paywall_cancelled",
                    mutableMapOf("reason" to "cancelled")
                )
                requireActivity().finish()
            }

            is PaywallResult.Error -> {
                analyticsHelperUtil.logEvent(
                    "paywall_error",
                    mutableMapOf("reason" to result.error)
                )
                binding.apply {
                    loadingLayout.gone()
                    mainLayout.gone()
                    errorLayout.visible()
                }
                showToast("Something went wrong")
            }

            is PaywallResult.Purchased -> {
                analyticsHelperUtil.logEvent(
                    "subscription_purchased",
                    mutableMapOf("customer_info" to result.customerInfo)
                )
                viewModel.getSubscriptionStatus()
            }

            else -> Unit
        }
    }
}
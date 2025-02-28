package com.convoxing.convoxing_customer.ui.call.fragments

import ai.vapi.android.Vapi
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.convoxing.convoxing_customer.BuildConfig
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import com.convoxing.convoxing_customer.databinding.BottomSheetExitConfirmationBinding
import com.convoxing.convoxing_customer.databinding.FragmentCallBinding
import com.convoxing.convoxing_customer.ui.call.viewmodel.CallViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CallFragment : Fragment() {

    private lateinit var binding: FragmentCallBinding
    private var isMuted: Boolean = false
    private lateinit var vapi: Vapi
    private var callTimer: CountDownTimer? = null
    private var callElapsedTime: Long = 0L  // in milliseconds

    @Inject
    lateinit var appPrefManager: AppPrefManager

    private val viewModel: CallViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Handle back press to show bottom sheet instead of exiting immediately
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitBottomSheet()
        }
        // Check if RECORD_AUDIO permission is granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1001)
        } else {
            initializeCall()
        }
    }

    private fun initializeCall() {
        viewModel.coinsBalance = arguments?.getInt("coinBalance") ?: 0

        setListeners()
        setObservers()

        // Initialize Vapi with configuration using the VAPI key from BuildConfig
        val configuration = Vapi.Configuration(publicKey = BuildConfig.VAPI_KEY)
        vapi = Vapi(requireActivity(), lifecycle, configuration)
        lifecycleScope.launch {
            vapi.start(
                metadata = mutableMapOf("userId" to appPrefManager.user.mId),
                assistantId = "f0f76227-f5c4-4009-ae5d-6d11add0d5a2"
            ).onSuccess { response ->
                // Handle successful start if needed
            }.onFailure { error ->
                Log.d("tag", error.message.toString())
                binding.apply {
                    loadingImage.setImageResource(R.drawable.ic_cry_with_cat)
                    connectionTv.text = "Connection failed please retry"
                }
            }
        }


        // Collect and handle events from Vapi
        lifecycleScope.launch {
            vapi.eventFlow.collect { event ->
                when (event) {
                    is Vapi.Event.CallDidStart -> {
                        // Calculate available minutes (each 10 coins = 1 minute)
                        val totalMinutes = viewModel.coinsBalance / 10
                        val totalTimeMs = totalMinutes * 60_000L

                        // Start a countdown timer if there's any time available
                        if (totalTimeMs > 0) {
                            callTimer = object : CountDownTimer(totalTimeMs, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    // Update elapsed time
                                    callElapsedTime = totalTimeMs - millisUntilFinished

                                    val minutesLeft = millisUntilFinished / 60000
                                    val secondsLeft = (millisUntilFinished % 60000) / 1000
                                    binding.callDuration.text =
                                        String.format("%02d:%02d", minutesLeft, secondsLeft)
                                }

                                override fun onFinish() {
                                    binding.callDuration.text = "00:00"
                                    vapi.stop()
//                                    findNavController().navigate(R.id.callSummaryFragment)
                                }
                            }.start()
                        }

                        binding.loadingScreen.gone()
                        binding.mainLayout.visible()
                    }

                    is Vapi.Event.CallDidEnd -> {
                        callTimer?.cancel()
                        // Instead of immediate navigation, show bottom sheet
//                        showExitBottomSheet()
                    }

                    is Vapi.Event.Transcript -> {
                        binding.captionTv.text = event.text
                    }

                    is Vapi.Event.FunctionCall -> {}
                    is Vapi.Event.SpeechUpdate -> {}
                    is Vapi.Event.Metadata -> {}
                    is Vapi.Event.ConversationUpdate -> {}

                    is Vapi.Event.Hang -> {
                        callTimer?.cancel()
                        binding.apply {

                            captionTv.text = "Connection failed retrying"
                        }
                        // Show bottom sheet on hang as well
//                        showExitBottomSheet()
                    }

                    is Vapi.Event.Error -> {
                        if (event.error.contains("network", ignoreCase = true)) {
                            binding.captionTv.text = "Network slow, trying to reconnect..."
                            lifecycleScope.launch {
                                delay(3000)
                                vapi.stop()
                                vapi.start(assistantId = "f0f76227-f5c4-4009-ae5d-6d11add0d5a2")
                                    .onSuccess { response ->
                                        binding.captionTv.text = "Reconnected successfully."
                                    }
                                    .onFailure { error ->
                                        binding.captionTv.text =
                                            "Reconnect failed: ${error.message}"
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showExitBottomSheet() {
        // Inflate the bottom sheet layout using view binding
        val bindingSheet = BottomSheetExitConfirmationBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bindingSheet.root)

        // Configure the bottom sheet based on elapsed call time
        if (callElapsedTime >= 120_000L) {
            bindingSheet.titleTextView.text = "Call Summary"
            bindingSheet.descriptionTextView.text =
                "Your call lasted over 2 minutes. Would you like to view the summary?"
            bindingSheet.btnContinue.text = "View Summary"
            bindingSheet.btnContinue.setOnClickListener {
                bottomSheetDialog.dismiss()
                vapi.stop()
//                findNavController().navigate(R.id.callSummaryFragment)
            }
            bindingSheet.btnExit.text = "I'll see you tomorrow!"
            bindingSheet.btnExit.setOnClickListener {
                bottomSheetDialog.dismiss()
                vapi.stop()
                findNavController().popBackStack()
            }
        } else {
            bindingSheet.titleTextView.text = "Call Not Long Enough"
            bindingSheet.descriptionTextView.text =
                "Your call lasted less than 2 minutes. Would you like to continue the call until 2 minutes for a full summary, or exit?"
            bindingSheet.btnContinue.text = "Continue Call"
            bindingSheet.btnContinue.setOnClickListener {
                bottomSheetDialog.dismiss()
                // Optionally resume call (if call is active, do nothing here)
            }
            bindingSheet.btnExit.text = "Exit"
            bindingSheet.btnExit.setOnClickListener {
                bottomSheetDialog.dismiss()
                vapi.stop()
                findNavController().popBackStack()
            }
        }
        bottomSheetDialog.show()
    }

    private fun setObservers() {
        // Add any observers if needed
    }

    private fun setListeners() {
        binding.apply {
            endCallBtn.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        binding.hangupButton.setOnClickListener {
            showExitBottomSheet()
        }
        binding.microphoneButton.setOnClickListener {
            isMuted = !isMuted
            vapi.toggleMute() // Toggle mute state
            if (isMuted) {
                binding.microphoneButton.setImageResource(R.drawable.ic_mic_off_white_24px)
            } else {
                binding.microphoneButton.setImageResource(R.drawable.ic_mic_white_24px)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initializeCall()
            } else {
                binding.captionTv.text = "Permissions are required to start the call."
                binding.callControls.gone()
            }
        }
    }
}
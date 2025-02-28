package com.convoxing.convoxing_customer.ui.chat.sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.convoxing.convoxing_customer.databinding.BottomSheetExitConfirmationBinding
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExitConfirmationBottomSheet(private val onConfirmExit: () -> Unit) :
    BottomSheetDialogFragment() {

    private var _binding: BottomSheetExitConfirmationBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var analyticsHelperUtil: AnalyticsHelperUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetExitConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up click listeners
        binding.btnExit.setOnClickListener {
            analyticsHelperUtil.trackButtonClick("exit_button")
            onConfirmExit.invoke()
            dismiss()
        }
        binding.btnContinue.setOnClickListener {
            analyticsHelperUtil.trackButtonClick("continue_button")
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
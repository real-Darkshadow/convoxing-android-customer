package com.convoxing.convoxing_customer.ui.analysis.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.databinding.FragmentAnalysisTabViewBinding
import com.convoxing.convoxing_customer.ui.analysis.adapter.AnalysisTabViewAdapter
import com.convoxing.convoxing_customer.ui.analysis.viewmodel.AnalysisViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.changeStatusBarColor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalysisTabViewFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAnalysisTabViewBinding? = null
    val binding: FragmentAnalysisTabViewBinding get() = _binding!!
    private val analysisViewModel: AnalysisViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAnalysisTabViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isSameMessage =
            (arguments?.getString("messageId") ?: "") == analysisViewModel.userMessageId
        analysisViewModel.sessionId = arguments?.getString("sessionId") ?: ""
        analysisViewModel.currentMessageForAnalysis = arguments?.getString("message") ?: ""
        analysisViewModel.modelMessageForAnalysis = arguments?.getString("modelMessage") ?: ""
        analysisViewModel.userMessageId = arguments?.getString("messageId") ?: ""
        changeStatusBarColor(R.color.off_white, 1)
        val adapter = AnalysisTabViewAdapter(
            childFragmentManager,
            lifecycle,
            binding.tabLayout.tabCount
        )
        if (!isSameMessage) {
            analysisViewModel.getGrammarAnalysis()
            analysisViewModel.getVocabAnalysis()
        }
        analysisViewModel.getBetterAnswer()

        with(binding) {
            binding.viewpager.adapter = adapter
            TabLayoutMediator(tabLayout, binding.viewpager) { tab, position ->
                tab.text = adapter.getPageTitle(position)
            }.attach()
            viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabLayout.selectTab(tabLayout.getTabAt(position))
                }
            })

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewpager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) = Unit
                override fun onTabReselected(tab: TabLayout.Tab) = Unit
            })

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        dialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    companion object {
        fun newInstance(
            message: String,
            modelMessage: String,
            messageId: String,
            sessionId: String
        ): AnalysisTabViewFragment {
            val fragment = AnalysisTabViewFragment()
            val args = Bundle().apply {
                putString("message", message)
                putString("modelMessage", modelMessage)
                putString("messageId", messageId)
                putString("sessionId", sessionId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
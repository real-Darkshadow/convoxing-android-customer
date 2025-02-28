package com.convoxing.convoxing_customer.ui.practice.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.databinding.FragmentPracticeBinding
import com.convoxing.convoxing_customer.ui.analysis.fragments.AnalysisTabViewFragment
import com.convoxing.convoxing_customer.ui.practice.adapter.PracticeFragPagerAdapter
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.changeStatusBarColor
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PracticeFragment : Fragment() {

    private var _binding: FragmentPracticeBinding? = null
    private val binding: FragmentPracticeBinding get() = _binding!!

    @Inject
    lateinit var analyticsHelperUtil: AnalyticsHelperUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeStatusBarColor(R.color.off_white, 1)
        analyticsHelperUtil.trackScreenView("Practice Screen")
        setListeners()
        val adapter = PracticeFragPagerAdapter(this)
        binding.viewpager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // No action needed when a tab is unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // No action needed when a tab is reselected
            }
        })
    }

    private fun setListeners() {
        binding.apply {
            historyBtn.setOnClickListener {
                analyticsHelperUtil.trackButtonClick("history_button")
                val sessionHistoryBottomSheet =
                    SessionHistoryFragment.newInstance()
                sessionHistoryBottomSheet.show(
                    requireActivity().supportFragmentManager,
                    sessionHistoryBottomSheet.tag
                )
            }
        }
    }

}
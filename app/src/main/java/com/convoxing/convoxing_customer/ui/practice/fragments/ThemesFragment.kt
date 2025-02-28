package com.convoxing.convoxing_customer.ui.practice.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.convoxing.convoxing_customer.databinding.FragmentThemesBinding
import com.convoxing.convoxing_customer.ui.chat.activity.ChatActivity
import com.convoxing.convoxing_customer.ui.practice.adapter.PracticeFragAdapter
import com.convoxing.convoxing_customer.ui.practice.viewmodel.PracticeViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.convoxing.convoxing_customer.utils.Status
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import com.revenuecat.purchases.ui.revenuecatui.ExperimentalPreviewRevenueCatUIPurchasesAPI
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallActivityLauncher
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallResult
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallResultHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(ExperimentalPreviewRevenueCatUIPurchasesAPI::class)
class ThemesFragment : Fragment(), PaywallResultHandler {

    private var _binding: FragmentThemesBinding? = null
    private val binding get() = _binding!!
    private lateinit var situationsAdapter: PracticeFragAdapter
    private lateinit var paywallActivityLauncher: PaywallActivityLauncher

    @Inject
    lateinit var analyticsHelperUtil: AnalyticsHelperUtil

    private val viewModel: PracticeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentThemesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paywallActivityLauncher = PaywallActivityLauncher(this, this)
        analyticsHelperUtil.trackScreenView("Themes Screen")
        viewModel.getSituations()
        initAdapter()
        setObservers()
    }

    private fun launchPaywallActivity() {
        paywallActivityLauncher.launch()
    }

    override fun onActivityResult(result: PaywallResult) {}

    private fun setObservers() {
        viewModel.situationsResult.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.themes?.let { situations ->
                        situationsAdapter.updateItem(situations)
                        binding.apply {
                            animationView.gone()
                            errorTv.gone()
                            situationsRecycler.visible()
                        }
                    }

                }

                Status.ERROR -> {
                    binding.apply {
                        animationView.gone()
                        errorTv.visible()
                        situationsRecycler.gone()
                    }
                }

                Status.LOADING -> {
                    binding.apply {
                        animationView.visible()
                        errorTv.gone()
                        situationsRecycler.gone()
                    }
                }

                Status.IDLE -> Unit

            }
        }

    }

    private fun initAdapter() {
        situationsAdapter = PracticeFragAdapter(arrayListOf()) { data ->
            analyticsHelperUtil.logEvent(
                "theme_card",
                mutableMapOf(
                    "topic_name" to data.loadingScreenInfo.title,
                    "topic_id" to data.id,
                    "category" to "Theme",
                    "is_clicked" to true
                )
            )
            startActivity(Intent(requireContext(), ChatActivity::class.java).also {
                it.putExtra("topicId", data.id)
                it.putExtra("topicName", data.loadingScreenInfo.title)
                it.putExtra("category", "Theme")
            })
        }
        binding.situationsRecycler.adapter = situationsAdapter
        binding.situationsRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

}
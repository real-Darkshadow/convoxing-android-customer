package com.convoxing.convoxing_customer.ui.call.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.remote.models.summary.Summary
import com.convoxing.convoxing_customer.databinding.FragmentCallBinding
import com.convoxing.convoxing_customer.databinding.FragmentCallSummaryBinding
import com.convoxing.convoxing_customer.databinding.ScoreCardItemBinding
import com.convoxing.convoxing_customer.ui.analysis.viewmodel.AnalysisViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.changeStatusBarColor
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.convoxing.convoxing_customer.utils.Status
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CallSummaryFragment : Fragment() {
    private lateinit var binding: FragmentCallSummaryBinding

    private val viewModel: AnalysisViewModel by viewModels()

    @Inject
    lateinit var analyticsHelperUtil: AnalyticsHelperUtil


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCallSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sessionId = arguments?.getString("sessionId") ?: ""
        val isHistory = arguments?.getBoolean("isHistory") ?: false
        changeStatusBarColor(R.color.alice_blue, 1)
        viewModel.getSessionSummary(sessionId)
        if (isHistory) binding.selectTopicButton.gone()
        analyticsHelperUtil.trackScreenView("Chat Summary")
        setObservers()
        setListeners()
        setupBackPressHandler(isHistory)
    }

    private fun setListeners() {
        binding.selectTopicButton.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun setupBackPressHandler(isHistory: Boolean) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    analyticsHelperUtil.logEvent(
                        "summary_back_pressed", mapOf(
                            "is_history" to isHistory.toString()
                        )
                    )

                    if (isHistory) {
                        // Normal back navigation for history mode
                        this.remove() // Remove callback to allow normal back navigation
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    } else {
                        // Finish activity for non-history mode
                        requireActivity().finish()
                    }
                }
            }
        )
    }

    private fun setObservers() {
        viewModel.sessionSummaryResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.loadingLayout.gone()
                    binding.mainLayout.visible()
                    it.data?.chatSummary?.let { summary ->
                        with(binding) {
                            scoreProgress.progress = summary.overallStats.score.toInt()
                            scoreText.text = "${summary.overallStats.score.toInt()}/10"
                            grammarCard.titleText.text = summary.grammarStats.title
                            grammarCard.contentText.text = summary.grammarStats.description
                            grammarCard.scoreIndicator.progress =
                                summary.grammarStats.averageScore.toInt()
                            vocabularyCard.titleText.text = summary.vocabularyStats.title
                            vocabularyCard.contentText.text = summary.vocabularyStats.description
                            vocabularyCard.scoreIndicator.progress =
                                summary.vocabularyStats.averageScore.toInt()

                            setupCard(grammarCard)
                            setupCard(vocabularyCard)
                        }

                    }
                }

                Status.LOADING -> {
                    binding.mainLayout.gone()
                    binding.loadingLayout.visible()
                }

                Status.ERROR -> {
                    binding.mainLayout.gone()
                    binding.errorLayout.visible()
                }

                Status.IDLE -> Unit
            }
        }
    }


    private fun setupCard(cardInclude: ScoreCardItemBinding) {
        with(cardInclude) {
            headerContainer.setOnClickListener {
                val isExpanded = contentText.visibility == View.VISIBLE
                toggleCardExpansion(!isExpanded, contentText, expandIcon, viewText)
            }
        }
    }

    private fun toggleCardExpansion(
        expand: Boolean,
        contentView: TextView,
        expandIcon: ImageView,
        viewText: TextView
    ) {
        // Animation duration
        val duration = 300L

        if (expand) {
            contentView.visibility = View.VISIBLE
            viewText.text = "Hide"
            expandIcon.animate().rotation(180f).setDuration(duration).start()
        } else {
            contentView.visibility = View.GONE
            viewText.text = "View"
            expandIcon.animate().rotation(0f).setDuration(duration).start()
        }
    }
}
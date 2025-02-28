package com.convoxing.convoxing_customer.ui.analysis.fragments

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.databinding.FragmentAnalysisGrammarBinding
import com.convoxing.convoxing_customer.databinding.FragmentAnalysisTabViewBinding
import com.convoxing.convoxing_customer.ui.analysis.adapter.GrammarAdapter
import com.convoxing.convoxing_customer.ui.analysis.viewmodel.AnalysisViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.isNotNullOrBlank
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.convoxing.convoxing_customer.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalysisGrammarFragment : Fragment() {

    private var _binding: FragmentAnalysisGrammarBinding? = null
    val binding: FragmentAnalysisGrammarBinding get() = _binding!!
    private val analysisViewModel: AnalysisViewModel by activityViewModels()

    private lateinit var mAdapter: GrammarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAnalysisGrammarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setObservers()
    }

    private fun setupAdapter() {
        mAdapter = GrammarAdapter(requireContext(), arrayListOf())
        binding.grammarRv.adapter = mAdapter
        binding.grammarRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }


    private fun setObservers() {
        analysisViewModel.grammarAnalysisResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it?.data?.grammarAnalysis?.let { data ->
                        binding.animationView.gone()
                        binding.mainLayout.visible()
                        if (data.isEmpty()) {
                            binding.grammarRv.gone()
                            binding.wellDoneImg.visible()
                            binding.originalText.text = analysisViewModel.currentMessageForAnalysis
                            return@let
                        }
                        val userMessage = analysisViewModel.currentMessageForAnalysis ?: ""
                        val spannableMessage = SpannableString(userMessage)

                        data.forEach { item ->
                            val wrongPortion = item.wrongPortion
                            if (wrongPortion != null) {
                                val startIndex = userMessage.indexOf(wrongPortion)
                                if (startIndex != -1) {
                                    val endIndex = startIndex + wrongPortion.length
                                    spannableMessage.setSpan(
                                        UnderlineSpan(),
                                        startIndex,
                                        endIndex,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                }
                            }
                        }
                        binding.originalText.text = spannableMessage
                        if (data.isNotEmpty() && data[0].wrongPortion.isNotNullOrBlank()) mAdapter.addAnalysis(
                            data
                        )
                    }

                }

                Status.ERROR -> {}
                Status.LOADING -> {
                    binding.animationView.visible()
                    binding.mainLayout.gone()
                }

                Status.IDLE -> {}
            }
        }
        analysisViewModel.betterAnswerResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it?.data?.betterAnswer?.let { data ->
                        if (data.isNotNullOrBlank()) {
                            binding.suggestionHeader.visible()
                            binding.suggestedText.visible()
                            binding.suggestedText.text = data
                        }
                    }

                }

                Status.ERROR -> {
                    binding.suggestionHeader.gone()
                    binding.suggestedText.gone()
                }

                Status.LOADING -> {
                    binding.suggestionHeader.gone()
                    binding.suggestedText.gone()
                }

                Status.IDLE -> {}
            }
        }
    }

}
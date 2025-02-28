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
import androidx.recyclerview.widget.LinearLayoutManager
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.remote.models.Replacements
import com.convoxing.convoxing_customer.databinding.FragmentAnalysisGrammarBinding
import com.convoxing.convoxing_customer.databinding.FragmentAnalysisVocabBinding
import com.convoxing.convoxing_customer.ui.analysis.adapter.GrammarAdapter
import com.convoxing.convoxing_customer.ui.analysis.adapter.VocabAdapter
import com.convoxing.convoxing_customer.ui.analysis.viewmodel.AnalysisViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.convoxing.convoxing_customer.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalysisVocabFragment : Fragment() {

    private var _binding: FragmentAnalysisVocabBinding? = null
    val binding: FragmentAnalysisVocabBinding get() = _binding!!
    private val analysisViewModel: AnalysisViewModel by activityViewModels()

    private lateinit var mAdapter: VocabAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAnalysisVocabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setObservers()
    }

    private fun setupAdapter() {
        mAdapter = VocabAdapter(requireContext(), arrayListOf())
        binding.vocabRv.adapter = mAdapter
        binding.vocabRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }


    private fun setObservers() {
        analysisViewModel.vocabAnalysisResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it?.data?.vocabAnalysis?.let { data ->
                        binding.animationView.gone()
                        binding.mainLayout.visible()
                        val userMessage = analysisViewModel.currentMessageForAnalysis
                        val spannableMessage = SpannableString(userMessage)
                        if (data.wordReplacements.isNotEmpty()) {
                            binding.wellDoneImg.gone()
                            binding.vocabRv.visible()
                            val orgData = data.wordReplacements
                            val replacements = arrayListOf<Replacements>()
                            orgData.forEach { wordReplacement ->
                                wordReplacement.replacements.forEach { item ->
                                    replacements.add(item)
                                    val wrongPortion = item.word
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
                            mAdapter.addAnalysis(replacements)
                        } else {
                            binding.apply {
                                wellDoneImg.visible()
                                vocabRv.gone()
                            }
                        }
                        binding.originalText.text = spannableMessage

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
    }
}
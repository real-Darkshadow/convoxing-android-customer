package com.convoxing.convoxing_customer.ui.practice.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.databinding.FragmentSessionHistoryBinding
import com.convoxing.convoxing_customer.ui.analysis.fragments.AnalysisTabViewFragment
import com.convoxing.convoxing_customer.ui.analysis.viewmodel.AnalysisViewModel
import com.convoxing.convoxing_customer.ui.chat.activity.ChatActivity
import com.convoxing.convoxing_customer.ui.practice.adapter.SessionHistoryAdapter
import com.convoxing.convoxing_customer.ui.practice.viewmodel.PracticeViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.changeStatusBarColor
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.convoxing.convoxing_customer.utils.Status
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import com.google.android.a.c
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SessionHistoryFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentSessionHistoryBinding? = null
    val binding: FragmentSessionHistoryBinding get() = _binding!!
    private val viewModel: PracticeViewModel by viewModels()
    private val anviewModel: AnalysisViewModel by viewModels()

    private lateinit var mAdapter: SessionHistoryAdapter

    @Inject
    lateinit var analyticsHelperUtil: AnalyticsHelperUtil


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSessionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeStatusBarColor(R.color.off_white, 1)
        viewModel.getSessionHistory()
        analyticsHelperUtil.trackScreenView("Chat History")
        initAdapter()
        setObservers()

    }

    private fun initAdapter() {
        mAdapter = SessionHistoryAdapter(arrayListOf()) { sessionHistory ->
            startActivity(Intent(requireContext(), ChatActivity::class.java).also {
                it.putExtra("isHistory", true)
                it.putExtra("currentSessionId", sessionHistory._id)
                it.putExtra("topicName", sessionHistory.sessionName)
            })
        }
        binding.sessionRv.adapter = mAdapter
        binding.sessionRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun setObservers() {
        viewModel.sessionHistoryResult.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    binding.progressBar.gone()
                    binding.sessionRv.visible()
                    resource.data?.chatSessions?.let { chatSession ->
                        if (chatSession.isEmpty()) {
                            binding.errorLayout.visible()
                            binding.errorText.text = "Oops! No session found"
                        } else {
                            mAdapter.updateItem(chatSession)
                        }

                    }
                }

                Status.ERROR -> {
                    binding.progressBar.gone()
                    binding.errorLayout.visible()
                }

                Status.LOADING -> {
                    binding.progressBar.visible()

                }

                else -> {}
            }
        }
    }


    companion object {
        fun newInstance(): SessionHistoryFragment {
            val fragment = SessionHistoryFragment()
            return fragment
        }
    }
}
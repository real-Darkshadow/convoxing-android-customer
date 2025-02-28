package com.convoxing.convoxing_customer.ui.practice.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.convoxing.convoxing_customer.databinding.FragmentRolePlayBinding
import com.convoxing.convoxing_customer.ui.chat.activity.ChatActivity
import com.convoxing.convoxing_customer.ui.practice.adapter.PracticeFragAdapter
import com.convoxing.convoxing_customer.ui.practice.viewmodel.PracticeViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.convoxing.convoxing_customer.utils.Status
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RolePlayFragment : Fragment() {

    private var _binding: FragmentRolePlayBinding? = null
    private val binding get() = _binding!!
    private lateinit var rolePlayAdapter: PracticeFragAdapter

    @Inject
    lateinit var analyticsHelperUtil: AnalyticsHelperUtil

    private val viewModel: PracticeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRolePlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analyticsHelperUtil.trackScreenView("RolePlay Screen")
        viewModel.getRolePlays()
        initAdapter()
        setObservers()
    }

    private fun setObservers() {
        viewModel.rolePlayResult.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.situations?.let { situations ->
                        rolePlayAdapter.updateItem(situations)
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
        rolePlayAdapter = PracticeFragAdapter(arrayListOf()) { data ->
            analyticsHelperUtil.logEvent(
                "role_play_card",
                mutableMapOf(
                    "topic_name" to data.loadingScreenInfo.title,
                    "topic_id" to data.id,
                    "category" to "Roleplay",
                    "is_clicked" to true
                )
            )
            startActivity(Intent(requireContext(), ChatActivity::class.java).also {
                it.putExtra("topicId", data.id)
                it.putExtra("topicName", data.loadingScreenInfo.title)
                it.putExtra("category", "Roleplay")
            })
        }
        binding.situationsRecycler.adapter = rolePlayAdapter
        binding.situationsRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }
}
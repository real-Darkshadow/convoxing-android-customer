package com.convoxing.convoxing_customer.ui.practice.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.convoxing.convoxing_customer.databinding.FragmentThemesBinding
import com.convoxing.convoxing_customer.ui.practice.adapter.PracticeFragAdapter
import com.convoxing.convoxing_customer.ui.practice.viewmodel.PracticeViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.convoxing.convoxing_customer.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThemesFragment : Fragment() {

    private var _binding: FragmentThemesBinding? = null
    private val binding get() = _binding!!
    private lateinit var situationsAdapter: PracticeFragAdapter

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
        viewModel.getSituations()
        initAdapter()
        setObservers()
    }

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
        situationsAdapter = PracticeFragAdapter(arrayListOf()) {}
        binding.situationsRecycler.adapter = situationsAdapter
        binding.situationsRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

}
package com.convoxing.convoxing_customer.ui.call.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.databinding.FragmentCallPracticeBinding
import com.convoxing.convoxing_customer.ui.call.bottomsheet.CoinPackagesBottomSheet
import com.convoxing.convoxing_customer.ui.call.viewmodel.CallViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.convoxing.convoxing_customer.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallPracticeFragment : Fragment() {

    private var _binding: FragmentCallPracticeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CallViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCallPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkUserCallCoins()
    }

    private fun setupObservers() {
        viewModel.checkUserCallCoinsResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.mainLayout.visible()
                    binding.loadingLayout.gone()
                    it.data?.user?.let { user ->
                        binding.tvCoins.text = user.coins.toString()
                        viewModel.coinsBalance = user.coins
                    }
                }

                Status.ERROR -> {}
                Status.LOADING -> {
                    binding.mainLayout.gone()
                    binding.loadingLayout.visible()
                }

                else -> {}
            }
        }
    }

    private fun setupListeners() {
        binding.btnStartCall.setOnClickListener {
            // Define custom navigation animations
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_left)
                .build()
            // Create a bundle with extra data
            val bundle = Bundle().apply {
                putInt("coinBalance", viewModel.coinsBalance)  // example extra data
            }

            findNavController().navigate(R.id.callFragment, bundle, navOptions)
        }

        binding.btnBuyCoins.setOnClickListener {
            showCoinPackagesBottomSheet()
        }
    }

    private fun showCoinPackagesBottomSheet() {
        val bottomSheet = CoinPackagesBottomSheet { coinsAdded ->
            // Update the UI after successful purchase
            viewModel.coinsBalance += coinsAdded
            binding.tvCoins.text = viewModel.coinsBalance.toString()

            // Refresh data from server to ensure it's in sync
            viewModel.checkUserCallCoins()
        }

        bottomSheet.show(parentFragmentManager, CoinPackagesBottomSheet.TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
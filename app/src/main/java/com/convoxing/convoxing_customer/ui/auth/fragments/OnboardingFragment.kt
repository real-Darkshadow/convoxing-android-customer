package com.convoxing.convoxing_customer.ui.auth.fragments

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.convoxing.convoxing_customer.MainActivity
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import com.convoxing.convoxing_customer.data.models.OnboardingScreenData
import com.convoxing.convoxing_customer.databinding.FragmentOnboardingBinding
import com.convoxing.convoxing_customer.ui.auth.adapter.OnboardingDetailsAdapter
import com.convoxing.convoxing_customer.ui.auth.viewmodel.AuthViewModel
import com.convoxing.convoxing_customer.ui.auth.viewmodel.AuthViewModel.DetailScreenType
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.showToast
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.convoxing.convoxing_customer.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()


    @Inject
    lateinit var appPrefManager: AppPrefManager

    private lateinit var currentScreen: DetailScreenType
    private lateinit var mAdapter: OnboardingDetailsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleBackPress()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        binding.apply {
            nxtBtn.setOnClickListener {
                when (currentScreen) {
                    DetailScreenType.NameScreen -> {
                        if (nameEt.text.isNullOrBlank()) {
                            showToast("Please enter your name")
                            return@setOnClickListener
                        }
                        viewModel.name = nameEt.text.toString()

                        viewModel.currentScreen.postValue(
                            DetailScreenType.GoalScreen
                        )
                    }

                    DetailScreenType.GoalScreen -> {
                        if (viewModel.goal.isBlank()) {
                            showToast("Please select a goal")
                            return@setOnClickListener
                        }
                        viewModel.addUserDetails()
                    }

                    DetailScreenType.ProfessionScreen -> {
                        if (viewModel.profession.isBlank()) {
                            showToast("Please select your profession")
                            return@setOnClickListener
                        }
                        viewModel.addUserDetails()
                    }
                }
            }
        }
    }

    private fun setupAdapter() {
        mAdapter = OnboardingDetailsAdapter(arrayListOf()) {
            when (currentScreen) {
                DetailScreenType.GoalScreen -> viewModel.goal = it
                DetailScreenType.ProfessionScreen -> viewModel.profession = it
                else -> Unit
            }
        }
        binding.recyclerView.adapter = mAdapter
    }

    private fun setObservers() {
        viewModel.currentScreen.observe(viewLifecycleOwner) { screen ->
            currentScreen = screen
            when (screen) {
                DetailScreenType.NameScreen -> setupNameScreen()
                DetailScreenType.GoalScreen -> setupGoalScreen()
                DetailScreenType.ProfessionScreen -> setupProfessionScreen()
            }
        }
        viewModel.addUserDetailsResult.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    appPrefManager.isUserLoggedIn = true
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finishAffinity()
                }

                Status.ERROR -> {

                }

                Status.LOADING -> {

                }

                Status.IDLE -> {

                }

            }
        }
    }

    private fun setupNameScreen() {
        binding.apply {
            nameContainer.visible()
            recyclerView.gone()
            headline.text = "What is your name?"
            subHeadline.text = "Write your name below"
        }
    }

    private fun setupGoalScreen() {
        binding.apply {
            nameContainer.gone()
            binding.recyclerView.apply {
                this.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                if (itemDecorationCount == 0) {
                    val bottomMarginInPx = resources.getDimensionPixelSize(R.dimen.bottom_margin)
                    addItemDecoration(BottomMarginItemDecoration(bottomMarginInPx))
                }
                this.visible()
            }
            mAdapter.clearData()
            mAdapter.updateData(goals)
            nxtBtn.text = "Finish"
            headline.text = "What is your goal?"
            subHeadline.text = "Select a goal from below"
        }
    }

    // TODO: Unable to apply flexbox layout
    private fun setupProfessionScreen() {
        binding.apply {
            nameContainer.gone()
            recyclerView.visible()
            binding.recyclerView.apply {
                this.layoutManager = GridLayoutManager(requireContext(), 2)
                this.visible()
            }
            mAdapter.clearData()
            mAdapter.updateData(profession)
            nxtBtn.text = "Finish"
            headline.text = "What is your profession?"
            subHeadline.text = "Select your profession from below"
        }
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when (currentScreen) {
                        DetailScreenType.NameScreen -> {
                            requireActivity().finishAffinity()
                        }

                        DetailScreenType.GoalScreen -> {
                            viewModel.goal = ""
                            viewModel.currentScreen.postValue(DetailScreenType.NameScreen)
                        }

                        DetailScreenType.ProfessionScreen -> {
                            viewModel.profession = ""
                            viewModel.goal = ""
                            viewModel.currentScreen.postValue(DetailScreenType.GoalScreen)
                        }
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val goals = arrayListOf(
            OnboardingScreenData("Crack Interviews", null),
            OnboardingScreenData("Professional Communication", null),
            OnboardingScreenData("Higher Studies or IELTS", null),
            OnboardingScreenData("Casual or fun", null)
        )
        val profession = arrayListOf(
            OnboardingScreenData("Teacher", null),
            OnboardingScreenData("Working professional", null),
            OnboardingScreenData("Student", null),
            OnboardingScreenData("Govt. Official", null),
            OnboardingScreenData("Freelancer", null),
            OnboardingScreenData("Artist", null),
            OnboardingScreenData("Other", null)
        )
    }

    inner class BottomMarginItemDecoration(private val bottomMargin: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)) {
                outRect.bottom = bottomMargin
            }
        }
    }
}
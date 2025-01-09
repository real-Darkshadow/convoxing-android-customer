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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.convoxing.convoxing_customer.ui.home.activity.MainActivity
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
import com.convoxing.convoxing_customer.utils.Resource
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
                            DetailScreenType.AgeScreen
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setupAdapter() {
        mAdapter = OnboardingDetailsAdapter(arrayListOf()) {
            when (currentScreen) {
                DetailScreenType.AgeScreen -> {
                    viewModel.age = it

                    viewModel.currentScreen.postValue(
                        DetailScreenType.EnglishLevelScreen
                    )
                }

                DetailScreenType.EnglishLevelScreen -> {
                    viewModel.englishLevel = it

                    viewModel.updateUserDetails()
                }

                else -> Unit
            }
        }
        binding.recyclerView.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            if (itemDecorationCount == 0) {
                val bottomMarginInPx = resources.getDimensionPixelSize(R.dimen.bottom_margin)
                addItemDecoration(BottomMarginItemDecoration(bottomMarginInPx))
            }
        }
        binding.recyclerView.adapter = mAdapter
    }

    private fun setObservers() {
        viewModel.currentScreen.observe(viewLifecycleOwner) { screen ->
            currentScreen = screen
            when (screen) {
                DetailScreenType.NameScreen -> setupNameScreen()
                DetailScreenType.AgeScreen -> setupAgeScreen()
                DetailScreenType.EnglishLevelScreen -> setupEnglishLevelScreen()
                DetailScreenType.CraftingExperienceScreen -> setupCraftingScreen()
            }
        }
        viewModel.updateUserDataResult.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.user?.let { user ->
                        viewModel.setUserPref(user)
                    }
                    appPrefManager.isUserLoggedIn = true
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finishAffinity()
                }

                Status.ERROR -> {
                    viewModel.updateUserDataResult.postValue(Resource.idle())
                    viewModel.currentScreen.postValue(DetailScreenType.EnglishLevelScreen)
                    showToast("Something Went Wrong")
                }

                Status.LOADING -> {
                    viewModel.currentScreen.postValue(DetailScreenType.CraftingExperienceScreen)
                }

                Status.IDLE -> Unit

            }
        }
    }

    private fun setupNameScreen() {
        binding.apply {
            nameContainer.visible()
            animationView.gone()
            recyclerView.gone()
            headline.text = "So nice to meet you."
            subHeadline.text = "What's your name ?"
            nxtBtn.text = "Next"
            nxtBtn.visible()
        }
    }

    private fun setupAgeScreen() {
        binding.apply {
            nameContainer.gone()
            animationView.gone()
            mAdapter.clearData()
            recyclerView.visible()
            mAdapter.updateData(ageList)
            nxtBtn.gone()
            headline.text = "Hi ${viewModel.name},"
            subHeadline.text = "how old are you ?"
        }
    }

    private fun setupEnglishLevelScreen() {
        binding.apply {
            nameContainer.gone()
            animationView.gone()
            recyclerView.visible()
            mAdapter.clearData()
            mAdapter.updateData(englishLevelList)
//            nxtBtn.text = "Finish"
            nxtBtn.gone()
            headline.text = "On a scale of 1-5,"
            subHeadline.text = "how's your English ?"
        }
    }

    private fun setupCraftingScreen() {
        binding.apply {
            nameContainer.gone()
            recyclerView.gone()
            nxtBtn.gone()
            animationView.visible()
            headline.text = "We are crafting"
            subHeadline.text = "your experience..."

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

                        DetailScreenType.AgeScreen -> {
                            viewModel.age = ""
                            viewModel.currentScreen.postValue(DetailScreenType.NameScreen)
                        }

                        DetailScreenType.EnglishLevelScreen -> {
                            viewModel.englishLevel = ""
                            viewModel.age = ""
                            viewModel.currentScreen.postValue(DetailScreenType.AgeScreen)
                        }

                        DetailScreenType.CraftingExperienceScreen -> Unit
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val ageList = arrayListOf(
            OnboardingScreenData("Under 12", null),
            OnboardingScreenData("12 - 15", null),
            OnboardingScreenData("16 - 18", null),
            OnboardingScreenData("19 - 24", null),
            OnboardingScreenData("25 - 34", null),
            OnboardingScreenData("35 - 44", null),
            OnboardingScreenData("45 - 54", null),
            OnboardingScreenData("55 - 64", null),
            OnboardingScreenData("65 or older", null)
        )
        val englishLevelList = arrayListOf(
            OnboardingScreenData("1 - Beginner", null),
            OnboardingScreenData("2 - Basic", null),
            OnboardingScreenData("3 - Intermediate", null),
            OnboardingScreenData("4 - Advanced", null),
            OnboardingScreenData("5 - Professional", null),
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
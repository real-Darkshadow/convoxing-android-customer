package com.convoxing.convoxing_customer.ui.auth.fragments

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
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
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import com.posthog.PostHog
import com.revenuecat.purchases.Purchases
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

    @Inject
    lateinit var analyticsHelper: AnalyticsHelperUtil

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
        analyticsHelper.trackScreenView("Onboarding Screen")
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
                    viewModel.currentScreen.postValue(DetailScreenType.PainPointScreen)
                }

                DetailScreenType.PainPointScreen -> {
                    viewModel.painPoint = it
                    viewModel.currentScreen.postValue(DetailScreenType.GoalScreen)
                }

                DetailScreenType.GoalScreen -> {
                    viewModel.goal = it
                    viewModel.currentScreen.postValue(DetailScreenType.AvailabilityScreen)
                }

                DetailScreenType.AvailabilityScreen -> {
                    viewModel.availability = it
                    viewModel.currentScreen.postValue(DetailScreenType.PastExperienceScreen)
                }

                DetailScreenType.PastExperienceScreen -> {
                    viewModel.pastExperience = it
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
                DetailScreenType.PainPointScreen -> setupPainPointScreen()
                DetailScreenType.GoalScreen -> setupGoalScreen()
                DetailScreenType.AvailabilityScreen -> setupAvailabilityScreen()
                DetailScreenType.PastExperienceScreen -> setupPastExperienceScreen()
                DetailScreenType.CraftingExperienceScreen -> setupCraftingScreen()
            }
        }
        viewModel.updateUserDataResult.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.user?.let { user ->
                        viewModel.setUserPref(user)
                        Purchases.sharedInstance.setAttributes(mapOf("age" to user.ageGroup))
                        Purchases.sharedInstance.setEmail(user.email)
                        Purchases.sharedInstance.setDisplayName(user.name)
                        analyticsHelper.logEvent(
                            "onboarding_complete",
                            mutableMapOf(
                                "email" to user.email,
                                "age_group" to user.ageGroup,
                                "english_level" to user.englishLevel,
                                "pain_point" to viewModel.painPoint,
                                "goal" to viewModel.goal,
                                "availability" to viewModel.availability,
                                "past_experience" to viewModel.pastExperience
                            )
                        )

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
            headline.text = "Welcome to Speakana!"
            subHeadline.text = "Let's get to know you. What's your name?"
            nxtBtn.text = "Next"
            nxtBtn.visible()
        }
    }

    private fun setupAgeScreen() {
        analyticsHelper.trackScreenView("Age Screen")
        binding.apply {
            nameContainer.gone()
            animationView.gone()
            mAdapter.clearData()
            recyclerView.visible()
            mAdapter.updateData(ageList)
            nxtBtn.gone()
            headline.text = "Nice to meet you, ${viewModel.name}!"
            subHeadline.text = "Could you tell us your age group?"
        }
    }

    private fun setupEnglishLevelScreen() {
        analyticsHelper.trackScreenView("English Level Screen")
        binding.apply {
            nameContainer.gone()
            animationView.gone()
            recyclerView.visible()
            mAdapter.clearData()
            mAdapter.updateData(englishLevelList)
            nxtBtn.gone()
            headline.text = "Your current English proficiency"
            subHeadline.text = "This helps us customize your learning journey"
        }
    }

    private fun setupPainPointScreen() {
        analyticsHelper.trackScreenView("Pain Points Screen")
        binding.apply {
            nameContainer.gone()
            animationView.gone()
            recyclerView.visible()
            mAdapter.clearData()
            mAdapter.updateData(painPointList)
            nxtBtn.gone()
            headline.text = "What's your biggest struggle?"
            subHeadline.text = "Our AI will focus on helping you overcome it"
        }
    }

    private fun setupGoalScreen() {
        analyticsHelper.trackScreenView("Goals Screen")
        binding.apply {
            nameContainer.gone()
            animationView.gone()
            recyclerView.visible()
            mAdapter.clearData()
            mAdapter.updateData(goalsList)
            nxtBtn.gone()
            headline.text = "What's your primary goal?"
            subHeadline.text = "Select what you want to achieve with Speakana"
        }
    }

    private fun setupAvailabilityScreen() {
        analyticsHelper.trackScreenView("Availability Screen")
        binding.apply {
            nameContainer.gone()
            animationView.gone()
            recyclerView.visible()
            mAdapter.clearData()
            mAdapter.updateData(availabilityList)
            nxtBtn.gone()
            headline.text = "How much time can you practice?"
            subHeadline.text = "We'll recommend a schedule that works for you"
        }
    }

    private fun setupPastExperienceScreen() {
        analyticsHelper.trackScreenView("Past Experience Screen")
        binding.apply {
            nameContainer.gone()
            animationView.gone()
            recyclerView.visible()
            mAdapter.clearData()
            mAdapter.updateData(pastExperienceList)
            nxtBtn.gone()
            headline.text = "What hasn't worked for you before?"
            subHeadline.text = "Our AI adapts to avoid traditional learning pitfalls"
        }
    }

    private fun setupCraftingScreen() {
        analyticsHelper.trackScreenView("Crafting Experience Screen")
        binding.apply {
            nameContainer.gone()
            recyclerView.gone()
            nxtBtn.gone()

            // Enhanced loading animation
            animationView.setAnimation(R.raw.onboarding_loading_animation)
            animationView.visible()

            headline.text = "Building your personalized learning path"

            // Create a list of AI processing messages
            val aiMessages = listOf(
                "Analyzing your speaking challenges...",
                "Creating conversation scenarios for your level...",
                "Building vocabulary sets for \"${viewModel.goal}\"...",
                "Preparing personalized pronunciation exercises...",
                "Configuring AI tutors to match your learning style...",
                "Scheduling optimal practice sessions for your availability...",
                "Finalizing your personalized English program..."
            )

            // Display cycling messages to show AI is working
            var currentMessageIndex = 0
            subHeadline.text = aiMessages[0]

            val handler = Handler(Looper.getMainLooper())
            val messageRunnable = object : Runnable {
                override fun run() {
                    currentMessageIndex = (currentMessageIndex + 1) % aiMessages.size
                    subHeadline.text = aiMessages[currentMessageIndex]
                    handler.postDelayed(this, 2000)
                }
            }

            handler.postDelayed(messageRunnable, 2000)

            // Clean up handler when view is destroyed
            viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        handler.removeCallbacks(messageRunnable)
                    }
                }
            })
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
                        DetailScreenType.PainPointScreen -> {
                            viewModel.painPoint = ""
                            viewModel.englishLevel = ""
                            viewModel.currentScreen.postValue(DetailScreenType.EnglishLevelScreen)
                        }

                        DetailScreenType.GoalScreen -> {
                            viewModel.goal = ""
                            viewModel.currentScreen.postValue(DetailScreenType.PainPointScreen)
                        }

                        DetailScreenType.AvailabilityScreen -> {
                            viewModel.availability = ""
                            viewModel.currentScreen.postValue(DetailScreenType.GoalScreen)
                        }

                        DetailScreenType.PastExperienceScreen -> {
                            viewModel.pastExperience = ""
                            viewModel.currentScreen.postValue(DetailScreenType.AvailabilityScreen)
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

        val goalsList = arrayListOf(
            OnboardingScreenData("Daily Conversation", null),
            OnboardingScreenData("Business English", null),
            OnboardingScreenData("Academic Success", null),
            OnboardingScreenData("Travel Abroad", null),
            OnboardingScreenData("Pass Exams (IELTS/TOEFL)", null)
        )

        val availabilityList = arrayListOf(
            OnboardingScreenData("5-10 minutes daily", null),
            OnboardingScreenData("15-30 minutes daily", null),
            OnboardingScreenData("30-60 minutes daily", null),
            OnboardingScreenData("1+ hours daily", null),
            OnboardingScreenData("Weekends only", null)
        )

        val painPointList = arrayListOf(
            OnboardingScreenData("I freeze when speaking with natives", null),
            OnboardingScreenData("I can't understand natural conversations", null),
            OnboardingScreenData("I forget vocabulary when I need it", null),
            OnboardingScreenData("My pronunciation isn't clear", null),
            OnboardingScreenData("I make grammar mistakes", null)
        )

        val pastExperienceList = arrayListOf(
            OnboardingScreenData("Language apps with no personalization", null),
            OnboardingScreenData("Classes that are too crowded", null),
            OnboardingScreenData("Too much grammar, not enough speaking", null),
            OnboardingScreenData("Material that's boring or irrelevant", null),
            OnboardingScreenData("No consistent feedback", null)
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
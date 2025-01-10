package com.convoxing.convoxing_customer.ui.home.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import com.convoxing.convoxing_customer.data.remote.models.VocabularyWord
import com.convoxing.convoxing_customer.databinding.FragmentHomeBinding
import com.convoxing.convoxing_customer.databinding.FragmentLoginBinding
import com.convoxing.convoxing_customer.ui.home.adapter.CardStackAdapter
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeableMethod
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var appPrefManager: AppPrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calculateGreetingMessage(appPrefManager.user.name)

    }


    private fun calculateGreetingMessage(userName: String) {
        val currentTime = LocalTime.now()
        val greetingMessage = when {
            currentTime.hour < 12 -> "Good Morning, $userName!\nReady to learn? 🌞"
            currentTime.hour < 18 -> "Good Afternoon, $userName!\nLet's practice some English! 🌟"
            else -> "Good Evening, $userName!\nTime for a chat? 🌙"
        }
        binding.tvGreeting.text = greetingMessage

    }


//    private fun setupVocabCardStack() {
//        manager = CardStackLayoutManager(requireContext(), object : CardStackListener {
//
//            override fun onCardDragging(direction: Direction, ratio: Float) {
//                Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
//            }
//
//            override fun onCardSwiped(direction: Direction) {
//                Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
//            }
//
//            override fun onCardRewound() {
//                Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
//            }
//
//            override fun onCardCanceled() {
//                Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
//            }
//
//            override fun onCardAppeared(view: View, position: Int) {
//                Log.d("CardStackView", "onCardAppeared: ($position)")
//            }
//
//            override fun onCardDisappeared(view: View, position: Int) {
//                Log.d("CardStackView", "onCardDisappeared: ($position)")
//            }
//        })
//
//        val vocabList = listOf(
//            VocabularyWord("Intriguing", "Something fascinating or interesting"),
//            VocabularyWord("Comprehend", "To understand something"),
//            VocabularyWord("Perplexed", "Confused or puzzled")
//        )
//        manager.setStackFrom(StackFrom.None)
//        manager.setVisibleCount(3)
//        manager.setTranslationInterval(8.0f)
//        manager.setScaleInterval(0.95f)
//        manager.setSwipeThreshold(0.3f)
//        manager.setMaxDegree(20.0f)
//        manager.setDirections(Direction.HORIZONTAL)
//        manager.setCanScrollHorizontal(true)
//        manager.setCanScrollVertical(true)
//        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
//        manager.setOverlayInterpolator(LinearInterpolator())
//
//
//        adapter = CardStackAdapter(vocabList)
//        binding.cardStackView.layoutManager = manager
//        binding.cardStackView.adapter = adapter
//        binding.cardStackView.itemAnimator.apply {
//            if (this is DefaultItemAnimator) {
//                supportsChangeAnimations = false
//            }
//        }
//    }

}
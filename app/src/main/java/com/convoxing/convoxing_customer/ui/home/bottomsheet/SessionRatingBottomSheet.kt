package com.convoxing.convoxing_customer.ui.home.bottomsheet

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RatingBar
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.databinding.BottomSheetSessionRatingBinding
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.showToast
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class SessionRatingBottomSheet(
    private val sessionId: String,
    private val isCallExit: Boolean = false,
    private val isChatExit: Boolean = false,
    private val onRatingSubmitted: (sessionId: String, rating: Float, comment: String?, reasonsForLeaving: String?) -> Unit
) : BottomSheetDialogFragment() {

    // View binding instance
    private var _binding: BottomSheetSessionRatingBinding? = null
    private val binding get() = _binding!!
    private var sessionRated = false
    private val exitReasonCheckboxes = mutableListOf<CheckBox>()

    @Inject
    lateinit var analyticsHelperUtil: AnalyticsHelperUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        _binding = BottomSheetSessionRatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Customize UI based on whether this is for call exit feedback
        if (isCallExit || isChatExit) {
            binding.tvRatingTitle.text = "How was your experience?"
            binding.tvRatingSubtitle.text = "Your feedback helps us improve our AI assistant."
        }

        // Initialize checkboxes list for easier access
        with(binding) {
            exitReasonCheckboxes.apply {
                add(reasonTechnical)
                add(reasonUnderstanding)
                add(reasonSlowResponses)
                add(reasonIrrelevant)
                add(reasonNoTime)
                add(reasonOther)
            }
        }

        // Configure checkboxes based on session type
        configureCheckboxesForSessionType()


        // Set up rating change listener to show exit reason field for low ratings during call/chat exits
        binding.ratingBarSession.setOnRatingBarChangeListener { _, rating, _ ->
            if ((isCallExit || isChatExit) && rating <= 3f) {
                binding.exitReasonContainer.visibility = View.VISIBLE
                binding.exitReasonLabel.text = if (isCallExit)
                    "Why did you leave the call?"
                else
                    "Why did you leave the chat?"
            } else {
                binding.exitReasonContainer.visibility = View.GONE
            }
        }

        // Handle submit button click event
        binding.btnSubmitRating.setOnClickListener {
            val rating = binding.ratingBarSession.rating
            // Retrieve and trim any additional comments
            val comment = binding.etComments.text?.toString()?.trim()

            // If low rating and call/chat exit, get selected reasons
            val reasonsForLeaving = if ((isCallExit || isChatExit) && rating <= 3f) {
                collectExitReasons()
            } else {
                null
            }

            // Validate that a rating has been selected (greater than 0)
            if (rating > 0f) {
                val analyticsParams = mutableMapOf(
                    "rating" to rating.toString(),
                    "sessionId" to sessionId,
                    "session_type" to when {
                        isCallExit -> "call"
                        isChatExit -> "chat"
                        else -> "other"
                    }
                )

                if (!comment.isNullOrEmpty()) {
                    analyticsParams["comment"] = comment.toString()
                }

                if (!reasonsForLeaving.isNullOrEmpty()) {
                    analyticsParams["reasons_for_leaving"] = reasonsForLeaving
                }

                analyticsHelperUtil.logEvent("session_rating_submitted", analyticsParams)

                sessionRated = true
                onRatingSubmitted(
                    sessionId, rating,
                    if (comment.isNullOrEmpty()) null else comment,
                    reasonsForLeaving
                )
                dismiss()
            } else {
                showToast("Please provide a rating.")
            }
        }
    }

    /**
     * Configures the checkboxes with appropriate text based on session type
     */
    private fun configureCheckboxesForSessionType() {
        when {
            isCallExit -> {
                binding.apply {
                    reasonTechnical.text = "Technical issues (audio/connection problems)"
                    reasonUnderstanding.text = "Difficulty understanding the AI assistant"
                    reasonSlowResponses.text = "AI responses were too slow"
                    reasonIrrelevant.text = "Conversation was not relevant to my needs"
                    reasonNoTime.text = "I didn't have enough time to continue"
                    exitReasonLabel.text = "Why did you leave the call?"
                }
            }

            isChatExit -> {
                binding.apply {
                    reasonTechnical.text = "Technical issues (messaging not working properly)"
                    reasonUnderstanding.text = "AI didn't understand my questions"
                    reasonSlowResponses.text = "Responses were too slow"
                    reasonIrrelevant.text = "Responses weren't helpful for learning English"
                    reasonNoTime.text = "I needed to exit the app"
                    exitReasonLabel.text = "Why did you leave the chat?"
                }
            }
        }
    }

    /**
     * Collects all selected exit reasons and formats them as a JSON string
     */
    private fun collectExitReasons(): String {
        val reasonsList = JSONArray()

        // Add each checked reason to the list
        for (checkbox in exitReasonCheckboxes) {
            if (checkbox.isChecked) {
                val reason = JSONObject().apply {
                    put("reason", checkbox.text.toString())

                }
                reasonsList.put(reason)
            }
        }

        return reasonsList.toString()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!sessionRated) {
            analyticsHelperUtil.logEvent("session_rating_cancelled", mutableMapOf())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Avoid memory leaks by nullifying the binding reference
        _binding = null
    }
}
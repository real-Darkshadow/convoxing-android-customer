package com.convoxing.convoxing_customer.data.remote.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class ChatMessage(
    @SerializedName("_id")
    var id: String? = null,
    var sessionId: String? = null,
    var userMessageId: String? = null,
    val user: String? = null,
    val content: String? = null,
    val userMessageCount: Int? = null,
    val isSessionCompleted: Boolean? = null,
    var time: String? = null,
    val role: String? = null,
    var showAnalysisButton: Boolean = false, // Flag to show/hide the button
    val timestamp: String? = null,
    val vocabAnalysis: VocabAnalysis? = null,
    val isVocabAnalysisCompleted: Boolean? = null,
    val isGrammarAnalysisCompleted: Boolean? = null,
    val grammarAnalysis: ArrayList<GrammarAnalysis>? = null
)

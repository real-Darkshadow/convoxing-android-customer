package com.convoxing.convoxing_customer.ui.analysis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import com.convoxing.convoxing_customer.data.repository.chat.ChatRepositoryInterface
import com.convoxing.convoxing_customer.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(private val repository: ChatRepositoryInterface) :
    ViewModel() {
    private val _grammarAnalysisResponse = MutableLiveData<Resource<SuccessResponse>>()
    val grammarAnalysisResponse: LiveData<Resource<SuccessResponse>> get() = _grammarAnalysisResponse

    private val _vocabAnalysisResponse = MutableLiveData<Resource<SuccessResponse>>()
    val vocabAnalysisResponse: LiveData<Resource<SuccessResponse>> get() = _vocabAnalysisResponse

    private val _sessionSummaryResponse = MutableLiveData<Resource<SuccessResponse>>()
    val sessionSummaryResponse: LiveData<Resource<SuccessResponse>> get() = _sessionSummaryResponse

    private val _betterAnswerResponse = MutableLiveData<Resource<SuccessResponse>>()
    val betterAnswerResponse: LiveData<Resource<SuccessResponse>> get() = _betterAnswerResponse


    var sessionId = ""
    var currentMessageForAnalysis = ""
    var modelMessageForAnalysis = ""
    var userMessageId = ""

    fun getGrammarAnalysis() {
        _grammarAnalysisResponse.postValue(Resource.loading())
        viewModelScope.launch {
            _grammarAnalysisResponse.postValue(
                repository.getGrammarAnalysis(
                    currentMessageForAnalysis,
                    userMessageId,
                    sessionId
                )
            )
        }
    }

    fun getVocabAnalysis() {
        _vocabAnalysisResponse.postValue(Resource.loading())
        viewModelScope.launch {
            _vocabAnalysisResponse.postValue(
                repository.getVocabAnalysis(
                    currentMessageForAnalysis,
                    userMessageId,
                    sessionId
                )
            )
        }
    }

    fun getSessionSummary(sessionId: String) {
        _sessionSummaryResponse.postValue(Resource.loading())
        viewModelScope.launch {
            _sessionSummaryResponse.postValue(
                repository.getSessionSummary(
                    sessionId
                )
            )
        }
    }

    fun getBetterAnswer() {
        _betterAnswerResponse.postValue(Resource.loading())
        val prompt =
            "Question: \"${modelMessageForAnalysis}\"\n    Answer: \"${currentMessageForAnalysis}\"\n    Ideal Answer Length: 40 words\n            Feedback:"

        viewModelScope.launch {
            _betterAnswerResponse.postValue(
                repository.getBetterAnswer(
                    prompt,
                    userMessageId,
                    sessionId
                )
            )
        }
    }


}
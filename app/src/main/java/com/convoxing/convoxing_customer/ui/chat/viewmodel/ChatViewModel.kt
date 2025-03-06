package com.convoxing.convoxing_customer.ui.chat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convoxing.convoxing_customer.data.remote.models.ChatMessage
import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import com.convoxing.convoxing_customer.data.repository.chat.ChatRepositoryInterface
import com.convoxing.convoxing_customer.data.repository.home.MainRepositoryInterface
import com.convoxing.convoxing_customer.utils.Resource
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepositoryInterface,
    private val mainRepository: MainRepositoryInterface,
    private val analyticsHelperUtil: AnalyticsHelperUtil
) :
    ViewModel() {

    var isHistory: Boolean = false
    var isCasual: Boolean = false
    var isOverview: Boolean = false
    var isFreeSession: Boolean = false
    var topicId = ""
    var currentSessionId = ""
    var topicName = ""
    var category = ""
    var lastAiPrompt = ""
    var lastAiPromptForBetterAnswer = ""
    var isSubscriptionActive = false

    val subscriptionStatusResponse = MutableLiveData<Resource<SuccessResponse>>()


    private val _chatResponse = MutableLiveData<Resource<ChatMessage>>()
    val chatResponse: LiveData<Resource<ChatMessage>> get() = _chatResponse

    private val _hintResponse = MutableLiveData<Resource<SuccessResponse>>()
    val hintResponse: LiveData<Resource<SuccessResponse>> get() = _hintResponse

    private val _chatHistoryResponse = MutableLiveData<Resource<SuccessResponse>>()
    val chatHistoryResponse: LiveData<Resource<SuccessResponse>> get() = _chatHistoryResponse


    fun getSubscriptionStatus() {
        subscriptionStatusResponse.postValue(Resource.loading())
        viewModelScope.launch {
            val result = mainRepository.getSubscriptionStatus()
            subscriptionStatusResponse.postValue(result)
        }
    }


    fun initiateChat(message: String) {
        _chatResponse.postValue(Resource.loading())
        analyticsHelperUtil.logEvent(
            "initiate_chat",
            mutableMapOf(
                "isCasual" to isCasual,
                "topicId" to topicId,
                "category" to category,
                "topicName" to topicName
            )
        )
        viewModelScope.launch {
            _chatResponse.postValue(
                repository.initiateChat(
                    topicId = topicId,
                    topicName = topicName,
                    category = category,
                    sessionId = currentSessionId,
                    message = message,
                    isOverview = isOverview
                )
            )
        }
    }

    fun getPromptHint() {
        _hintResponse.postValue(Resource.loading())
        viewModelScope.launch {
            _hintResponse.postValue(repository.getPromptHint(lastAiPrompt))
        }

    }

    fun getChatHistory() {
        _chatHistoryResponse.postValue(Resource.loading())
        viewModelScope.launch {
            _chatHistoryResponse.postValue(repository.getSessionById(currentSessionId))
        }
    }

    fun markSessionClosed() {
        analyticsHelperUtil.logEvent(
            "close_chat",
            mutableMapOf(
                "isCasual" to isCasual,
                "topicId" to topicId,
                "category" to category,
                "topicName" to topicName
            )
        )
        viewModelScope.launch {
            repository.markSessionClosedById(currentSessionId)
        }
    }


}
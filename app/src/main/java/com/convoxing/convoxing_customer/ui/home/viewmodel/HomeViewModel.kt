package com.convoxing.convoxing_customer.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convoxing.convoxing_customer.data.models.DeepLinkValues
import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import com.convoxing.convoxing_customer.data.remote.models.User
import com.convoxing.convoxing_customer.data.repository.home.MainRepositoryInterface
import com.convoxing.convoxing_customer.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: MainRepositoryInterface) :
    ViewModel() {

    var deepLinkScreen = MutableLiveData<DeepLinkValues>()
    val navToPractice: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _userDetailResponse = MutableLiveData<Resource<SuccessResponse>>()
    val userDetailResponse: LiveData<Resource<SuccessResponse>> get() = _userDetailResponse

    private val _randomVocabResponse = MutableLiveData<Resource<SuccessResponse>>()
    val randomVocabResponse: LiveData<Resource<SuccessResponse>> get() = _randomVocabResponse

    val lastSessionRatingResponse = MutableLiveData<Resource<SuccessResponse>>()

    private val _sessionRatingResponse = MutableLiveData<Resource<SuccessResponse>>()
    val sessionRatingResponse: LiveData<Resource<SuccessResponse>> get() = _sessionRatingResponse


    val subscriptionStatusResponse = MutableLiveData<Resource<SuccessResponse>>()

    var isSubscriptionActive = false
    var isFreeSessionsLeft = false

    val user: User
        get() = repository.user

    fun logout() {
        repository.logout()
    }

    fun getSubscriptionStatus() {
        subscriptionStatusResponse.postValue(Resource.loading())
        viewModelScope.launch {
            val result = repository.getSubscriptionStatus()
            subscriptionStatusResponse.postValue(result)
        }
    }

    fun getUserById() {
        _userDetailResponse.postValue(Resource.loading())
        viewModelScope.launch {
            val result = repository.getUser()
            _userDetailResponse.postValue(result)
        }
    }

    fun setUserData(user: User) {
        repository.updateLearner(user)
    }

    fun getRandomVocab() {
        _randomVocabResponse.postValue(Resource.loading())
        viewModelScope.launch {
            val result = repository.getRandomVocab()
            _randomVocabResponse.postValue(result)
        }
    }

    fun getLastSessionRating() {
        lastSessionRatingResponse.postValue(Resource.loading())
        viewModelScope.launch {
            val result = repository.getLastSessionRating()
            lastSessionRatingResponse.postValue(result)
        }
    }

    fun submitSessionRating(sessionId: String, rating: Int, comment: String) {
        _sessionRatingResponse.postValue(Resource.loading())
        viewModelScope.launch {
            val result = repository.rateSession(sessionId, rating, comment)
            _sessionRatingResponse.postValue(result)
        }
    }

}
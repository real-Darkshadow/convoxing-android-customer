package com.convoxing.convoxing_customer.ui.progress.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import com.convoxing.convoxing_customer.data.remote.models.User
import com.convoxing.convoxing_customer.data.repository.home.MainRepositoryInterface
import com.convoxing.convoxing_customer.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(private val repository: MainRepositoryInterface) :
    ViewModel() {


    private val _userDetailResponse = MutableLiveData<Resource<SuccessResponse>>()
    val userDetailResponse: LiveData<Resource<SuccessResponse>> get() = _userDetailResponse

    private val _avgVocabWordsResponse = MutableLiveData<Resource<SuccessResponse>>()
    val avgVocabWordsResponse: LiveData<Resource<SuccessResponse>> get() = _avgVocabWordsResponse

    val user: User
        get() = repository.user

    fun getVocabularyAvgWords() {
        _avgVocabWordsResponse.postValue(Resource.loading())
        viewModelScope.launch {
            val result = repository.getVocabularyAvgWords()
            _avgVocabWordsResponse.postValue(result)
        }
    }

    fun getUserById() {
        _userDetailResponse.postValue(Resource.loading())
        viewModelScope.launch {
            val result = repository.getUser()
            _userDetailResponse.postValue(result)
        }
    }


    fun logoutUser() {
        repository.logout()
    }

}
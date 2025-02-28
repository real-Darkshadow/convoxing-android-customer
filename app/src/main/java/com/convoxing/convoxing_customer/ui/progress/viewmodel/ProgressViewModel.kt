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

    val profileOptionList = mapOf(
        0 to Triple("Share App", "Share our app with others and earn rewards.", "share"),
        1 to Triple(
            "Privacy Policy",
            "Read our privacy policy to understand how we handle your data.",
            "privacy"
        ),
        2 to Triple("Terms of Use", "Read and understand our terms and conditions.", "terms"),
        3 to Triple("Contact Us", "Please contact if you face any issues.", "contact"),
        4 to Triple("App Settings", "Customize your app experience.", "settings"),
        5 to Triple("Help & FAQ", "Get answers to frequently asked questions.", "help"),
        6 to Triple(
            "Delete Account",
            "Permanently delete your account and all associated data.",
            "delete"
        ),
        7 to Triple("Logout", "Sign out from your account.", "logout"),
    )


    fun logoutUser() {
        repository.logout()
    }

}
package com.convoxing.convoxing_customer.ui.practice.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import com.convoxing.convoxing_customer.data.repository.home.MainRepositoryInterface
import com.convoxing.convoxing_customer.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val mainRepository: MainRepositoryInterface
) : ViewModel() {

    var situationsResult = MutableLiveData<Resource<SuccessResponse>>(Resource.idle())
    var rolePlayResult = MutableLiveData<Resource<SuccessResponse>>(Resource.idle())
    var sessionHistoryResult = MutableLiveData<Resource<SuccessResponse>>(Resource.idle())


    fun getSituations() {
        viewModelScope.launch {
            situationsResult.value = Resource.loading()
            situationsResult.value = mainRepository.getThemes()
        }
    }

    fun getRolePlays() {
        viewModelScope.launch {
            rolePlayResult.value = Resource.loading()
            rolePlayResult.value = mainRepository.getRolePlays()
        }
    }

    fun getSessionHistory() {
        viewModelScope.launch {
            sessionHistoryResult.value = Resource.loading()
            sessionHistoryResult.value = mainRepository.getSessionHistory()
        }

    }


}
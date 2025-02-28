package com.convoxing.convoxing_customer.ui.call.viewmodel

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
class CallViewModel @Inject constructor(
    private val mainRepository: MainRepositoryInterface,
) : ViewModel() {

    private val _checkUserCallCoinsResponse = MutableLiveData<Resource<SuccessResponse>>()
    val checkUserCallCoinsResponse: MutableLiveData<Resource<SuccessResponse>>
        get() = _checkUserCallCoinsResponse


    var coinsBalance = 0


    fun checkUserCallCoins() {
        _checkUserCallCoinsResponse.postValue(Resource.loading())
        viewModelScope.launch {
            _checkUserCallCoinsResponse.value = mainRepository.checkUserCallCoins()
        }
    }


}

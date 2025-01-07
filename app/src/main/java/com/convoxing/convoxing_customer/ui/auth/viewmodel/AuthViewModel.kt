package com.convoxing.convoxing_customer.ui.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
//    private val authRepository: AuthRepositoryInterface,
) : ViewModel() {


    val currentScreen = MutableLiveData<DetailScreenType>(DetailScreenType.NameScreen)

    var profession = ""
    var name = ""
    var goal = ""


    sealed class DetailScreenType {
        data object NameScreen : DetailScreenType()
        data object GoalScreen : DetailScreenType()
        data object ProfessionScreen : DetailScreenType()
    }
}
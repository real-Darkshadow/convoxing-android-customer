package com.convoxing.convoxing_customer.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import com.convoxing.convoxing_customer.data.remote.models.User
import com.convoxing.convoxing_customer.data.repository.auth.AuthRepositoryInterface
import com.convoxing.convoxing_customer.utils.Resource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.revenuecat.purchases.Purchases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepositoryInterface,
) : ViewModel() {

    var socialLoginResult = MutableLiveData<Resource<SuccessResponse>>(Resource.idle())
    var updateUserDataResult = MutableLiveData<Resource<SuccessResponse>>(Resource.idle())
    val currentScreen = MutableLiveData<DetailScreenType>(DetailScreenType.NameScreen)
    val fcmToken = MutableLiveData<String>()

    var englishLevel = ""
    var name = ""
    var age = ""
    var goal = ""
    var availability = ""
    var painPoint = ""  // New field
    var pastExperience = ""  // New field


    fun socialLogin(
        email: String = "",
        authType: String,
        name: String,
        token: String?,
    ) {
        socialLoginResult.value = Resource.loading()

        viewModelScope.launch {
            socialLoginResult.value = authRepository.socialLogin(
                email = email,
                name = name,
                token = token,
                authType = authType,
                firebaseToken = fcmToken.value.toString()
            )

        }
    }

    fun setUserPref(user: User) {
        viewModelScope.launch {
            authRepository.setUserData(user)
        }
    }

    fun updateUserDetails() {
        viewModelScope.launch {
            updateUserDataResult.postValue(Resource.loading())
            updateUserDataResult.value = authRepository.addUserDetails(
                name,
                age,
                englishLevel,
                goal,
                availability,
                painPoint,
                pastExperience
            )
        }
    }


    fun initFcm() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("tag :", "getInstanceId failed", task.exception)
                    return@OnCompleteListener

                }
                val tokenLocal = task.result
                fcmToken.value = tokenLocal.toString()
                Purchases.sharedInstance.setPushToken(tokenLocal.toString())
            })
    }


    sealed class DetailScreenType {
        data object NameScreen : DetailScreenType()
        data object AgeScreen : DetailScreenType()
        data object EnglishLevelScreen : DetailScreenType()
        data object PainPointScreen : DetailScreenType()  // New screen type
        data object GoalScreen : DetailScreenType()
        data object AvailabilityScreen : DetailScreenType()
        data object PastExperienceScreen : DetailScreenType()  // New screen type
        data object CraftingExperienceScreen : DetailScreenType()
    }
}
package com.convoxing.convoxing_customer.data.repository.auth

import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import com.convoxing.convoxing_customer.data.remote.models.User
import com.convoxing.convoxing_customer.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.util.TimeZone

interface AuthRepositoryInterface {
    fun setUserData(user: User)


    suspend fun socialLogin(
        email: String,
        name: String,
        token: String?,
        authType: String,
        firebaseToken: String,
    ): Resource<SuccessResponse>


    suspend fun addUserDetails(
        name: String,
        age: String,
        englishLevel: String
    ): Resource<SuccessResponse>


}
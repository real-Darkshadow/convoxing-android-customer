package com.convoxing.convoxing_customer.data.repository.auth

import com.convoxing.convoxing_customer.ConvoxingApp

import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import com.convoxing.convoxing_customer.data.remote.api.ApiService
import com.convoxing.convoxing_customer.data.remote.models.User
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.isHttpSuccessCode
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.isNotNullOrBlank
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.logError
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.parseErrorMessage
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.toJson
import com.convoxing.convoxing_customer.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject


class AuthRepository @Inject constructor(
    val context: ConvoxingApp,
    private val apiService: ApiService,
    val appPrefManager: AppPrefManager,
) : AuthRepositoryInterface {


    override suspend fun addUserDetails(
        name: String,
        age: String,
        englishLevel: String,
    ): Resource<SuccessResponse> = withContext(Dispatchers.IO) {
        val headerMap = HashMap<String, String>()
        try {
            headerMap["x-access-token"] = appPrefManager.user.mToken.toString()
            val bodyMap = HashMap<String, Any>()
            if (name.isNotNullOrBlank()) bodyMap["name"] = name.trim()
            if (age.isNotNullOrBlank()) bodyMap["age"] = age
            if (englishLevel.isNotNullOrBlank()) bodyMap["englishLevel"] = englishLevel

            val result = apiService.setupUserProfile(headerMap, bodyMap)
            if (result.statusCode.isHttpSuccessCode()) {
                Resource.success(result)
            } else {
                logError {
                    exception = Exception(result.message)
                    message = "${headerMap.toJson()}"
                    function = "addUserDetails"
                }
                Resource.error(result.message)
            }
        } catch (e: Exception) {
            Resource.error(e.parseErrorMessage())
        }


    }

    override fun setUserData(user: User) {
        appPrefManager.user = user
    }

//
//    override fun logout() {
//        appPrefManager.logoutUser()
//    }


    override suspend fun socialLogin(
        email: String,
        name: String,
        token: String?,
        authType: String,
        firebaseToken: String,
    ): Resource<SuccessResponse> {
        val jsonObject = JSONObject()
        if (email.isNotBlank()) jsonObject.putOpt("email", email)
        jsonObject.put("name", name)
        jsonObject.putOpt("authType", authType)
        jsonObject.putOpt("notificationid", firebaseToken)
        jsonObject.putOpt("token", token)


        val requestBody =
            jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return withContext(Dispatchers.IO) {
            try {
                val result = apiService.socialAuthentication(requestBody)
                if (result.statusCode.isHttpSuccessCode()) {
                    Resource.success(result)
                } else {

                    Resource.error(result.message)
                }
            } catch (e: Exception) {
                logError {
                    message = "${requestBody.toJson()}"
                    exception = e
                    function = "socialLogin"
                }
                Resource.error(e.message)
            }
        }

    }


}
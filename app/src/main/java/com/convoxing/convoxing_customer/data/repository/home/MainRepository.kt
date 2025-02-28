package com.convoxing.convoxing_customer.data.repository.home

import com.convoxing.convoxing_customer.ConvoxingApp
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import com.convoxing.convoxing_customer.data.remote.api.ApiService
import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import com.convoxing.convoxing_customer.data.remote.models.User
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.isHttpSuccessCode
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.parseErrorMessage
import com.convoxing.convoxing_customer.utils.Resource
import com.datadog.android.log.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.TimeZone
import javax.inject.Named

class MainRepository(
    val context: ConvoxingApp,
    val apiService: ApiService,
    val appPrefManager: AppPrefManager,
    @Named("device_id") private val deviceID: String,
//    private val logger: Logger,
) : MainRepositoryInterface {
    override suspend fun getUser(): Resource<SuccessResponse> {
        val requestBody = HashMap<String, String>()
        requestBody["userID"] = appPrefManager.user.mId
        requestBody["timezone"] = TimeZone.getDefault().id
        requestBody["deviceId"] = deviceID

        return withContext(Dispatchers.IO) {
            try {
                val result = apiService.findUserById(
                    appPrefManager.user.mToken.toString(),
                    requestBody
                )
                if (result.code().isHttpSuccessCode())
                    Resource.success(result.body())
                else
                    Resource.error(result.body().toString())
            } catch (e: Exception) {
                Resource.error(e.parseErrorMessage())
            }
        }
    }


    override fun logout() {
        appPrefManager.logoutUser()
    }


//
//    override suspend fun getSubscriptions(
//        subject: String,
//        peerSubscription: Boolean,
//    ): SuccessResponse {
//        val body = HashMap<String, Any>()
//        val id = appPrefManager.user.mId.toString()
//        val token = appPrefManager.user.mToken.toString()
//        return withContext(Dispatchers.IO) {
//            val headerMap = HashMap<String, String>()
//            headerMap["x-access-token"] = token
//
//            body["learnerId"] = id
//            body["subject"] = subject
//            if (peerSubscription)
//                body["peerSubscription"] = true
//
//            val result = apiService.getSubscriptions(headerMap, body)
//            result
//        }
//
//    }
//
//

    //
    override fun updateLearner(user: User) {
        val token = appPrefManager.user.mToken.toString()
        user.mToken = token
        appPrefManager.user = user
    }
//


    override val user: User
        get() {
            return appPrefManager.user
        }


//
//    override suspend fun getLearnerPeerStreak(): Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.getLearnerPeerStreak(
//                    appPrefManager.user.mToken.toString(),
//                    appPrefManager.user.mId.toString()
//                )
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.body().toString())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun updateNotificationId(token: String) {
//        return withContext(Dispatchers.IO) {
//
//
//            try {
//                val body = HashMap<String, String>().apply {
//                    put("notificationid", token)
//                    put("learnerId", appPrefManager.user.mId)
//                }
//                val result = apiService.updateNotificationId(
//                    appPrefManager.user.mToken.toString(), body
//                )
//
//            } catch (e: Exception) {
//                logError {
//                    message = "${e.message}"
//                    exception = e
//                    function = "updateNotificationId"
//                }
//            }
//
//        }
//    }


    override suspend fun getRolePlays(): Resource<SuccessResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val result = apiService.getSituations(
                    appPrefManager.user.mToken.toString()
                )
                if (result.code().isHttpSuccessCode())
                    Resource.success(result.body())
                else
                    Resource.error(result.body().toString())
            } catch (e: Exception) {
                Resource.error(e.parseErrorMessage())
            }
        }
    }

    override suspend fun getThemes(): Resource<SuccessResponse> {
        return withContext(Dispatchers.IO) {
            try {

                val result = apiService.getThemes(
                    appPrefManager.user.mToken.toString()
                )
                if (result.code().isHttpSuccessCode())
                    Resource.success(result.body())
                else
                    Resource.error(result.body().toString())
            } catch (e: Exception) {
                Resource.error(e.parseErrorMessage())
            }
        }
    }

    override suspend fun getRandomVocab(): Resource<SuccessResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val result = apiService.getRandomVocab(
                    appPrefManager.user.mToken.toString()
                )
                if (result.code().isHttpSuccessCode())
                    Resource.success(result.body())
                else
                    Resource.error(result.body().toString())
            } catch (e: Exception) {
                Resource.error(e.parseErrorMessage())
            }
        }
    }

    override suspend fun getVocabularyAvgWords(): Resource<SuccessResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val result = apiService.getVocabularyAvgWords(
                    appPrefManager.user.mToken.toString()
                )
                if (result.code().isHttpSuccessCode())
                    Resource.success(result.body())
                else
                    Resource.error(result.body().toString())
            } catch (e: Exception) {
                Resource.error(e.parseErrorMessage())
            }
        }
    }

    override suspend fun getSessionHistory(): Resource<SuccessResponse> {
        val requestBody = HashMap<String, String>()
        requestBody["userId"] = appPrefManager.user.mId
        return withContext(Dispatchers.IO) {
            try {
                val result = apiService.getSessionHistory(
                    appPrefManager.user.mToken.toString(),
                    requestBody
                )
                if (result.code().isHttpSuccessCode())
                    Resource.success(result.body())
                else
                    Resource.error(result.body().toString())
            } catch (e: Exception) {
                Resource.error(e.parseErrorMessage())
            }
        }
    }

    override suspend fun getLastSessionRating(): Resource<SuccessResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val result = apiService.getLastSessionRating(
                    appPrefManager.user.mToken.toString()
                )
                if (result.code().isHttpSuccessCode())
                    Resource.success(result.body())
                else
                    Resource.error(result.body().toString())
            } catch (e: Exception) {
                Resource.error(e.parseErrorMessage())
            }
        }
    }

    override suspend fun checkUserCallCoins(): Resource<SuccessResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val result = apiService.checkUserCallCoins(
                    appPrefManager.user.mToken.toString()
                )
                if (result.code().isHttpSuccessCode())
                    Resource.success(result.body())
                else
                    Resource.error(result.body().toString())
            } catch (e: Exception) {
                Resource.error(e.parseErrorMessage())
            }
        }
    }

    override suspend fun getSubscriptionStatus(): Resource<SuccessResponse> {
        val requestBody = HashMap<String, String>()
        requestBody["customerId"] = appPrefManager.user.mId
        return withContext(Dispatchers.IO) {
            try {
                val result = apiService.getSubscriptionStatus(
                    appPrefManager.user.mToken.toString(),
                    requestBody
                )
                if (result.code().isHttpSuccessCode())
                    Resource.success(result.body())
                else
                    Resource.error(result.body().toString())
            } catch (e: Exception) {
                Resource.error(e.parseErrorMessage())
            }
        }
    }

    override suspend fun rateSession(
        sessionId: String,
        rating: Int,
        comment: String
    ): Resource<SuccessResponse> {
        val requestBody = HashMap<String, Any>()
        requestBody["sessionId"] = sessionId
        requestBody["rating"] = rating
        requestBody["comment"] = comment

        return withContext(Dispatchers.IO) {
            try {
                val result = apiService.rateSession(
                    appPrefManager.user.mToken.toString(),
                    requestBody
                )
                if (result.code().isHttpSuccessCode())
                    Resource.success(result.body())
                else
                    Resource.error(result.body().toString())
            } catch (e: Exception) {
                Resource.error(e.parseErrorMessage())
            }
        }
    }
}

package com.convoxing.convoxing_customer.data.repository.home

import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import com.convoxing.convoxing_customer.data.remote.models.User
import com.convoxing.convoxing_customer.utils.Resource

interface MainRepositoryInterface {
    suspend fun getUser(): Resource<SuccessResponse>
    val user: User
//
//    suspend fun getSubscriptions(
//        subject: String,
//        peerSubscription: Boolean = false,
//    ): SuccessResponse

    fun updateLearner(user: User)
    fun logout()

//    suspend fun getLearnerPeerStreak(): Resource<SuccessResponse>

//    suspend fun updateNotificationId(token: String)


    suspend fun getRolePlays(): Resource<SuccessResponse>
    suspend fun getThemes(): Resource<SuccessResponse>
    suspend fun getRandomVocab(): Resource<SuccessResponse>
    suspend fun getSessionHistory(): Resource<SuccessResponse>
    suspend fun getLastSessionRating(): Resource<SuccessResponse>
    suspend fun getVocabularyAvgWords(): Resource<SuccessResponse>
    suspend fun getSubscriptionStatus(): Resource<SuccessResponse>
    suspend fun checkUserCallCoins(): Resource<SuccessResponse>
    suspend fun rateSession(
        sessionId: String,
        rating: Int,
        comment: String
    ): Resource<SuccessResponse>


}
package com.convoxing.convoxing_customer.data.repository.home

import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import com.convoxing.convoxing_customer.utils.Resource

interface MainRepositoryInterface {
//    suspend fun getLearner(): SuccessResponse
//    val user: Learner
//    fun logout()
//    suspend fun getReferCode(): Resource<ReferCode>
//
////    suspend fun getSubscriptions(
////        subject: String,
////        peerSubscription: Boolean = false,
////    ): SuccessResponse
//
//
//    suspend fun getImages(): Resource<ImageResponse>
//    fun setToken(token: String)
//    fun updateLearner(learner: Learner)
//
//
//    fun setUserReviewedApp(): Unit
//    fun hasUserReviewedApp(): Boolean
//
//
//    suspend fun getReferHistory(page: Int, limit: Int): Resource<ReferHistoryResponse>
//    suspend fun getLearnerPeerStreak(): Resource<SuccessResponse>
//    suspend fun deleteAccount(): Resource<SuccessResponse>
//
//
//    suspend fun setPeerModeratorRetry(): Resource<SuccessResponse>
//
//    suspend fun addLearnerBackground(
//        name: String,
//        phone: String,
//        email: String,
//        dob: String,
//        postalCode: String,
//        profession: String,
//        englishLevel: String,
//        bio: String,
//        joiningMotivation: ArrayList<String>,
//        interests: ArrayList<String>,
//        languages: ArrayList<String>,
//    ): Resource<SuccessResponse>
//
//
//
//    suspend fun updateNotificationId(token: String)
//    suspend fun restoreAccount(): Resource<SuccessResponse>

    suspend fun getRolePlays(): Resource<SuccessResponse>
    suspend fun getThemes(): Resource<SuccessResponse>


}
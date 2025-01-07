package com.convoxing.convoxing_customer.data.remote.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query


interface ApiService {

    @POST("api/user/getUserByID")
    suspend fun findUserById(
        @HeaderMap headerMap: HashMap<String, String>,
        @Body body: HashMap<String, String>,
    ): SuccessResponse

    @GET("api/images")
    suspend fun getImages(@Header("x-access-token") token: String): Response<com.clapingo.speakana.data.models.ImageResponse>

    @POST("api/user/v1/signupUser")
    suspend fun signupUser(@Body signUpData: HashMap<String, Any>): SuccessResponse

    @PUT("api/User/v1/setupUser")
    suspend fun setupUserProfile(
        @HeaderMap headerMap: HashMap<String, String>,
        @Body body: HashMap<String, Any>,
    ): SuccessResponse

    @POST("api/User/getFilteredPlan")
    suspend fun getFilteredPlan(
        @HeaderMap headerMap: HashMap<String, String>,
        @Body body: HashMap<String, String>,
    ): SuccessResponse

    @POST("api/User/getSubscriptions")
    suspend fun getSubscriptions(
        @HeaderMap headerMap: HashMap<String, String>,
        @Body body: HashMap<String, Any>,
    ): SuccessResponse

    @POST("api/feedback/feedback")
    suspend fun postFeedback(
        @HeaderMap headerMap: HashMap<String, String>,
        @Body body: HashMap<String, String>,
    ): ResponseBody

    @POST("api/User/socialAuth")
    suspend fun socialAuthentication(@Body body: RequestBody): SuccessResponse

    @PUT("api/User/updateNotificationId")
    fun updateNotificationId(
        @Header("x-access-token") token: String,
        @Body body: HashMap<String, String>,
    ): Response<SuccessResponse>

    @GET("api/User/getDailyStreak")
    suspend fun getUserDailyStreak(
        @Header("x-access-token") token: String,
        @Query("userId") sessionId: String,
    ): Response<SuccessResponse>

    @POST("/api/User/deleteUserAccount")
    suspend fun deleteAccount(
        @Header("x-access-token") token: String,
        @Body body: HashMap<String, String>,
    ): Response<SuccessResponse>

    @PUT("/api/User/cancelDeleteAccount")
    suspend fun restoreAccount(
        @Header("x-access-token") token: String,
        @Body body: HashMap<String, String>,
    ): Response<SuccessResponse>
}

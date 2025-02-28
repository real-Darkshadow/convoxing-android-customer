package com.convoxing.convoxing_customer.data.remote.api

import com.convoxing.convoxing_customer.data.remote.models.ChatMessage
import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Streaming


interface ApiService {

    @POST("api/user/getUserByID")
    suspend fun findUserById(
        @Header("x-access-token") token: String,
        @Body body: HashMap<String, String>
    ): Response<SuccessResponse>

    @PUT("api/user/setupUser")
    suspend fun setupUserProfile(
        @HeaderMap headerMap: HashMap<String, String>,
        @Body body: HashMap<String, Any>,
    ): SuccessResponse

    @POST("api/user/socialAuth")
    suspend fun socialAuthentication(@Body body: RequestBody): SuccessResponse

    @GET("/api/conversation/themes")
    suspend fun getThemes(@Header("x-access-token") token: String): Response<SuccessResponse>

    @GET("/api/conversation/situations")
    suspend fun getSituations(@Header("x-access-token") token: String): Response<SuccessResponse>

    @POST("/api/subscription/checkSubscriptionStatus")
    suspend fun getSubscriptionStatus(
        @Header("x-access-token") token: String, @Body body: HashMap<String, String>,
    ): Response<SuccessResponse>

    @GET("/api/subscription/checkUserCallCoins")
    suspend fun checkUserCallCoins(@Header("x-access-token") token: String): Response<SuccessResponse>


    @GET("/api/vocab/getRandomVocab")
    suspend fun getRandomVocab(@Header("x-access-token") token: String): Response<SuccessResponse>

    @GET("/api/session/last-rating")
    suspend fun getLastSessionRating(@Header("x-access-token") token: String): Response<SuccessResponse>

    @Streaming
    @POST("api/chat/stream")
    suspend fun initiateChatStream(
        @Header("x-access-token") token: String,
        @Body body: HashMap<Any, Any>
    ): Response<ResponseBody>

    @POST("api/chat/stream")
    suspend fun initiateChat(
        @Header("x-access-token") token: String,
        @Body body: HashMap<Any, Any>
    ): Response<ChatMessage>

    @POST("api/analysis/grammar")
    suspend fun getGrammarAnalysis(
        @Header("x-access-token") token: String,
        @Body body: HashMap<Any, Any>
    ): Response<SuccessResponse>

    @POST("api/analysis/sessionSummary")
    suspend fun getSessionSummary(
        @Header("x-access-token") token: String,
        @Body body: HashMap<String, String>
    ): Response<SuccessResponse>

    @POST("api/analysis/vocab")
    suspend fun getVocabAnalysis(
        @Header("x-access-token") token: String,
        @Body body: HashMap<Any, Any>
    ): Response<SuccessResponse>

    @POST("api/chat/chatHint")
    suspend fun getPromptHint(
        @Header("x-access-token") token: String,
        @Body body: HashMap<Any, Any>
    ): Response<SuccessResponse>

    @POST("api/chat/betterAnswer")
    suspend fun getBetterAnswer(
        @Header("x-access-token") token: String,
        @Body body: HashMap<Any, Any>
    ): Response<SuccessResponse>

    @POST("api/session/history")
    suspend fun getSessionHistory(
        @Header("x-access-token") token: String,
        @Body body: HashMap<String, String>
    ): Response<SuccessResponse>

    @POST("api/session/sessionById")
    suspend fun getSessionById(
        @Header("x-access-token") token: String,
        @Body body: HashMap<String, String>
    ): Response<SuccessResponse>

    @POST("api/session/markSessionClosedById")
    suspend fun markSessionClosedById(
        @Header("x-access-token") token: String,
        @Body body: HashMap<String, String>
    ): Response<SuccessResponse>

    @POST("api/session/rate")
    suspend fun rateSession(
        @Header("x-access-token") token: String,
        @Body body: HashMap<String, Any>
    ): Response<SuccessResponse>

    @GET("api/session/vocabularyAvgWords")
    suspend fun getVocabularyAvgWords(@Header("x-access-token") token: String): Response<SuccessResponse>


    @POST("api/User/getSubscriptions")
    suspend fun getSubscriptions(
        @HeaderMap headerMap: HashMap<String, String>,
        @Body body: HashMap<String, Any>,
    ): SuccessResponse


    @PUT("/api/User/updateNotificationId")
    fun updateNotificationId(
        @Header("x-access-token") token: String,
        @Body body: HashMap<String, String>,
    ): Response<SuccessResponse>

}

package com.convoxing.convoxing_customer.data.repository.chat

import com.convoxing.convoxing_customer.data.remote.models.ChatMessage
import com.convoxing.convoxing_customer.data.remote.models.Stream
import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import com.convoxing.convoxing_customer.utils.Resource
import kotlinx.coroutines.flow.Flow


interface ChatRepositoryInterface {

    suspend fun initiateChatStream(
        topicId: String,
        topicName: String,
        isPractice: Boolean
    ): Flow<Resource<Stream>>

    suspend fun getGrammarAnalysis(
        chatMessage: String,
        userMessageId: String,
        sessionId: String
    ): Resource<SuccessResponse>

    suspend fun getVocabAnalysis(
        chatMessage: String,
        userMessageId: String,
        sessionId: String
    ): Resource<SuccessResponse>

    suspend fun getSessionSummary(
        sessionId: String
    ): Resource<SuccessResponse>

    suspend fun getPromptHint(
        prompt: String,
    ): Resource<SuccessResponse>

    suspend fun getBetterAnswer(
        prompt: String,
        userMessageId: String,
        sessionId: String
    ): Resource<SuccessResponse>


    suspend fun initiateChat(
        topicId: String,
        topicName: String,
        category: String,
        sessionId: String,
        message: String,
        isOverview: Boolean
    ): Resource<ChatMessage>

    suspend fun getSessionById(
        sessionId: String
    ): Resource<SuccessResponse>

    suspend fun markSessionClosedById(
        sessionId: String
    ): Resource<SuccessResponse>
}
package com.convoxing.convoxing_customer.data.repository.chat

import com.convoxing.convoxing_customer.ConvoxingApp
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import com.convoxing.convoxing_customer.data.remote.api.ApiService
import com.convoxing.convoxing_customer.data.remote.models.ChatMessage
import com.convoxing.convoxing_customer.data.remote.models.Stream
import com.convoxing.convoxing_customer.data.remote.models.SuccessResponse
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.isNotNullOrBlank
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.logError
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.toJson
import com.convoxing.convoxing_customer.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.IOException


class ChatRepository(
    val context: ConvoxingApp,
    val appPrefManager: AppPrefManager,
    private val apiService: ApiService,
) : ChatRepositoryInterface {


    override suspend fun initiateChatStream(
        topicId: String, topicName: String, isPractice: Boolean
    ): Flow<Resource<Stream>> {
        val body = HashMap<Any, Any>()

        if (topicName.isNotNullOrBlank()) body["topicName"] = topicName
        if (topicId.isNotNullOrBlank()) body["topicId"] = topicId
        body["isPractice"] = isPractice

        val gson = Gson()
        return flow {
            try {
                // Make the API call to initiate chat and get the Response<ResponseBody>
                val response =
                    apiService.initiateChatStream(appPrefManager.user.mToken.toString(), body)

                if (response.isSuccessful) {
                    // If the response is successful, get the stream data
                    val responseBody = response.body()?.byteStream()?.bufferedReader()
                        ?: throw Exception("Failed to retrieve stream")

                    try {
                        while (currentCoroutineContext().isActive) {
                            val line = responseBody.readLine()
                            if (line != null && line.startsWith("data:")) {
                                try {
                                    // Deserialize to Stream type
                                    val answerDetailInfo =
                                        gson.fromJson(line.substring(5).trim(), Stream::class.java)
                                    emit(Resource.success(answerDetailInfo))  // Emit Resource<Stream>
                                } catch (e: Exception) {
                                    e.printStackTrace() // Handle malformed JSON or other exceptions
                                }
                            }
                            delay(100) // Adjust delay if necessary, depending on stream data frequency
                        }
                    } catch (e: IOException) {
                        emit(Resource.error("Stream reading error: ${e.message}"))
                    } finally {
                        responseBody.close() // Ensure the stream is closed after use
                    }

                } else {
                    // If the response was not successful, emit an error state
                    emit(Resource.error<Stream>(response.errorBody()?.string() ?: "Error occurred"))
                }
            } catch (e: Exception) {
                // Catch any exception and emit an error state
                emit(Resource.error<Stream>("Error occurred: ${e.message ?: "Unknown error"}"))
            }
        }.flowOn(Dispatchers.IO) // Use IO dispatcher for network calls
    }

    override suspend fun initiateChat(
        topicId: String,
        topicName: String,
        category: String,
        sessionId: String,
        message: String,
        isOverview: Boolean
    ): Resource<ChatMessage> {
        val body = HashMap<Any, Any>()

        if (topicName.isNotNullOrBlank()) body["topicName"] = topicName
        if (topicId.isNotNullOrBlank()) body["topicId"] = topicId
        if (sessionId.isNotNullOrBlank()) body["sessionId"] = sessionId
        body["category"] = category
        body["isOverview"] = isOverview
        body["userId"] = appPrefManager.user.mId
        body["message"] = message
        body["totalWords"] = message.trim().split("\\s+".toRegex()).size

        return withContext(Dispatchers.IO) {
            try {
                val response =
                    apiService.initiateChat(appPrefManager.user.mToken.toString(), body)
                if (response.isSuccessful) {
                    Resource.success(response.body())
                } else
                    Resource.error(response.message())
            } catch (e: Exception) {
                logError {
                    this.message = "${body.toJson()}"
                    exception = e
                    function = "initiateChat"
                }
                Resource.error(e.message)
            }
        }
    }

    override suspend fun getGrammarAnalysis(
        chatMessage: String,
        userMessageId: String,
        sessionId: String
    ): Resource<SuccessResponse> {
        val body = HashMap<Any, Any>()
        body["message"] = chatMessage
        body["userMessageId"] = userMessageId
        body["sessionId"] = sessionId
        body["userId"] = appPrefManager.user.mId
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    apiService.getGrammarAnalysis(appPrefManager.user.mToken.toString(), body)
                if (response.isSuccessful) {
                    Resource.success(response.body())
                } else
                    Resource.error(response.message())
            } catch (e: Exception) {
                logError {
                    message = "${body.toJson()}"
                    exception = e
                    function = "getGrammarAnalysis"
                }
                Resource.error(e.message)
            }

        }
    }

    override suspend fun getVocabAnalysis(
        chatMessage: String,
        userMessageId: String,
        sessionId: String
    ): Resource<SuccessResponse> {
        val body = HashMap<Any, Any>()
        body["message"] = chatMessage
        body["userMessageId"] = userMessageId
        body["sessionId"] = sessionId
        body["userId"] = appPrefManager.user.mId
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    apiService.getVocabAnalysis(appPrefManager.user.mToken.toString(), body)
                if (response.isSuccessful) {
                    Resource.success(response.body())
                } else
                    Resource.error(response.message())
            } catch (e: Exception) {
                logError {
                    message = "${body.toJson()}"
                    exception = e
                    function = "getVocabAnalysis"
                }
                Resource.error(e.message)
            }

        }
    }

    override suspend fun getPromptHint(prompt: String): Resource<SuccessResponse> {
        val body = HashMap<Any, Any>()
        body["prompt"] = prompt
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    apiService.getPromptHint(appPrefManager.user.mToken.toString(), body)
                if (response.isSuccessful) {
                    Resource.success(response.body())
                } else
                    Resource.error(response.message())
            } catch (e: Exception) {
                logError {
                    message = "${body.toJson()}"
                    exception = e
                    function = "getPromptHint"
                }
                Resource.error(e.message)
            }

        }
    }

    override suspend fun getBetterAnswer(
        prompt: String,
        userMessageId: String,
        sessionId: String
    ): Resource<SuccessResponse> {
        val body = HashMap<Any, Any>()
        body["prompt"] = prompt
        body["userMessageId"] = userMessageId
        body["sessionId"] = sessionId
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    apiService.getBetterAnswer(appPrefManager.user.mToken.toString(), body)
                if (response.isSuccessful) {
                    Resource.success(response.body())
                } else
                    Resource.error(response.message())
            } catch (e: Exception) {
                logError {
                    message = "${body.toJson()}"
                    exception = e
                    function = "getBetterAnswer"
                }
                Resource.error(e.message)
            }

        }
    }

    override suspend fun getSessionById(sessionId: String): Resource<SuccessResponse> {
        val body = HashMap<String, String>()
        body["sessionId"] = sessionId
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    apiService.getSessionById(appPrefManager.user.mToken.toString(), body)
                if (response.isSuccessful) {
                    Resource.success(response.body())
                } else
                    Resource.error(response.message())
            } catch (e: Exception) {
                logError {
                    message = "${body.toJson()}"
                    exception = e
                    function = "getSessionById"
                }
                Resource.error(e.message)
            }

        }
    }

    override suspend fun getSessionSummary(sessionId: String): Resource<SuccessResponse> {
        val body = HashMap<String, String>()
        body["sessionId"] = sessionId
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    apiService.getSessionSummary(appPrefManager.user.mToken.toString(), body)
                if (response.isSuccessful) {
                    Resource.success(response.body())
                } else
                    Resource.error(response.message())
            } catch (e: Exception) {
                logError {
                    message = "${body.toJson()}"
                    exception = e
                    function = "getSessionById"
                }
                Resource.error(e.message)
            }

        }
    }

    override suspend fun markSessionClosedById(sessionId: String): Resource<SuccessResponse> {
        val body = HashMap<String, String>()
        body["sessionId"] = sessionId
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    apiService.markSessionClosedById(appPrefManager.user.mToken.toString(), body)
                if (response.isSuccessful) {
                    Resource.success(response.body())
                } else
                    Resource.error(response.message())
            } catch (e: Exception) {
                logError {
                    message = "${body.toJson()}"
                    exception = e
                    function = "markSessionClosedById"
                }
                Resource.error(e.message)
            }

        }
    }
}
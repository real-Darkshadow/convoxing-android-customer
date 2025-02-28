package com.convoxing.convoxing_customer.data.remote.models

import com.google.gson.annotations.SerializedName

data class SessionHistory(
    val _id: String,
    val createdAt: String,
    val messages: ArrayList<ChatMessage>,
    @SerializedName("metadata")
    val metadata: SessionMetadata,
    val sessionName: String,
    val updatedAt: String,
    val userId: String,
    val category: String,
    val sessionTimestamp: String? = null,
    val averageTotalWords: Int? = null,
    val sessionId: String? = null,

    )

data class SessionMetadata(
    val status: String,
    val totalMessages: Int,
    val isClosedByUser: Boolean
)
package com.convoxing.convoxing_customer.data.remote.models

data class SessionRating(
    val comment: String,
    val isReviewed: Boolean,
    val rating: Int,
    val sessionId: String,
    val sessionType: String,
)
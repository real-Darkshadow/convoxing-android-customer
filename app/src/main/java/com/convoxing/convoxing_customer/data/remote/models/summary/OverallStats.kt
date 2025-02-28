package com.convoxing.convoxing_customer.data.remote.models.summary

data class OverallStats(
    val description: String,
    val formattedScore: String,
    val grade: String,
    val interpretation: String,
    val score: Double,
    val title: String
)
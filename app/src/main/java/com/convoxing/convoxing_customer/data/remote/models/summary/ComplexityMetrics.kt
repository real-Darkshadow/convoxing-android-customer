package com.convoxing.convoxing_customer.data.remote.models.summary

data class ComplexityMetrics(
    val averageWordLength: Double,
    val complexWords: Int,
    val diversityRatio: Double,
    val uniqueWordCount: Int
)
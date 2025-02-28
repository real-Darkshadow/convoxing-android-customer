package com.convoxing.convoxing_customer.data.remote.models.summary

data class VocabularyStats(
    val analyzedMessageCount: Int,
    val averageScore: Double,
    val complexityMetrics: ComplexityMetrics,
    val description: String,
    val diversityRating: String,
    val formattedScore: String,
    val improvements: Improvements,
    val specificExamples: List<Any>,
    val title: String
)
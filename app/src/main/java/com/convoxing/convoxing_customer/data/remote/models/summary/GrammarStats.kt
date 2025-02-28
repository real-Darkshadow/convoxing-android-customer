package com.convoxing.convoxing_customer.data.remote.models.summary

data class GrammarStats(
    val analyzedMessageCount: Int,
    val averageScore: Double,
    val commonErrors: List<CommonError>,
    val description: String,
    val errorDensity: Double,
    val formattedScore: String,
    val improvementAreas: List<String>,
    val specificExamples: List<Any>,
    val title: String,
    val totalErrors: Int
)
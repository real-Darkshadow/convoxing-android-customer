package com.convoxing.convoxing_customer.data.remote.models.summary

data class Summary(
    val calculatedAt: String,
    val category: String,
    val grammarStats: GrammarStats,
    val language: String,
    val lastUpdated: String,
    val messageCount: Int,
    val mistakeSummary: String,
    val overallStats: OverallStats,
    val sessionId: String,
    val sessionName: String,
    val summaryOverview: String,
    val totalWordCount: Int,
    val vocabularyStats: VocabularyStats
)
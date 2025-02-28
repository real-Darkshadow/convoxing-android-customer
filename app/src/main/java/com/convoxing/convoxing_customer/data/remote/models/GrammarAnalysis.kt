package com.convoxing.convoxing_customer.data.remote.models

import com.google.gson.annotations.SerializedName

data class GrammarAnalysis(
    @SerializedName("error_category")
    val errorCategory: String,
    @SerializedName("error_explanation")
    val errorExplanation: String,
    @SerializedName("original_sentence")
    val originalSentence: String,
    @SerializedName("right_portion")
    val rightPortion: String,
    @SerializedName("wrong_portion")
    val wrongPortion: String
)
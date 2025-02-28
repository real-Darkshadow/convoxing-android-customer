package com.convoxing.convoxing_customer.data.remote.models

import com.google.gson.annotations.SerializedName

data class RandomVocab(
    val definition: String,
    val example: String,
    @SerializedName("part_of_speech")
    val partOfSpeech: String,
    val synonyms: String,
    val word: String
)
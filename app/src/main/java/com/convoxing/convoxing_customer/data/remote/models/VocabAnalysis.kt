package com.convoxing.convoxing_customer.data.remote.models

import com.google.gson.annotations.SerializedName

data class VocabAnalysis(
    @SerializedName("figure_of_speech_additions")
    val figureOfSpeechAdditions: ArrayList<FigureOfSpeechAddition>,
    @SerializedName("word_replacements")
    val wordReplacements: ArrayList<WordReplacements>
)

data class FigureOfSpeechAddition(
    @SerializedName("figure_of_speech_category")
    val figureOfSpeechCategory: String,
    @SerializedName("figure_of_speech_to_be_used")
    val figureOfSpeechToBeUsed: String,
    @SerializedName("meaning")
    val meaning: String,
    @SerializedName("modified_sentence")
    val modifiedSentence: String,
    @SerializedName("original_sentence")
    val originalSentence: String
)

data class WordReplacements(
    @SerializedName("original_sentence")
    val originalSentence: String,
    val replacements: ArrayList<Replacements>
)

data class Replacements(
    val word: String,
    val replacement: String
)
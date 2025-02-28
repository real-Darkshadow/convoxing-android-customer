package com.convoxing.convoxing_customer.data.models

enum class DeepLinkValues(val uri: String) {
    HOME(""),
    LEADERBOARD("leaderboard"),
    PRACTICE("practice"),
    PROFILE("profile"),
    PLANS("plans");

    companion object {
        @JvmStatic
        fun fromUri(uri: String): DeepLinkValues =
            entries.find { value -> value.uri == uri } ?: HOME
    }
}
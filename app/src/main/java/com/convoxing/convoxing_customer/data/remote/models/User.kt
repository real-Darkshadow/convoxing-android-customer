package com.convoxing.convoxing_customer.data.remote.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("token")
    var mToken: String? = null,
    @SerializedName("_id")
    var mId: String,
    val activeSubscription: List<Any>,
    val createdAt: String,
    val sessionStreak: Int,
    val coins: Int,
    val deviceDetails: String,
    val email: String,
    val facebookId: String,
    val googleId: String,
    val isBlock: Boolean,
    val isFacebookLoggedIn: Boolean,
    val isGoogleLoggedIn: Boolean,
    val isUserSubscribed: Boolean,
    val isLearnerSync: Boolean,
    val isLogsEnabled: Boolean,
    val isNewUser: Boolean,
    val isPolicyAccepted: Boolean,
    val isProfileSetup: Boolean,
    val isRatingAllowed: Boolean,
    val isSubscribedToEmail: Boolean,
    val joiningMotivation: List<Any>,
    val learnersReferred: Int,
    val longestStreak: Int,
    val name: String,
    val referral: String,
    val source: String,
    val status: String,
    val updatedAt: String,
    val ageGroup: String,
    val englishLevel: String,
    val freeSessions: Int,
    val isFreeSessionsLeft: Boolean,
    val lastSession: String,
    val totalSessions: Int,
    val totalStars: Int,
    val totalGems: Int,
    val totalSessionsCompleted: Int
)
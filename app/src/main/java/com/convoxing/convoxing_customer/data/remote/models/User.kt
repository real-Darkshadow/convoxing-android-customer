package com.convoxing.convoxing_customer.data.remote.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("token")
    var mToken: String? = null,
    @SerializedName("_id")
    var mId: String,
    val createdAt: String? = null,
    val sessionStreak: Int = 0,
    val coins: Int = 0,
    val deviceDetails: String? = null,
    val email: String,
    val facebookId: String? = null,
    val googleId: String? = null,
    val isBlock: Boolean = false,
    val isFacebookLoggedIn: Boolean = false,
    val isGoogleLoggedIn: Boolean = false,
    val isUserSubscribed: Boolean = false,
    val isLearnerSync: Boolean = false,
    val isLogsEnabled: Boolean = false,
    val isNewUser: Boolean = false,
    val isPolicyAccepted: Boolean = false,
    val isProfileSetup: Boolean = false,
    val isRatingAllowed: Boolean = false,
    val isSubscribedToEmail: Boolean = false,
    val learnersReferred: Int = 0,
    val longestStreak: Int = 0,
    val name: String,
    val referral: String? = null,
    val source: String? = null,
    val status: String? = null,
    val updatedAt: String? = null,
    val ageGroup: String,
    val englishLevel: String,
    val freeSessions: Int = 0,
    val isFreeSessionsLeft: Boolean = false,
    val lastSession: String? = null,
    val totalSessions: Int = 0,
    val totalStars: Int = 0,
    val totalGems: Int = 0,
    val totalSessionsCompleted: Int = 0
)
package com.convoxing.convoxing_customer.data.remote.models

import com.convoxing.convoxing_customer.data.remote.models.summary.Summary
import com.google.gson.annotations.SerializedName

data class SuccessResponse(
    val message: String,
    val statusCode: Int,
    val user: User? = null,
    val status: String,
    val activeSubscription: ActiveSubscription,
    @SerializedName("summary")
    val chatSummary: Summary,


//
//    @SerializedName("plansData")
//    val plansData: ArrayList<Plan>? = null,
//
//    @SerializedName("plans")
//    val planV1: ArrayList<Plan>? = null,
//
//    @SerializedName("sessions")
//    val sessions: ArrayList<Session>,
//    /*
//
//        @SerializedName("teacherData")
//        val session: TeacherData? = null,
//    */
//
//    @SerializedName("subscriptions")
//    val subscriptions: ArrayList<Subscription>? = null,
//
//    @SerializedName("currentsubscriptions")
//    val currentsubscription: ArrayList<Subscription>,


    @SerializedName("corrections")
    val grammarAnalysis: ArrayList<GrammarAnalysis>? = null,

    val vocabAnalysis: VocabAnalysis? = null,

    @SerializedName("durations")
    val durations: ArrayList<String>? = null,

    @SerializedName("sessionTaken")
    val sessionTaken: Int = 0,

    @SerializedName("currentStreak")
    val currentStreak: Int = 0,

    val situations: ArrayList<Scenario>? = null,
    val themes: ArrayList<Scenario>? = null,

    val hint: String? = null,

    val betterAnswer: String? = null,

    val randomVocab: RandomVocab? = null,

    val chatSessions: ArrayList<SessionHistory>? = null,
    val chatSession: SessionHistory? = null,
    val sessions: ArrayList<SessionHistory>? = null,
    val sessionRating: SessionRating? = null,

//    @SerializedName("months")
//    val months: ArrayList<Month>? = null,
//
//    @SerializedName("refDiscount")
//    val refDiscount: Int = 0,
//
//    @SerializedName("transaction")
//    val transactions: ArrayList<Order>,
//
//    @SerializedName("transactions")
//    val walletTransactions: ArrayList<Wallet>,
//
//    @SerializedName("totalSession")
//    val totalCount: Int = 0,
//
//    @SerializedName("teachers")
//    val schedules: ArrayList<TeacherSchedule>? = null,
//
//    @SerializedName("pastBuddies")
//    val pastBuddies: ArrayList<PastBuddy>? = null,

    @SerializedName("demoCouponTime")
    val demoCouponTime: Double? = null,

    @SerializedName("demoCouponEndDate")
    val demoCouponEndDate: String? = null,

    @SerializedName("isDeviceTaken")
    val isDeviceTaken: Boolean = false,

    @SerializedName("demoCouponDiscount")
    val demoCouponDiscount: Int = 0,

    @SerializedName("coupon")
    val coupon: String? = null,

    @SerializedName("isZoomRecording")
    var isZoomRecording: Boolean? = null,

//    @SerializedName("meeting")
//    var meeting: MeetingRecording? = null,
//
//    @SerializedName("bannerCoupon")
//    var bannerCoupon: Banner? = null,
//
//    @SerializedName("avgRating")
//    var avgRating: Double? = null,
//
//    @SerializedName("courseMembershipPrice")
//    var courseMembershipPrice: Int? = null,
//
//    @SerializedName("CompanyData")
//    var companyData: ArrayList<CompanyData>? = null,
//
//    @SerializedName("Teacher")
//    var teachers: ArrayList<Teacher>? = null,
//
//    @SerializedName("storyData")
//    var clapingoStories: ArrayList<ClapingoStories>? = null,
//
//    @SerializedName("referralContactsData1")
//    var referContacts: ArrayList<Contact>? = null,
//
//    @SerializedName("referralContactsDetails")
//    var getReferContacts: ArrayList<Contact>? = null,

    var courseMessage: String? = null,

    var defaultChecked: Boolean? = null,

    @SerializedName("signature")
    var zoomSignature: String? = null,

//    @SerializedName("updatedMessage")
//    var updateMessages: Message? = null,
//
//    @SerializedName("answerSubmitted")
//    var answerSubmitted: Boolean = false,
//
//    @SerializedName("learnerLevel")
//    var learnerLevel: String? = null,
//
//    @SerializedName("totalScore")
//    var assessmentScore: Int? = null,
//
//    @SerializedName("feedbackeDetails")
//    var teacherFeedback: TeacherFeedback? = null,
//
//    @SerializedName("activeInstantBookingRequest")
//    var activeInstantBookingRequest: ArrayList<PeerRequest>? = null,
//
//    @SerializedName("transcript")
//    var peerTranscript: ArrayList<PeerTranscript>? = null,
//
//    @SerializedName("friends")
//    var peerFriends: ArrayList<PeerFriend>? = null,
//
//    @SerializedName("userState")
//    var userState: UserState? = null,
//
//    @SerializedName("interviewRoom")
//    var interviewRoom: Session? = null,

//    @SerializedName("summary_point")
//    var peerAiSummary: ArrayList<PeerAiSummary>,
//
//    @SerializedName("pronounciation")
//    var pronunciation: ArrayList<Pronunciation>,
//
//    @SerializedName("filler_words")
//    var fillerWords: ArrayList<FillerWord>,
//
//    @SerializedName("vocabulary_corrections")
//    var vocabCorrections: ArrayList<Correction>,
//
//    @SerializedName("grammar_corrections")
//    var grammarCorrections: ArrayList<Correction>,
//
//    @SerializedName("summary")
//    var summary: String,
//
//    val learnerCertificate: ArrayList<Any>?,
//    @SerializedName("data")
//    val onboardingStepsList: ArrayList<OnboardingItemV2>,
)

package com.convoxing.convoxing_customer.data.remote.models

import com.google.gson.annotations.SerializedName

data class Scenario(
    @SerializedName("_id")
    val id: String,
    @SerializedName("loading_screen_info")
    val loadingScreenInfo: LoadingScreenInfo,
    val metadata: Metadata,
    val name: String,
    @SerializedName("stars_info")
    val starsInfo: StarsInfo,
    @SerializedName("long_name")
    val longName: String,
    @SerializedName("short_name")
    val shortName: String,
)

data class StarsInfo(
    val achieved: Int,
    val total: Int
)

data class Metadata(
    @SerializedName("access_tier")
    val accessTier: String,
    @SerializedName("deployment_stage")
    val deploymentStage: String,
    val tags: ArrayList<String>,
    @SerializedName("inc_purpose_list")
    val purposeList: ArrayList<String>
)

data class LoadingScreenInfo(
    val subtitle: String,
    val title: String
)
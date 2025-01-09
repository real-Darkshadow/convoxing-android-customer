package com.convoxing.convoxing_customer.data.repository.installreferrer

import com.amplitude.android.Amplitude
import com.convoxing.convoxing_customer.ConvoxingApp
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import com.convoxing.convoxing_customer.utils.analytics.AnalyticsHelperUtil
import com.datadog.android.log.Logger
import javax.inject.Inject

class InstallReferrerRepository @Inject constructor(
    private val context: ConvoxingApp,
    private val appPrefManager: AppPrefManager,
    private val amplitude: Amplitude,
    private val analyticsHelperUtil: AnalyticsHelperUtil,
    private val encryptionHelper: EncryptionHelper,
    private val logger: Logger,
) : InstallReferrerRepositoryInterface {

//    private val _referrerResult = MutableStateFlow<ReferrerResult?>(null)
//    override val referrerResult: StateFlow<ReferrerResult?> = _referrerResult
//
//
//    override suspend fun getInstallReferrer() {
//        if (hasReferrerBeenRetrieved()) {
//            _referrerResult.value = ReferrerResult.Success(getStoredReferrer())
//            return
//        }
//        val result = suspendCancellableCoroutine { continuation ->
//            val installReferrerClient = InstallReferrerClient.newBuilder(context).build()
//            val listener = object : InstallReferrerStateListener {
//                override fun onInstallReferrerSetupFinished(responseCode: Int) {
//                    when (responseCode) {
//                        InstallReferrerClient.InstallReferrerResponse.OK -> {
//                            try {
//                                val response = installReferrerClient.installReferrer
//                                val referrerUrl = ReferrerUrl.parse(response.installReferrer)
//                                val referrerInfo = ReferrerInfo(
//                                    referrerUrl = referrerUrl,
//                                    referrerClickTimestampSeconds = response.referrerClickTimestampSeconds,
//                                    appInstallTimestampSeconds = response.installBeginTimestampSeconds,
//                                    googlePlayInstantParam = response.googlePlayInstantParam,
//                                    installVersion = response.installVersion ?: "",
//                                )
//
//                                val utmContents = extractUtmContentsFromFacebook(
//                                    referrerInfo.referrerUrl.utmParameters.content
//                                )
//
//                                val identity = Identify()
//                                referrerInfo.referrerUrl.utmParameters.source?.let {
//                                    identity.set("utm_source", it)
//                                }
//                                referrerInfo.referrerUrl.utmParameters.campaign?.let {
//                                    identity.set("utm_campaign", it)
//                                }
//                                referrerInfo.referrerUrl.utmParameters.medium?.let {
//                                    identity.set("utm_medium", it)
//                                }
//                                referrerInfo.referrerUrl.utmParameters.term?.let {
//                                    identity.set("utm_term", it)
//                                }
//                                referrerInfo.referrerUrl.utmParameters.content?.let {
//                                    identity.set("utm_content", it)
//                                }
//                                identity.set("app_install_version", referrerInfo.installVersion)
//                                identity.set(
//                                    "click_timestamp",
//                                    referrerInfo.referrerClickTimestampSeconds
//                                )
//                                identity.set(
//                                    "install_timestamp",
//                                    referrerInfo.appInstallTimestampSeconds
//                                )
//
//
//                                // INSERT_YOUR_CODE
//                                val eventProperties = mutableMapOf(
//                                    "utm_source" to referrerInfo.referrerUrl.utmParameters.source,
//                                    "utm_campaign" to referrerInfo.referrerUrl.utmParameters.campaign,
//                                    "utm_medium" to referrerInfo.referrerUrl.utmParameters.medium,
//                                    "utm_term" to referrerInfo.referrerUrl.utmParameters.term,
//                                    "utm_content" to referrerInfo.referrerUrl.utmParameters.content,
//                                    "app_install_version" to referrerInfo.installVersion,
//                                    "click_timestamp" to referrerInfo.referrerClickTimestampSeconds,
//                                    "install_timestamp" to referrerInfo.appInstallTimestampSeconds
//                                )
//                                analyticsHelperUtil.logEvent("install_referrer", eventProperties)
//
//                                amplitude.identify(identity)
//
//                                storeReferrer(referrerInfo)
//                                continuation.resume(ReferrerResult.Success(referrerInfo))
//                            } catch (e: Exception) {
//                                continuation.resume(ReferrerResult.Error(e))
//                            }
//                        }
//
//                        else -> continuation.resume(ReferrerResult.Error(Exception("Failed to setup referrer: $responseCode")))
//                    }
//                    installReferrerClient.endConnection()
//                }
//
//                override fun onInstallReferrerServiceDisconnected() {
//                    continuation.resume(ReferrerResult.Error(Exception("Referrer service disconnected")))
//                }
//            }
//
//            installReferrerClient.startConnection(listener)
//            continuation.invokeOnCancellation {
//                installReferrerClient.endConnection()
//            }
//        }
//
//        _referrerResult.value = result
//    }
//
//
//    private fun hasReferrerBeenRetrieved(): Boolean {
//        return appPrefManager.isReferrerRetrieved
//    }
//
//    private fun storeReferrer(referrer: ReferrerInfo) {
//        appPrefManager.setInstallReferrer(referrer, true)
//    }
//
//    private fun getStoredReferrer(): ReferrerInfo {
//        return appPrefManager.installReferrerInfo
//    }
//
//    fun extractUtmContentsFromFacebook(utmContents: String?): InstallReferrerData? {
//
//        try {
//            if (utmContents.isNullOrBlank())
//                return null
//            val utmContent = JSONObject(utmContents)
//            val source = utmContent.getJSONObject("source")
//            val encryptedData = source.getString("data")
//            val nonce = source.getString("nonce")
//            val decryptedData = encryptionHelper.decrypt(encryptedData, nonce)
//            val decryptedJson = JSONObject(decryptedData)
//            val installReferrerData = InstallReferrerData(
//                adId = decryptedJson.getString("ad_id") ?: "",
//                adGroupId = decryptedJson.getString("adgroup_id") ?: "",
//                adGroupName = decryptedJson.getString("adgroup_name") ?: "",
//                campaignId = decryptedJson.getString("campaign_id") ?: "",
//                campaignName = decryptedJson.getString("campaign_name") ?: "",
//                campaignGroupId = decryptedJson.getString("campaign_group_id") ?: "",
//                campaignGroupName = decryptedJson.getString("campaign_group_name") ?: "",
//                accountId = decryptedJson.getString("account_id") ?: "",
//                adObjectiveName = decryptedJson.getString("ad_objective_name") ?: "",
//                publisherPlatform = decryptedJson.getString("publisher_platform"),
//                isInstagram = decryptedJson.getBoolean("is_instagram"),
//                platformPosition = decryptedJson.getString("platform_position"),
//            )
//            val referrerDataMap = HashMap<String, Any>().apply {
//                installReferrerData.adId?.let { put("adId", it) }
//                installReferrerData.adGroupId?.let { put("adGroupId", it) }
//                installReferrerData.adGroupName?.let { put("adGroupName", it) }
//                installReferrerData.campaignId?.let { put("campaignId", it) }
//                installReferrerData.campaignName?.let { put("campaignName", it) }
//                installReferrerData.campaignGroupId?.let { put("campaignGroupId", it) }
//                installReferrerData.campaignGroupName?.let { put("campaignGroupName", it) }
//                installReferrerData.accountId?.let { put("accountId", it) }
//                installReferrerData.adObjectiveName?.let { put("adObjectiveName", it) }
//                installReferrerData.publisherPlatform?.let { put("publisher_platform", it) }
//                installReferrerData.isInstagram?.let { put("is_instagram", it) }
//                installReferrerData.platformPosition?.let { put("platform_position", it) }
//            }
//            amplitude.identify(referrerDataMap)
//
//            analyticsHelperUtil.logEvent("install_referrer", referrerDataMap)
//            return installReferrerData
//        } catch (e: Exception) {
//            logger.i("error_parsing_utm_contents: ${e.message}")
//        }
//        return null
//    }
}
package com.convoxing.convoxing_customer.data.repository.home

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract
import com.convoxing.convoxing_customer.ConvoxingApp
import com.convoxing.convoxing_customer.data.local.AppPrefManager
import com.convoxing.convoxing_customer.data.remote.api.ApiService
import com.datadog.android.log.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.net.URL
import java.util.TimeZone
import javax.inject.Named
import kotlin.math.roundToInt

class MainRepository(
    val context: ConvoxingApp,
    val apiService: ApiService,
    val appPrefManager: AppPrefManager,
    @Named("device_id") private val deviceID: String,
    private val logger: Logger,
) : MainRepositoryInterface {
//    override suspend fun getLearner(): SuccessResponse {
//        val headerMap = HashMap<String, String>()
//        headerMap["x-access-token"] = appPrefManager.user.mToken.toString()
//
//        val requestBody = HashMap<String, String>()
//        requestBody["learnerId"] = appPrefManager.user.mId.toString()
//        requestBody["timezone"] = TimeZone.getDefault().id
//
//        requestBody["deviceId"] = deviceID
//
//        return withContext(Dispatchers.IO) { apiService.findLearnerById(headerMap, requestBody) }
//    }
//
//    override fun setUserReviewedApp() {
//        appPrefManager.isUserReviewed = true
//    }
//
//    override fun hasUserReviewedApp(): Boolean {
//        return appPrefManager.isUserReviewed
//    }
//
//
//    override fun logout() {
//        appPrefManager.logoutUser()
//
//    }
//
//    override suspend fun updateUserPincode(pinCode: String): Resource<SuccessResponse> {
//        val headerMap = HashMap<String, String>()
//        headerMap["x-access-token"] = appPrefManager.user.mToken.toString()
//        val bodyMap = HashMap<String, Any>()
//        bodyMap["postalCode"] = pinCode.trim()
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.setupLearnerProfile(headerMap, bodyMap)
//                Resource.success(result)
//            } catch (e: Exception) {
//                Resource.error(e.message)
//            }
//        }
//    }
//
//    override suspend fun getReferCode(): Resource<ReferCode> {
//        val requestBody = HashMap<String, String>()
//
//        return withContext(Dispatchers.IO) {
//
//            try {
//                val headerMap = HashMap<String, String>()
//                headerMap["x-access-token"] = appPrefManager.user.mToken.toString()
//
//                requestBody["learnerId"] = appPrefManager.user.mId.toString()
//                val result = apiService.getReferal(headerMap, requestBody)
//                Resource.success(result)
//            } catch (e: Exception) {
//
//                logError {
//                    exception = e
//                    message = """
//                        ${requestBody.toJson()}
//                    """.trimIndent()
//                    function = "getReferCode"
//                }
//                Resource.error(e.message)
//
//            }
//
//
//        }
//
//
//    }
//
//    override suspend fun getUSDRate(): Dollar = apiService.getUSDinInr()
//
//
//    override suspend fun addUserDetails(
//        name: String,
//        email: String,
//        institution: String,
//        profileUrl: URL?,
//        dob: String,
//        postalCode: String,
//        employmentStatus: String,
//        firstLanguage: String,
//        profession: String,
//
//        ): Resource<SuccessResponse> {
//        val headerMap = HashMap<String, String>()
//        headerMap["x-access-token"] = appPrefManager.user.mToken.toString()
//        val bodyMap = HashMap<String, Any>()
//        bodyMap["learner"] = appPrefManager.user.mId.toString()
//        bodyMap["name"] = name.trim()
//        bodyMap["email"] = email.lowercase().trim()
//        if (institution.isNotEmpty()) bodyMap["institution"] = institution.trim()
//        bodyMap["dob"] = dob.trim()
//        bodyMap["employmentStatus"] = employmentStatus.trim()
//        if (profession.isNotEmpty()) bodyMap["profession"] = profession.trim()
//        bodyMap["firstLanguage"] = firstLanguage.trim()
//        if (profileUrl != null)
//            bodyMap["imageUrl"] = profileUrl.toString()
//        bodyMap["postalCode"] = postalCode.trim()
//        bodyMap["isSpeakanaUser"] = true
//
//        return withContext(Dispatchers.IO) {
//
//            try {
//                val result = apiService.setupLearnerProfile(headerMap, bodyMap)
//                if (result.mStatusCode.isHttpSuccessCode())
//                    return@withContext Resource.success(result)
//                return@withContext Resource.error(result.message)
//
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//
//    }
//
//    override suspend fun updateLearnerDetails(
//        name: String,
//        dob: String,
//        gender: String,
//    ): Resource<SuccessResponse> {
//
//        val headerMap = HashMap<String, String>()
//        headerMap["x-access-token"] = appPrefManager.user.mToken.toString()
//        val bodyMap = HashMap<String, Any>()
//        bodyMap["learner"] = appPrefManager.user.mId.toString()
//        bodyMap["name"] = name.trim()
//
//        bodyMap["dob"] = dob.trim()
//        bodyMap["gender"] = gender.trim()
//        bodyMap["isSpeakanaUser"] = true
//
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.setupLearnerProfile(headerMap, bodyMap)
//                if (result.mStatusCode.isHttpSuccessCode())
//                    return@withContext Resource.success(result)
//                return@withContext Resource.error(result.message)
//
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//
//    }
//
//
//    override suspend fun getReferHistory(page: Int, limit: Int): Resource<ReferHistoryResponse> {
//        return withContext(Dispatchers.IO) {
//
//            try {
//                val result = apiService.getReferHistory(
//                    token = appPrefManager.user.mToken.toString(),
//                    page = page,
//                    limit = limit
//                )
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.message())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//
//        }
//
//    }
//
//    override suspend fun askPhoneVerifyOtp(phoneNumber: String): Resource<SuccessResponse> {
//        val body = HashMap<String, String>()
//        body["phoneNumber"] = phoneNumber.trim()
//        return withContext(Dispatchers.IO) {
//
//            try {
//                val result =
//                    apiService.askPhoneVerifyOtp(appPrefManager.user.mToken.toString(), body)
//                if (result.mStatusCode.isHttpSuccessCode()) {
//                    Resource.success(result)
//                } else {
//                    logError {
//                        function = "askLearnerOTP"
//                        message = "$body"
//                        exception = Exception(result.message)
//                    }
//                    Resource.error(result.message)
//                }
//            } catch (e: Exception) {
//                logError {
//                    function = "askLearnerOTP"
//                    message = "$body"
//                    exception = e
//                }
//                Resource.error(e.parseErrorMessage())
//            }
//
//        }
//    }
//
//    override suspend fun updateRestoreId(restoreId: String): Resource<SuccessResponse> {
//
//        val body = HashMap<String, String>()
//        body["restoreId"] = restoreId
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.updateRestoreId(appPrefManager.user.mToken.toString(), body)
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.message())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun getInvitedContacts(): Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//
//            try {
//                val result = apiService.getInvitedContacts(appPrefManager.user.mToken.toString())
//                if (result.mStatusCode.isHttpSuccessCode())
//                    Resource.success(result)
//                else
//                    Resource.error(result.message)
//
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun referContact(it: Contact): Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//            val body = HashMap<String, String>()
//            body["phoneNumber"] = it.phoneNumber!!
//            body["name"] = it.name!!
//            try {
//                val result = apiService.referContact(appPrefManager.user.mToken.toString(), body)
//                if (result.mStatusCode.isHttpSuccessCode())
//                    Resource.success(result)
//                else
//                    Resource.error(result.message)
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//
//
//        }
//    }
//
//    override suspend fun getContacts(context: Context): Resource<List<Contact>> {
//        return withContext(Dispatchers.IO) {
//            val cachedContacts = appPrefManager.contacts
//            if (cachedContacts != null)
//                return@withContext Resource.success(cachedContacts)
//            val contacts = withContext(Dispatchers.IO) {
//                fetchContacts(context).sortedBy { it.name?.lowercase() }
//            }
//            appPrefManager.contacts = contacts
//            return@withContext Resource.success(contacts)
//        }
//
//    }
//
//    @SuppressLint("Range")
//    private fun fetchContacts(context: Context): List<Contact> {
//        val contactsMap = mutableMapOf<String, Contact>()
//
//        val selection = null
//        val selectionArgs = null
//        val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
//        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
//
//
//        val cursor =
//            context.contentResolver.query(uri, null, selection, selectionArgs, sortOrder)
//
//        cursor?.use { c ->
//            val nameIndex =
//                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
//            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//
//            while (c.moveToNext()) {
//                val name =
//                    c.getString(nameIndex)
//                val phoneNumber = cursor.getString(numberIndex)?.replace("\\s+".toRegex(), "") ?: ""
//
//                if (phoneNumber.isNotEmpty()) {
//                    // Standardize phone number without country code for comparison
//                    val standardizedNumber = if (phoneNumber.startsWith("+91")) {
//                        phoneNumber.removePrefix("+91")
//                    } else {
//                        phoneNumber
//                    }
//
//                    // Prioritize number with +91 prefix
//                    if (phoneNumber.startsWith("+91")) {
//                        contactsMap[standardizedNumber] = Contact(name, phoneNumber)
//                    } else if (!contactsMap.containsKey(standardizedNumber)) {
//                        contactsMap[standardizedNumber] = Contact(name, phoneNumber)
//                    }
//                }
//                if (c.isLast)
//                    break
//            }
//        }
//        cursor?.close()
//        return contactsMap.values.toList()
//    }
//
//
//    override suspend fun verifyPhoneOtp(
//        otp: String,
//        phoneNumber: String,
//    ): Resource<SuccessResponse> {
//        val body = HashMap<String, String>()
//        body["phoneNumber"] = phoneNumber.trim()
//        body["otp"] = otp
//        return withContext(Dispatchers.IO) {
//
//            try {
//                val result = apiService.phoneVerifyOtp(appPrefManager.user.mToken.toString(), body)
//                if (result.mStatusCode.isHttpSuccessCode()) {
//                    Resource.success(result)
//                } else {
//                    logError {
//                        function = "phoneVerifyOTP"
//                        message = "$body"
//                        exception = Exception(result.message)
//                    }
//                    Resource.error(result.message)
//                }
//            } catch (e: Exception) {
//                logError {
//                    function = "phoneVerifyOTP"
//                    message = "$body"
//                    exception = e
//                }
//                Resource.error(e.parseErrorMessage())
//            }
//
//        }
//    }
//
//    override suspend fun resendPhoneVerifyOtp(phoneNumber: String): Resource<SuccessResponse> {
//        val body = HashMap<String, String>()
//        body["phoneNumber"] = phoneNumber.trim()
//        return withContext(Dispatchers.IO) {
//
//            try {
//                val result =
//                    apiService.resendPhoneVerifyOtp(appPrefManager.user.mToken.toString(), body)
//                if (result.mStatusCode.isHttpSuccessCode()) {
//                    Resource.success(result)
//                } else {
//                    logError {
//                        function = "resendPhoneVerifyOtp"
//                        message = "$body"
//                        exception = Exception(result.message)
//                    }
//                    Resource.error(result.message)
//                }
//            } catch (e: Exception) {
//                logError {
//                    function = "resendPhoneVerifyOtp"
//                    message = "$body"
//                    exception = e
//                }
//                Resource.error(e.parseErrorMessage())
//            }
//
//        }
//    }
//
//
//    override suspend fun getSubscriptions(
//        subject: String,
//        peerSubscription: Boolean,
//    ): SuccessResponse {
//        val body = HashMap<String, Any>()
//        val id = appPrefManager.user.mId.toString()
//        val token = appPrefManager.user.mToken.toString()
//        return withContext(Dispatchers.IO) {
//            val headerMap = HashMap<String, String>()
//            headerMap["x-access-token"] = token
//
//            body["learnerId"] = id
//            body["subject"] = subject
//            if (peerSubscription)
//                body["peerSubscription"] = true
//
//            val result = apiService.getSubscriptions(headerMap, body)
//            result
//        }
//
//    }
//
//
//    override suspend fun acceptPrivacy(): SuccessResponse {
//
//        val body = HashMap<String, String>().apply {
//            put("learnerId", appPrefManager.user.mId.toString())
//        }
//        val token = appPrefManager.user.mToken.toString()
//        return withContext(Dispatchers.IO) {
//            apiService.acceptPrivacy(body = body, token)
//        }
//    }
//
//    override suspend fun getAllOrders(): Resource<SuccessResponse> {
//
//        return withContext(Dispatchers.IO) {
//            try {
//                val headerMap = HashMap<String, String>()
//                headerMap["x-access-token"] = appPrefManager.user.mToken.toString()
//                val result = apiService.getUserTransactions(headerMap)
//                if (result.mStatusCode.isHttpSuccessCode()) {
//                    Resource.success(result)
//                } else {
//
//                    logError {
//                        message = """
//                              ${result.message}
//                        """.trimIndent()
//                        function = "getAllOrders"
//                    }
//                    Resource.error(result.message)
//                }
//            } catch (e: Exception) {
//                logError {
//                    exception = e
//                    function = "getAllOrders"
//
//                }
//                Resource.error(e.message)
//            }
//
//        }
//
//    }
//
//    override suspend fun getWalletDetails(): Resource<SuccessResponse> {
//
//        val requestBody = HashMap<String, String>()
//        return withContext(Dispatchers.IO) {
//
//            try {
//                val headerMap = HashMap<String, String>()
//                headerMap["x-access-token"] = appPrefManager.user.mToken.toString()
//
//                requestBody["learnerId"] = appPrefManager.user.mId.toString()
//                val result = apiService.getWallet(headerMap, requestBody)
//
//                if (result.mStatusCode.isHttpSuccessCode()) {
//                    Resource.success(result)
//                } else {
//                    logError {
//                        message = """
//                              ${result.message}
//                        body = $requestBody
//                        """.trimIndent()
//                        function = "getWalletDetails"
//                    }
//                    Resource.error(result.message)
//                }
//
//
//            } catch (e: Exception) {
//
//                logError {
//                    exception = e
//                    message = """
//                              ${e.message}
//                        body = $requestBody
//                        """.trimIndent()
//                    function = "getWalletDetails"
//
//                }
//                Resource.error(e.message)
//            }
//
//        }
//
//    }
//
//
//    override suspend fun getLiveRequest(): Resource<SuccessResponse> {
//
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.getLivePeerRequest(appPrefManager.user.mToken.toString())
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.message())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//
//        }
//    }
//
//
//    override fun setToken(token: String) {
//        val user = appPrefManager.user
//        user.mToken = token
//        appPrefManager.user = user
//    }
//
//    override fun updateLearner(learner: Learner) {
//        val token = appPrefManager.user.mToken.toString()
//        learner.mToken = token
//        appPrefManager.user = learner
//    }
//
//    override suspend fun addProfileImage(profileUrl: String): Resource<SuccessResponse> {
//        val body = HashMap<String, Any>().apply {
//            put("imageUrl", profileUrl)
//        }
//        body["isSpeakanaUser"] = true
//
//        val headerMap = HashMap<String, String>()
//        headerMap["x-access-token"] = appPrefManager.user.mToken.toString()
//
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.setupLearnerProfile(headerMap, body)
//                Resource.success(result)
//            } catch (e: Exception) {
//                Resource.error(e.message)
//            }
//        }
//    }
//
//    override suspend fun addIntroductionVideoUrl(profileUrl: String): Resource<SuccessResponse> {
//        val body = HashMap<String, Any>().apply {
//            put("p2pVideoUrl", profileUrl)
//        }
//        body["isSpeakanaUser"] = true
//        val headerMap = HashMap<String, String>()
//        headerMap["x-access-token"] = appPrefManager.user.mToken.toString()
//
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.setupLearnerProfile(headerMap, body)
//                Resource.success(result)
//            } catch (e: Exception) {
//                Resource.error(e.message)
//            }
//        }
//    }
//
//
//    override suspend fun getPastSessionPaging(
//        page: Int,
//        limit: Int,
//        timezone: String,
//        subject: String,
//        isPeerLearning: Boolean,
//    ): SuccessResponse {
//        val bodyMap = HashMap<String, Any>()
//        bodyMap["studentID"] = appPrefManager.user.mId.toString()
//        bodyMap["page"] = page.toString()
//        bodyMap["limit"] = limit.toString()
//        bodyMap["timezone"] = timezone
//        bodyMap["subject"] = subject
//        if (isPeerLearning)
//            bodyMap["isPeerLearning"] = true
//        val token = appPrefManager.user.mToken.toString()
//        return withContext(Dispatchers.IO) {
//            apiService.getPastSessionsPaging(token, bodyMap)
//        }
//    }
//
//    override suspend fun createOrder(
//        notes: String,
//        amount: Double,
//        currency: String,
//        courseName: String,
//        isDemo: Boolean,
//    ): OrderDetails {
//        val notesBody = JSONObject()
//        notesBody.putOpt("Name", appPrefManager.user.userName)
//        notesBody.putOpt("Number", appPrefManager.user.phoneNumber ?: "N/A")
//        notesBody.putOpt("Email", appPrefManager.user.email ?: "N/A")
//        notesBody.putOpt("medium", notes)
//        notesBody.putOpt("subject", "English")
//        notesBody.putOpt("learnerID", appPrefManager.user.mId.toString())
//        val jsonBody = JSONObject()
//        jsonBody.putOpt("payment_capture", true)
//        jsonBody.putOpt("amount", amount)
//        jsonBody.putOpt("notes", notesBody)
//        jsonBody.put("currency", currency)
//        jsonBody.putOpt("txndesc", courseName)
//        jsonBody.putOpt("key_id", appPrefManager.razorpayKey)
//        jsonBody.putOpt("key_secret", appPrefManager.razorpaySecret)
//        jsonBody.putOpt("source", Constants.CLAPINGO_SOURCE)
//        val headerMap = HashMap<String, String>()
//        headerMap["x-access-token"] = appPrefManager.user.mToken.toString()
//
//        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())
//
//        return withContext(Dispatchers.IO) { apiService.getRazorPayOrderId(headerMap, requestBody) }
//    }
//
//
//    override val user: Learner
//        get() {
//            return appPrefManager.user
//        }
//
//    override fun getAddress(): AddressStripe {
//        return appPrefManager.address
//    }
//
//    override fun setAddress(addressStripe: AddressStripe) {
//        appPrefManager.address = (addressStripe)
//    }
//
//
//    override suspend fun getImages(): Resource<ImageResponse> {
//        val token = appPrefManager.user.mToken.toString()
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.getImages(token)
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.message())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//
//    override suspend fun stripeCheckout(
//        amount: Double,
//        currency: String,
//        desc: String,
//        addressStripe: AddressStripe,
//    ): Resource<StripeResponse> {
//        var stripe: StripeCreateOrder? = null
//        return withContext(Dispatchers.IO) {
//            try {
//
//                val notes = Notes(
//                    phone = appPrefManager.user.phoneNumber ?: "n/a",
//                    email = appPrefManager.user.email ?: "n/a",
//                    name = appPrefManager.user.userName ?: "NA",
//                    appVersion = "${BuildConfig.VERSION_NAME}@${BuildConfig.VERSION_CODE}",
//                    amount = (if (currency == "inr") (amount.roundToInt()) else amount).toString(),
//                    currency = currency,
//                    desc = desc,
//                    deviceId = deviceID,
//                    learner = appPrefManager.user.mId.toString(),
//                    learnerID = appPrefManager.user.mId.toString(),
//                    statusCode = 200.toString(),
//                    type = "Demo",
//                )
//
//                stripe =
//                    StripeCreateOrder(
//                        amount = if (currency == "inr") amount.roundToInt() else amount,
//                        currency = currency,
//                        notes = notes,
//                        txndesc = desc,
//                        address = addressStripe
//                    )
//                val token = appPrefManager.user.mToken.toString()
//
//                val result = stripe?.let {
//                    apiService.stripeCheckout(token, it)
//                }
//                if (result != null) Resource.success(result)
//                else Resource.error("Error")
//
//
//            } catch (e: Exception) {
//                logError {
//                    message = """
//                        ${stripe.toJson()}
//                    """.trimIndent()
//                    exception = e
//                    function = "stripeCheckout"
//                }
//                Resource.error(e.message)
//            }
//        }
//    }
//
//
//    override suspend fun searchCollegeData(keyword: String): Resource<SuccessResponse> {
//        val body = HashMap<String, String>().apply {
//            put("keyword", keyword)
//        }
//        return withContext(Dispatchers.IO) {
//
//            try {
//                val result = apiService.searchCollege(appPrefManager.user.mToken.toString(), body)
//                if (result.code().isHttpSuccessCode()) {
//                    Resource.success(result.body())
//                } else Resource.error(result.message())
//            } catch (e: Exception) {
//                logError {
//                    message = "$body"
//                    exception = e
//                    function = "searchCollegeData"
//                }
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//
//    override suspend fun addCollege(collegeName: String): Resource<SuccessResponse> {
//        val body = HashMap<String, String>().apply {
//            put("collegeName", collegeName)
//        }
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.addCollege(appPrefManager.user.mToken.toString(), body)
//                if (result.code().isHttpSuccessCode()) {
//                    Resource.success(result.body())
//                } else Resource.error(result.message())
//            } catch (e: Exception) {
//                logError {
//                    message = "$body"
//                    exception = e
//                    function = "searchCollegeData"
//                }
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//
//    override suspend fun getPinCode(pinCode: String): Resource<PinCodeResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//
//                val result = apiService.getPinCodeAddress(
//                    pinCode,
//                )
//
//                if (result.code().isHttpSuccessCode()) {
//                    Resource.success(result.body())
//                } else
//                    Resource.error(result.body().toString())
//            } catch (e: Exception) {
//                logError {
//                    function = "getPinCode"
//                    message = "Get pinCode api not working"
//                    exception = e
//                }
//                Resource.error(e.message)
//            }
//        }
//    }
//
//    override suspend fun changePeerStatus(online: Boolean): Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//            val body = HashMap<String, Any>()
//            body["online"] = online
//            try {
//                val result =
//                    apiService.changePeerStatus(appPrefManager.user.mToken.toString(), body)
//                if (result.code().isHttpSuccessCode()) {
//                    Resource.success(result.body())
//                } else {
//                    Resource.error(result.message())
//                }
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun getPeerBuddies(
//        page: Int,
//        limit: Int,
//        tz: String,
//    ): Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//            val body = HashMap<String, Any>()
//            body["learnerId"] = appPrefManager.user.mId.toString()
//            body["page"] = page
//            body["limit"] = limit
//            body["timezone"] = tz
//            try {
//                val result =
//                    apiService.getPastPeerBuddies(appPrefManager.user.mToken.toString(), body)
//                if (result.code().isHttpSuccessCode()) {
//                    Resource.success(result.body())
//                } else {
//                    Resource.error(result.message())
//                }
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun addPeerRating(sessionId: String, feedback: ArrayList<PeerFeedback>):
//            Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//            val body = HashMap<String, Any>()
//            body["sessionId"] = sessionId
//            body["feedback"] = feedback
//            try {
//                val result = apiService.addPeerRating(appPrefManager.user.mToken.toString(), body)
//                if (result.code().isHttpSuccessCode()) {
//                    Resource.success(result.body())
//                } else {
//                    Resource.error(result.message())
//                }
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun sendFriendRequest(friendId: String): Response<SuccessResponse> {
//        val body = HashMap<String, String>()
//        body["friendId"] = friendId
//        body["userId"] = appPrefManager.user.mId.toString()
//        return withContext(Dispatchers.IO) {
//            apiService.sendFriendRequest(appPrefManager.user.mToken.toString(), body)
//        }
//    }
//
//    override suspend fun acceptFriendRequest(
//        requestId: String,
//        status: String,
//    ): Response<SuccessResponse> {
//        val body = HashMap<String, String>()
//        body["requestId"] = requestId
//        body["status"] = status
//        return withContext(Dispatchers.IO) {
//            apiService.acceptFriendRequest(appPrefManager.user.mToken.toString(), body)
//        }
//    }
//
//    override suspend fun getAllRequestedFriend(
//    ): Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.getAllRequestedFriend(
//                    appPrefManager.user.mToken.toString(),
//                    appPrefManager.user.mId.toString()
//                )
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.body().toString())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun getAllAcceptedFriend(
//    ): Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.getAllAcceptedFriend(
//                    appPrefManager.user.mToken.toString(),
//                    appPrefManager.user.mId.toString()
//                )
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.body().toString())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun getAllSentFriendRequests(
//    ): Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.getAllSentFriendRequests(
//                    appPrefManager.user.mToken.toString(),
//                    appPrefManager.user.mId.toString()
//                )
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.body().toString())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun getLearnerPeerStreak(): Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.getLearnerPeerStreak(
//                    appPrefManager.user.mToken.toString(),
//                    appPrefManager.user.mId.toString()
//                )
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.body().toString())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun deleteAccount(): Resource<SuccessResponse> {
//        val body = HashMap<String, String>().apply {
//            put("learnerId", appPrefManager.user.mId.toString())
//        }
//        return withContext(Dispatchers.IO) {
//            try {
//                val response =
//                    apiService.deleteAccount(appPrefManager.user.mToken.toString(), body)
//                if (response.isSuccessful) {
//                    Resource.success(response.body())
//                } else
//                    Resource.error(response.message())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//
//        }
//    }
//
//    override suspend fun getPeerActivity(userId: String): Resource<PeerActivity> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.getPeerActivity(
//                    appPrefManager.user.mToken.toString(),
//                    userId
//                )
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.body().toString())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//
//    override suspend fun setPeerModeratorRetry(): Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.setPeerModeratorRetry(
//                    appPrefManager.user.mToken.toString(),
//                )
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.body().toString())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun updateNotificationId(token: String) {
//        return withContext(Dispatchers.IO) {
//
//
//            try {
//                val body = HashMap<String, String>().apply {
//                    put("notificationid", token)
//                    put("learnerId", appPrefManager.user.mId)
//                }
//                val result = apiService.updateNotificationId(
//                    appPrefManager.user.mToken.toString(), body
//                )
//
//            } catch (e: Exception) {
//                logError {
//                    message = "${e.message}"
//                    exception = e
//                    function = "updateNotificationId"
//                }
//            }
//
//        }
//    }
//
//    override suspend fun addLearnerBackground(
//        name: String,
//        phone: String,
//        email: String,
//        dob: String,
//        postalCode: String,
//        profession: String,
//        englishLevel: String,
//        bio: String,
//        joiningMotivation: ArrayList<String>,
//        interests: ArrayList<String>,
//        languages: ArrayList<String>,
//    ): Resource<SuccessResponse> {
//
//        val body = HashMap<String, Any>()
//
//        if (isNull(name).not())
//            body["name"] = name
//        if (isNull(phone).not())
//            body["phoneNumber"] = phone
//        if (isNull(email).not())
//            body["email"] = email
//        if (isNull(dob).not())
//            body["dob"] = dob
//        if (isNull(postalCode).not())
//            body["postalCode"] = postalCode
//        if (isNull(profession).not())
//            body["profession"] = profession
//        if (isNull(englishLevel).not())
//            body["englishLevel"] = englishLevel
//        if (isNull(bio).not())
//            body["peerBio"] = bio
//        body["joiningMotivation"] = joiningMotivation
//        body["interests"] = interests
//        body["languages"] = languages
//        body["isP2PProfileCompleted"] = true
//        val headerMap = HashMap<String, String>()
//        body["isSpeakanaUser"] = true
//
//        headerMap["x-access-token"] = appPrefManager.user.mToken.toString()
//
//
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.setupLearnerProfile(headerMap, body)
//
//                if (result.mStatusCode.isHttpSuccessCode())
//                    return@withContext Resource.success(result)
//                return@withContext Resource.error(result.message)
//
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun getMeetingRecording(meetingNo: String): Resource<SuccessResponse> {
//        val body = HashMap<String, String>()
//        body["meetingId"] = meetingNo
//        return withContext(Dispatchers.IO) {
//            try {
//                val result =
//                    apiService.getMeetingRecording(appPrefManager.user.mToken.toString(), body)
//                if (result.isSuccessful)
//                    Resource.success(result.body())
//                else
//                    Resource.error("Some Error happened!")
//
//            } catch (e: Exception) {
//                Resource.error(e.message)
//            }
//        }
//
//    }
//
//    override suspend fun getPresignedUrl(bucket: String, fileName: String, contentType: String):
//            Resource<PresignedApiResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//
//                val body: HashMap<String, String> = HashMap()
//                body["bucket"] = bucket
//                body["fileName"] = fileName
//                body["contentType"] = contentType
//
//                val result = apiService.getS3PresignedUrl(
//                    token = appPrefManager.user.mToken.toString(),
//                    body
//                )
//
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.body().toString())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun postDataToAwsPreSignedUrl(
//        url: String,
//        file: File,
//        isVideo: Boolean,
//    ): Resource<Unit> {
//        return withContext(Dispatchers.IO) {
//            try {
//
//                val body =
//                    if (!isVideo) file.asRequestBody("image/jpg".toMediaTypeOrNull()) else file.asRequestBody(
//                        "video/mp4".toMediaTypeOrNull()
//                    )
//                val result = apiService.postDataToS3Url(
//                    url,
//                    body
//                )
//
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.body().toString())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun getOnboardingSteps(): Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val result =
//                    apiService.getOnboardingSteps(appPrefManager.user.mToken.toString())
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.body().toString())
//
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun updateOnboardingState(stepsId: String): Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val body = HashMap<String, Any>()
//                body["watched"] = true
//                body["status"] = "completed"
//
//                val result =
//                    apiService.updateOnboardingState(
//                        appPrefManager.user.mToken.toString(),
//                        stepsId, body
//                    )
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.body().toString())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
//    override suspend fun updateLearnerEmail(
//        email: String,
//    ): Resource<SuccessResponse> {
//
//        val headerMap = HashMap<String, String>()
//        headerMap["x-access-token"] = appPrefManager.user.mToken.toString()
//        val bodyMap = HashMap<String, Any>()
//        bodyMap["learner"] = appPrefManager.user.mId.toString()
//        bodyMap["email"] = email.trim()
//        bodyMap["isSpeakanaUser"] = true
//
//        return withContext(Dispatchers.IO) {
//            try {
//                val result = apiService.setupLearnerProfile(headerMap, bodyMap)
//                if (result.mStatusCode.isHttpSuccessCode())
//                    return@withContext Resource.success(result)
//                return@withContext Resource.error(result.message)
//
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//
//    }
//
//    override suspend fun restoreAccount(): Resource<SuccessResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//
//                val requestBody = HashMap<String, String>()
//                requestBody["learnerId"] = appPrefManager.user.mId.toString()
//                val result = apiService.restoreAccount(
//                    appPrefManager.user.mToken.toString(),
//                    requestBody
//                )
//                if (result.code().isHttpSuccessCode())
//                    Resource.success(result.body())
//                else
//                    Resource.error(result.body().toString())
//            } catch (e: Exception) {
//                Resource.error(e.parseErrorMessage())
//            }
//        }
//    }
//
}

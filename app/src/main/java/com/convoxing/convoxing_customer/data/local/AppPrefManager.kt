package com.convoxing.convoxing_customer.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.convoxing.convoxing_customer.data.remote.models.User
import com.google.gson.Gson

class AppPrefManager(context: Context) {
    private val pref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    var isUserLoggedIn: Boolean
        get() = pref.getBoolean(PREF_IS_USER_LOGGED_ID, false)
        set(value) {
            editor.putBoolean(PREF_IS_USER_LOGGED_ID, value)
            editor.commit()
        }

    fun logoutUser() {
        editor.clear().commit()
    }

    // Add other getter and setter methods for other preferences

    var user: User
        get() {
            val data = pref.getString(PREF_USER_DATA, null)
            val gson = Gson()
            return gson.fromJson(data, User::class.java)
        }
        set(value) {
            // Preserve the existing token when updating user
            val currentUser = try {
                user  // Try to get current user
            } catch (e: Exception) {
                null  // If any error occurs, set to null
            }

            // Create a modified user object that maintains the token
            val updatedUser = if (currentUser?.mToken != null && value.mToken == null) {
                // If new user has null token but current user has token, preserve it
                value.copy(mToken = currentUser.mToken)
            } else {
                value
            }
            
            val gson = Gson()
            val userJson = gson.toJson(updatedUser)
            editor.putString(PREF_USER_DATA, userJson)
            editor.commit()
        }

    var isUserReviewed: Boolean
        get() = pref.getBoolean(PREF_REVIEWED, false)
        set(value) {
            editor.putBoolean(PREF_REVIEWED, value)
            editor.commit()
        }

    var isUserShowFreeSession: Boolean
        get() = pref.getBoolean(PREF_FREE_SESSION_POPUP, true)
        set(value) {
            editor.putBoolean(PREF_FREE_SESSION_POPUP, value)
            editor.commit()
        }

    var isFirstTimeUser: Boolean
        get() = pref.getBoolean(PREF_FIRST_TIME, true)
        set(value) {
            editor.putBoolean(PREF_FIRST_TIME, value)
            editor.commit()
        }


    companion object {
        private const val PREF_NAME = "convoxing-customer-app"
        private const val PREF_IS_USER_LOGGED_ID = "customer-app-is_use_logged_in"
        private const val PREF_USER_DATA = "convoxing_user"
        private const val PREF_REVIEWED = "reviewed"
        private const val PREF_FREE_SESSION_POPUP = "reviewed"
        private const val PREF_FIRST_TIME = "first_time"


    }
}
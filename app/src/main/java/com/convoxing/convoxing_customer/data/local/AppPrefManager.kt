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
            val gson = Gson()
            val userJson = gson.toJson(value)
            editor.putString(PREF_USER_DATA, userJson)
            editor.commit()
        }

    var isUserReviewed: Boolean
        get() = pref.getBoolean(PREF_REVIEWED, false)
        set(value) {
            editor.putBoolean(PREF_REVIEWED, value)
            editor.commit()
        }


    companion object {
        private const val PREF_NAME = "convoxing-customer-app"
        private const val PREF_IS_USER_LOGGED_ID = "customer-app-is_use_logged_in"
        private const val PREF_USER_DATA = "convoxing_user"
        private const val PREF_REVIEWED = "reviewed"

    }
}
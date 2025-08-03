package com.sopnolikhi.booksmyfriend.services.sessions

import android.content.Context
import android.content.SharedPreferences
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LoginSession {

    private const val LOGIN_PREFERENCES = "UserLoginPref"
    private const val KEY_USER_FULLNAME = "FullName"
    private const val KEY_USER_ID = "Uid"
    private const val KEY_USER_TOKEN = "Token"
    private const val KEY_USER_EXPIRED = "Expired"

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun saveLoginUserInfo(name: String, userid: String, token: String, expired: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_USER_FULLNAME, name)
            putString(KEY_USER_ID, userid)
            putString(KEY_USER_TOKEN, token)
            putString(KEY_USER_EXPIRED, expired)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        val hasUsername = sharedPreferences.contains(KEY_USER_ID)
        val hasToken = sharedPreferences.contains(KEY_USER_TOKEN)
        val expiredTime = sharedPreferences.getString(KEY_USER_EXPIRED, "")

        return hasUsername && hasToken && !isExpired(expiredTime)
    }

    private fun isExpired(expiredTime: String?): Boolean = try {
        val expirationDate = expiredTime?.let {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(
                it
            )
        }
        Date().run {
            if (after(expirationDate)) {
                logout()
                true
            } else {
                false
            }
        }
    } catch (e: ParseException) {
        e.printStackTrace()
        true // Treat as expired if there's a parsing error
    }


    private fun logout() {
        with(sharedPreferences.edit()) {
            remove(KEY_USER_FULLNAME)
            remove(KEY_USER_ID)
            remove(KEY_USER_TOKEN)
            remove(KEY_USER_EXPIRED)
            apply()
        }
    }
}
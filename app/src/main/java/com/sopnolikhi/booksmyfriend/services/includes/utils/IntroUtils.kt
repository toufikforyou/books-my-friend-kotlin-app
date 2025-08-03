package com.sopnolikhi.booksmyfriend.services.includes.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

object IntroUtils {

    private const val INTRO_PREFERENCES = "IntroLayout"
    private const val KEY_INTRO_OPENED = "IntroOpened"

    fun clearApplicationNone(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(INTRO_PREFERENCES, MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_INTRO_OPENED, false)
    }

    fun savePreviousData(context: Context) {
        context.getSharedPreferences(INTRO_PREFERENCES, MODE_PRIVATE).edit {
            putBoolean(KEY_INTRO_OPENED, true)
        }
    }
}
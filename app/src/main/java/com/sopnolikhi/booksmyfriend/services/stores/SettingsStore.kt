package com.sopnolikhi.booksmyfriend.services.stores

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

enum class ReaderTheme { PAPER, SEPIA, NIGHT }

/** App-wide preferences: appearance and reading comfort. */
object SettingsStore {

    private const val SETTINGS_PREFERENCES = "SettingsPref"
    private const val KEY_NIGHT_MODE = "NightMode"
    private const val KEY_DYNAMIC_COLOR = "DynamicColor"
    private const val KEY_READER_FONT_SIZE = "ReaderFontSize"
    private const val KEY_READER_THEME = "ReaderTheme"

    const val MIN_FONT_SIZE = 14f
    const val MAX_FONT_SIZE = 30f
    private const val DEFAULT_FONT_SIZE = 19f

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(SETTINGS_PREFERENCES, Context.MODE_PRIVATE)
    }

    var nightMode: Int
        get() = sharedPreferences.getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(value) {
            sharedPreferences.edit { putInt(KEY_NIGHT_MODE, value) }
            AppCompatDelegate.setDefaultNightMode(value)
        }

    var useDynamicColor: Boolean
        get() = sharedPreferences.getBoolean(KEY_DYNAMIC_COLOR, false)
        set(value) = sharedPreferences.edit { putBoolean(KEY_DYNAMIC_COLOR, value) }

    var readerFontSize: Float
        get() = sharedPreferences.getFloat(KEY_READER_FONT_SIZE, DEFAULT_FONT_SIZE)
        set(value) = sharedPreferences.edit {
            putFloat(KEY_READER_FONT_SIZE, value.coerceIn(MIN_FONT_SIZE, MAX_FONT_SIZE))
        }

    var readerTheme: ReaderTheme
        get() = ReaderTheme.entries.getOrElse(sharedPreferences.getInt(KEY_READER_THEME, 0)) { ReaderTheme.PAPER }
        set(value) = sharedPreferences.edit { putInt(KEY_READER_THEME, value.ordinal) }
}

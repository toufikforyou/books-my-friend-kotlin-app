package com.sopnolikhi.booksmyfriend

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.sopnolikhi.booksmyfriend.services.sessions.LoginSession
import com.sopnolikhi.booksmyfriend.services.stores.LibraryStore
import com.sopnolikhi.booksmyfriend.services.stores.SettingsStore

class SettingDynamic : Application() {
    override fun onCreate() {
        super.onCreate()
        LoginSession.initialize(this)
        LibraryStore.initialize(this)
        SettingsStore.initialize(this)

        AppCompatDelegate.setDefaultNightMode(SettingsStore.nightMode)

        /** Wallpaper colours (Android 12+) are opt-in so the warm reading palette stays the default. */
        DynamicColors.applyToActivitiesIfAvailable(
            this,
            DynamicColorsOptions.Builder()
                .setPrecondition { _, _ -> SettingsStore.useDynamicColor }
                .build()
        )
    }
}

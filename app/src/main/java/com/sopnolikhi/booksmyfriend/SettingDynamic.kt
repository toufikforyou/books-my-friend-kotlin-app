package com.sopnolikhi.booksmyfriend

import android.app.Application
import com.google.android.material.color.DynamicColors

class SettingDynamic : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
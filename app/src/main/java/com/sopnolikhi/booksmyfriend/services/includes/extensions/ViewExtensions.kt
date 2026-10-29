package com.sopnolikhi.booksmyfriend.services.includes.extensions

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Pads this view so its content stays clear of the status bar, navigation bar and display cutouts
 * while the background still draws edge to edge.
 */
fun View.applySystemBarPadding(top: Boolean = true, bottom: Boolean = true, horizontal: Boolean = true) {
    val initialLeft = paddingLeft
    val initialTop = paddingTop
    val initialRight = paddingRight
    val initialBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        view.updatePadding(
            left = initialLeft + if (horizontal) bars.left else 0,
            top = initialTop + if (top) bars.top else 0,
            right = initialRight + if (horizontal) bars.right else 0,
            bottom = initialBottom + if (bottom) maxOf(bars.bottom, ime.bottom) else 0,
        )
        insets
    }
}

/** First letter of a name, used for the friendly avatar circle. */
fun String.initial(): String = trim().firstOrNull()?.uppercase() ?: "📖"

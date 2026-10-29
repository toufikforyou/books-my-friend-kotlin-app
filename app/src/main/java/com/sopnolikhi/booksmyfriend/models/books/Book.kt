package com.sopnolikhi.booksmyfriend.models.books

import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.sopnolikhi.booksmyfriend.R

enum class BookCategory(@param:StringRes val label: Int) {
    CLASSICS(R.string.categoryClassicsText),
    ADVENTURE(R.string.categoryAdventureText),
    MYSTERY(R.string.categoryMysteryText),
    CHILDREN(R.string.categoryChildrenText),
    BANGLA(R.string.categoryBanglaText),
}

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val category: BookCategory,
    val description: String,
    val rating: Float,
    val readingMinutes: Int,
    val languageTag: String,
    @param:ColorInt val coverStart: Int,
    @param:ColorInt val coverEnd: Int,
) {
    val assetPath: String get() = "books/$id.txt"
    val isBangla: Boolean get() = languageTag == "bn"
}

package com.sopnolikhi.booksmyfriend.design.ui.custom

import android.view.LayoutInflater
import android.view.View
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.models.books.BookCategory

/** Fills [group] with an "All" chip plus one chip per category; null means "All". */
fun ChipGroup.setupCategoryChips(
    selected: BookCategory? = null,
    onSelected: (BookCategory?) -> Unit,
) {
    removeAllViews()
    val options = listOf<BookCategory?>(null) + BookCategory.entries
    options.forEach { category ->
        val chip = LayoutInflater.from(context).inflate(R.layout.view_category_chip, this, false) as Chip
        chip.id = View.generateViewId()
        chip.text = context.getString(category?.label ?: R.string.allCategoriesText)
        chip.tag = category
        chip.isChecked = category == selected
        addView(chip)
    }
    setOnCheckedStateChangeListener { group, checkedIds ->
        val chip = checkedIds.firstOrNull()?.let { group.findViewById<Chip>(it) }
        onSelected(chip?.tag as? BookCategory)
    }
}

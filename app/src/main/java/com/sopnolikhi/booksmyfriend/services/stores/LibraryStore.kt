package com.sopnolikhi.booksmyfriend.services.stores

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.sopnolikhi.booksmyfriend.models.books.Book

/** Remembers which books the reader saved and how far they got in each one. */
object LibraryStore {

    private const val LIBRARY_PREFERENCES = "LibraryPref"
    private const val KEY_SAVED = "SavedBooks"
    private const val KEY_PROGRESS_PREFIX = "Progress_"
    private const val KEY_OPENED_PREFIX = "Opened_"

    const val FINISHED_THRESHOLD = 0.98f

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(LIBRARY_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun isSaved(bookId: String): Boolean =
        sharedPreferences.getStringSet(KEY_SAVED, emptySet())!!.contains(bookId)

    /** Toggles the bookmark and returns the new state. */
    fun toggleSaved(bookId: String): Boolean {
        val saved = sharedPreferences.getStringSet(KEY_SAVED, emptySet())!!.toMutableSet()
        val nowSaved = if (saved.contains(bookId)) !saved.remove(bookId) else saved.add(bookId)
        sharedPreferences.edit { putStringSet(KEY_SAVED, saved) }
        return nowSaved
    }

    fun progress(bookId: String): Float = sharedPreferences.getFloat(KEY_PROGRESS_PREFIX + bookId, 0f)

    fun saveProgress(bookId: String, progress: Float) {
        sharedPreferences.edit {
            putFloat(KEY_PROGRESS_PREFIX + bookId, progress.coerceIn(0f, 1f))
            putLong(KEY_OPENED_PREFIX + bookId, System.currentTimeMillis())
        }
    }

    private fun lastOpened(bookId: String): Long = sharedPreferences.getLong(KEY_OPENED_PREFIX + bookId, 0L)

    fun savedBooks(): List<Book> = BookRepository.books.filter { isSaved(it.id) }

    /** Books started but not finished, most recently opened first. */
    fun inProgressBooks(): List<Book> = BookRepository.books
        .filter { lastOpened(it.id) > 0 && progress(it.id) < FINISHED_THRESHOLD }
        .sortedByDescending { lastOpened(it.id) }

    fun finishedCount(): Int = BookRepository.books.count { progress(it.id) >= FINISHED_THRESHOLD }

    fun continueReading(): Book? = inProgressBooks().firstOrNull()

    fun clear() = sharedPreferences.edit { clear() }
}

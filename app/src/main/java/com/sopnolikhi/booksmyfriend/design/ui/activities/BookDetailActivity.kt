package com.sopnolikhi.booksmyfriend.design.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.databinding.ActivityBookDetailBinding
import com.sopnolikhi.booksmyfriend.models.books.Book
import com.sopnolikhi.booksmyfriend.services.includes.extensions.applySystemBarPadding
import com.sopnolikhi.booksmyfriend.services.stores.BookRepository
import com.sopnolikhi.booksmyfriend.services.stores.LibraryStore
import kotlin.math.roundToInt

class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var book: Book

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        book = BookRepository.find(intent.getStringExtra(EXTRA_BOOK_ID)) ?: run {
            finish()
            return
        }
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.detailRoot.applySystemBarPadding()

        binding.detailToolbar.setNavigationOnClickListener { finish() }
        binding.detailCover.setBook(book)
        binding.detailTitle.text = book.title
        binding.detailAuthor.text = book.author
        binding.detailDescription.text = book.description
        binding.ratingChip.text = getString(R.string.ratingFormat, book.rating)
        binding.lengthChip.text = getString(R.string.minutesReadFormat, book.readingMinutes)
        binding.categoryChip.setText(book.category.label)

        binding.readButton.setOnClickListener { ReaderActivity.start(this, book.id) }
        binding.saveButton.setOnClickListener {
            val saved = LibraryStore.toggleSaved(book.id)
            showSaved(saved)
            Snackbar.make(
                binding.root,
                if (saved) R.string.savedToLibraryText else R.string.removedFromLibraryText,
                Snackbar.LENGTH_SHORT,
            ).setAnchorView(binding.readButton).show()
        }
    }

    override fun onResume() {
        super.onResume()
        showSaved(LibraryStore.isSaved(book.id))
        val progress = LibraryStore.progress(book.id)
        val percent = (progress * 100).roundToInt()
        binding.readButton.text = when {
            progress >= LibraryStore.FINISHED_THRESHOLD -> getString(R.string.readAgainText)
            percent > 0 -> getString(R.string.continueReadingFormat, percent)
            else -> getString(R.string.startReadingButton)
        }
    }

    private fun showSaved(saved: Boolean) {
        binding.saveButton.setIconResource(if (saved) R.drawable.ic_bookmark else R.drawable.ic_bookmark_border)
        binding.saveButton.contentDescription = getString(if (saved) R.string.removeBookText else R.string.saveBookText)
    }

    companion object {
        private const val EXTRA_BOOK_ID = "bookId"

        fun start(context: Context, bookId: String) {
            context.startActivity(Intent(context, BookDetailActivity::class.java).putExtra(EXTRA_BOOK_ID, bookId))
        }
    }
}

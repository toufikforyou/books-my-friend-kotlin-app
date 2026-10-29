package com.sopnolikhi.booksmyfriend.design.adapters.books

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.databinding.ItemBookCoverBinding
import com.sopnolikhi.booksmyfriend.databinding.ItemBookRowBinding
import com.sopnolikhi.booksmyfriend.models.books.Book
import kotlin.math.roundToInt

/** A book plus how far the reader got, or null when progress should not be shown. */
data class BookItem(val book: Book, val progress: Float? = null)

private object BookItemDiff : DiffUtil.ItemCallback<BookItem>() {
    override fun areItemsTheSame(oldItem: BookItem, newItem: BookItem) = oldItem.book.id == newItem.book.id
    override fun areContentsTheSame(oldItem: BookItem, newItem: BookItem) = oldItem == newItem
}

/** Cover-first cards for shelves and grids. */
class BookCoverAdapter(
    private val fillWidth: Boolean = false,
    private val onClick: (Book) -> Unit,
) : ListAdapter<BookItem, BookCoverAdapter.ViewHolder>(BookItemDiff) {

    class ViewHolder(val binding: ItemBookCoverBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookCoverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        if (fillWidth) binding.root.updateLayoutParams { width = ViewGroup.LayoutParams.MATCH_PARENT }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = getItem(position).book
        with(holder.binding) {
            bookCover.setBook(book)
            bookTitle.text = book.title
            bookAuthor.text = book.author
            root.contentDescription = root.context.getString(R.string.bookCoverDescription, book.title, book.author)
            root.setOnClickListener { onClick(book) }
        }
    }
}

/** Detailed rows with rating, length and optional reading progress. */
class BookRowAdapter(
    private val onClick: (Book) -> Unit,
) : ListAdapter<BookItem, BookRowAdapter.ViewHolder>(BookItemDiff) {

    class ViewHolder(val binding: ItemBookRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemBookRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val book = item.book
        with(holder.binding) {
            val context = root.context
            bookCover.setBook(book)
            bookTitle.text = book.title
            bookAuthor.text = book.author
            bookMeta.text = listOf(
                context.getString(R.string.ratingFormat, book.rating),
                context.getString(book.category.label),
                context.getString(R.string.minutesReadFormat, book.readingMinutes),
            ).joinToString(" · ")

            val percent = item.progress?.let { (it * 100).roundToInt() }
            bookProgress.isVisible = percent != null
            bookProgressText.isVisible = percent != null
            if (percent != null) {
                bookProgress.progress = percent
                bookProgressText.text = if (percent == 0) {
                    context.getString(R.string.justStartedText)
                } else {
                    context.getString(R.string.percentReadFormat, percent)
                }
            }
            root.setOnClickListener { onClick(book) }
        }
    }
}

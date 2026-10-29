package com.sopnolikhi.booksmyfriend.design.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.databinding.FragmentLibraryBinding
import com.sopnolikhi.booksmyfriend.design.adapters.books.BookItem
import com.sopnolikhi.booksmyfriend.design.adapters.books.BookRowAdapter
import com.sopnolikhi.booksmyfriend.design.ui.activities.BookDetailActivity
import com.sopnolikhi.booksmyfriend.design.ui.activities.MainActivity
import com.sopnolikhi.booksmyfriend.design.ui.activities.ReaderActivity
import com.sopnolikhi.booksmyfriend.design.ui.activities.ScrollableTab
import com.sopnolikhi.booksmyfriend.services.includes.extensions.applySystemBarPadding
import com.sopnolikhi.booksmyfriend.services.stores.LibraryStore

class LibraryFragment : Fragment(), ScrollableTab {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val readingAdapter = BookRowAdapter { ReaderActivity.start(requireContext(), it.id) }
    private val savedAdapter = BookRowAdapter { BookDetailActivity.start(requireContext(), it.id) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.libraryRoot.applySystemBarPadding(bottom = false)
        binding.libraryToggle.addOnButtonCheckedListener { _, _, isChecked -> if (isChecked) refresh() }
        binding.libraryEmpty.emptyAction.isVisible = true
        binding.libraryEmpty.emptyAction.setOnClickListener {
            (activity as? MainActivity)?.openTab(R.id.navigationExplore)
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) refresh()
    }

    private fun refresh() {
        val binding = _binding ?: return
        val showReading = binding.libraryToggle.checkedButtonId == R.id.readingTabButton
        val items = if (showReading) {
            LibraryStore.inProgressBooks().map { BookItem(it, LibraryStore.progress(it.id)) }
        } else {
            LibraryStore.savedBooks().map { BookItem(it) }
        }

        binding.libraryList.adapter = if (showReading) readingAdapter else savedAdapter
        (if (showReading) readingAdapter else savedAdapter).submitList(items)

        binding.libraryList.isVisible = items.isNotEmpty()
        binding.libraryEmpty.root.isVisible = items.isEmpty()
        binding.libraryEmpty.emptyEmoji.text = if (showReading) "📖" else "🔖"
        binding.libraryEmpty.emptyTitle.setText(if (showReading) R.string.emptyReadingTitle else R.string.emptySavedTitle)
        binding.libraryEmpty.emptyBody.setText(if (showReading) R.string.emptyReadingBody else R.string.emptySavedBody)
    }

    override fun scrollToTop() {
        _binding?.libraryList?.smoothScrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

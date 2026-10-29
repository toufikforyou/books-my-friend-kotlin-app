package com.sopnolikhi.booksmyfriend.design.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.databinding.FragmentExploreBinding
import com.sopnolikhi.booksmyfriend.design.adapters.books.BookCoverAdapter
import com.sopnolikhi.booksmyfriend.design.adapters.books.BookItem
import com.sopnolikhi.booksmyfriend.design.ui.activities.BookDetailActivity
import com.sopnolikhi.booksmyfriend.design.ui.activities.ScrollableTab
import com.sopnolikhi.booksmyfriend.design.ui.custom.setupCategoryChips
import com.sopnolikhi.booksmyfriend.models.books.BookCategory
import com.sopnolikhi.booksmyfriend.services.includes.extensions.applySystemBarPadding
import com.sopnolikhi.booksmyfriend.services.stores.BookRepository

class ExploreFragment : Fragment(), ScrollableTab {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private var selectedCategory: BookCategory? = null
    private val adapter = BookCoverAdapter(fillWidth = true) { BookDetailActivity.start(requireContext(), it.id) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.exploreRoot.applySystemBarPadding(bottom = false)

        val columnWidthDp = 120
        val screenWidthDp = resources.configuration.screenWidthDp
        binding.exploreGrid.layoutManager = GridLayoutManager(requireContext(), (screenWidthDp / columnWidthDp).coerceIn(2, 6))
        binding.exploreGrid.adapter = adapter

        binding.exploreEmpty.emptyEmoji.text = "🔍"
        binding.exploreEmpty.emptyTitle.setText(R.string.noResultsTitle)
        binding.exploreEmpty.emptyBody.setText(R.string.noResultsBody)

        binding.searchInput.doAfterTextChanged { showResults() }
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) hideKeyboard()
            actionId == EditorInfo.IME_ACTION_SEARCH
        }
        binding.exploreCategoryChips.setupCategoryChips(selectedCategory) {
            selectedCategory = it
            showResults()
        }
        showResults()
    }

    private fun showResults() {
        val binding = _binding ?: return
        val results = BookRepository.search(binding.searchInput.text?.toString().orEmpty(), selectedCategory)
        adapter.submitList(results.map { BookItem(it) })
        binding.exploreEmpty.root.isVisible = results.isEmpty()
        binding.exploreGrid.isVisible = results.isNotEmpty()
    }

    fun focusSearch() {
        val input = _binding?.searchInput ?: return
        input.post {
            input.requestFocus()
            requireContext().getSystemService<InputMethodManager>()?.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboard() {
        val input = _binding?.searchInput ?: return
        requireContext().getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(input.windowToken, 0)
        input.clearFocus()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) hideKeyboard()
    }

    override fun scrollToTop() {
        _binding?.exploreGrid?.smoothScrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.sopnolikhi.booksmyfriend.design.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.databinding.FragmentHomeBinding
import com.sopnolikhi.booksmyfriend.design.adapters.books.BookCoverAdapter
import com.sopnolikhi.booksmyfriend.design.adapters.books.BookItem
import com.sopnolikhi.booksmyfriend.design.adapters.books.BookRowAdapter
import com.sopnolikhi.booksmyfriend.design.ui.activities.BookDetailActivity
import com.sopnolikhi.booksmyfriend.design.ui.activities.MainActivity
import com.sopnolikhi.booksmyfriend.design.ui.activities.ReaderActivity
import com.sopnolikhi.booksmyfriend.design.ui.activities.ScrollableTab
import com.sopnolikhi.booksmyfriend.design.ui.custom.setupCategoryChips
import com.sopnolikhi.booksmyfriend.models.books.BookCategory
import com.sopnolikhi.booksmyfriend.services.includes.extensions.applySystemBarPadding
import com.sopnolikhi.booksmyfriend.services.includes.extensions.initial
import com.sopnolikhi.booksmyfriend.services.sessions.LoginSession
import com.sopnolikhi.booksmyfriend.services.stores.BookRepository
import com.sopnolikhi.booksmyfriend.services.stores.LibraryStore
import java.util.Calendar
import kotlin.math.roundToInt

class HomeFragment : Fragment(), ScrollableTab {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var selectedCategory: BookCategory? = null

    private val popularAdapter = BookCoverAdapter { BookDetailActivity.start(requireContext(), it.id) }
    private val recommendedAdapter = BookRowAdapter { BookDetailActivity.start(requireContext(), it.id) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.homeScroll.applySystemBarPadding(bottom = false)

        binding.popularHeader.root.setText(R.string.popularThisWeekText)
        binding.recommendedHeader.root.setText(R.string.recommendedText)
        binding.popularList.adapter = popularAdapter
        binding.recommendedList.adapter = recommendedAdapter

        binding.searchCard.setOnClickListener { (activity as? MainActivity)?.openSearch() }
        binding.avatarText.setOnClickListener { (activity as? MainActivity)?.openTab(R.id.navigationProfile) }

        binding.homeCategoryChips.setupCategoryChips(selectedCategory) {
            selectedCategory = it
            showRecommended()
        }

        popularAdapter.submitList(BookRepository.books.sortedByDescending { it.rating }.map { BookItem(it) })
        showRecommended()
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
        val name = LoginSession.getUserName().ifBlank { getString(R.string.defaultReaderName) }
        binding.greetingText.text = getString(greetingFor(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)))
        binding.nameText.text = name
        binding.avatarText.text = name.initial()

        val current = LibraryStore.continueReading()
        binding.continueCard.isVisible = current != null
        if (current != null) {
            val percent = (LibraryStore.progress(current.id) * 100).roundToInt()
            binding.continueCover.setBook(current)
            binding.continueTitle.text = current.title
            binding.continueAuthor.text = current.author
            binding.continueProgress.progress = percent
            binding.continuePercent.text = getString(R.string.percentReadFormat, percent)
            val open = View.OnClickListener { ReaderActivity.start(requireContext(), current.id) }
            binding.continueCard.setOnClickListener(open)
            binding.continueButton.setOnClickListener(open)
        }
    }

    private fun showRecommended() {
        recommendedAdapter.submitList(BookRepository.byCategory(selectedCategory).map { BookItem(it) })
    }

    override fun scrollToTop() {
        _binding?.homeScroll?.smoothScrollTo(0, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun greetingFor(hour: Int): Int = when (hour) {
        in 5..11 -> R.string.greetingMorning
        in 12..16 -> R.string.greetingAfternoon
        in 17..21 -> R.string.greetingEvening
        else -> R.string.greetingNight
    }
}

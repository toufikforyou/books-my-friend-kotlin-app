package com.sopnolikhi.booksmyfriend.design.ui.activities

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.MenuItemCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.databinding.ActivityReaderBinding
import com.sopnolikhi.booksmyfriend.databinding.SheetReaderSettingsBinding
import com.sopnolikhi.booksmyfriend.models.books.Book
import com.sopnolikhi.booksmyfriend.services.stores.BookRepository
import com.sopnolikhi.booksmyfriend.services.stores.LibraryStore
import com.sopnolikhi.booksmyfriend.services.stores.ReaderTheme
import com.sopnolikhi.booksmyfriend.services.stores.SettingsStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * Distraction-free reading: tap the page to hide or show the controls, adjust text size and
 * page colour, and the reader's place is saved automatically.
 */
class ReaderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReaderBinding
    private lateinit var book: Book
    private lateinit var insetsController: WindowInsetsControllerCompat

    private var chromeVisible = true
    private var contentLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        book = BookRepository.find(intent.getStringExtra(EXTRA_BOOK_ID)) ?: run {
            finish()
            return
        }
        binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        insetsController = WindowCompat.getInsetsController(window, binding.root)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        binding.readerToolbar.title = book.title
        binding.readerToolbar.setNavigationOnClickListener { finish() }
        binding.readerToolbar.setOnMenuItemClickListener {
            showSettings()
            true
        }

        val typeface = if (book.isBangla) ResourcesCompat.getFont(this, R.font.hind_regular) else Typeface.SERIF
        binding.readerText.typeface = typeface
        binding.readerText.setOnClickListener { toggleChrome() }
        binding.readerChapter.setOnClickListener { toggleChrome() }

        applyInsets()
        applyFontSize()
        applyReaderTheme()

        binding.readerScroll.setOnScrollChangeListener { _, _, _, _, _ -> updateProgress() }
        loadText()
    }

    private fun applyInsets() {
        val toolbarHeight = TypedValue().let {
            theme.resolveAttribute(androidx.appcompat.R.attr.actionBarSize, it, true)
            TypedValue.complexToDimensionPixelSize(it.data, resources.displayMetrics)
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.readerRoot) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            binding.readerToolbar.updatePadding(top = bars.top, left = bars.left, right = bars.right)
            binding.readerBottomBar.updatePadding(bottom = bars.bottom + bottomBarPadding())
            binding.readerScroll.updatePadding(
                left = bars.left,
                right = bars.right,
                top = bars.top + toolbarHeight,
                bottom = bars.bottom + toolbarHeight,
            )
            insets
        }
    }

    private fun bottomBarPadding() = resources.getDimensionPixelSize(R.dimen.positive_012dp)

    private fun loadText() {
        lifecycleScope.launch {
            val text = withContext(Dispatchers.IO) { BookRepository.loadText(this@ReaderActivity, book) }
            val chapter = text.substringBefore('\n').trim()
            binding.readerChapter.text = chapter
            binding.readerText.text = text.substringAfter('\n').trim()
            contentLoaded = true
            restorePosition()
        }
    }

    private fun restorePosition() {
        val saved = LibraryStore.progress(book.id)
        binding.readerScroll.post {
            if (saved > 0f && saved < LibraryStore.FINISHED_THRESHOLD) {
                binding.readerScroll.scrollTo(0, (scrollRange() * saved).roundToInt())
            }
            updateProgress()
        }
    }

    private fun scrollRange(): Int {
        val content = binding.readerScroll.getChildAt(0) ?: return 0
        val scroll = binding.readerScroll
        return (content.height + scroll.paddingTop + scroll.paddingBottom - scroll.height).coerceAtLeast(0)
    }

    private fun currentProgress(): Float {
        val range = scrollRange()
        return if (range == 0) 1f else (binding.readerScroll.scrollY.toFloat() / range).coerceIn(0f, 1f)
    }

    private fun updateProgress() {
        val percent = (currentProgress() * 100).roundToInt()
        binding.readerProgress.progress = percent
        binding.readerPercent.text = getString(R.string.percentReadFormat, percent)
    }

    private fun toggleChrome() {
        chromeVisible = !chromeVisible
        listOf(binding.readerToolbar, binding.readerBottomBar).forEach { bar ->
            bar.animate().alpha(if (chromeVisible) 1f else 0f).withEndAction {
                bar.visibility = if (chromeVisible) View.VISIBLE else View.INVISIBLE
            }.withStartAction { bar.visibility = View.VISIBLE }.start()
        }
        if (chromeVisible) {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        } else {
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun applyFontSize() {
        val size = SettingsStore.readerFontSize
        binding.readerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        binding.readerChapter.setTextSize(TypedValue.COMPLEX_UNIT_SP, size * 1.35f)
    }

    private fun applyReaderTheme() {
        val (background, text) = when (SettingsStore.readerTheme) {
            ReaderTheme.PAPER -> R.color.reader_paper_background to R.color.reader_paper_text
            ReaderTheme.SEPIA -> R.color.reader_sepia_background to R.color.reader_sepia_text
            ReaderTheme.NIGHT -> R.color.reader_night_background to R.color.reader_night_text
        }
        val backgroundColor = ContextCompat.getColor(this, background)
        val textColor = ContextCompat.getColor(this, text)
        val mutedColor = ColorUtils.setAlphaComponent(textColor, 0xB3)
        val barColor = ColorUtils.setAlphaComponent(backgroundColor, 0xF2)

        binding.readerRoot.setBackgroundColor(backgroundColor)
        binding.readerToolbar.setBackgroundColor(barColor)
        binding.readerBottomBar.setBackgroundColor(barColor)
        binding.readerText.setTextColor(textColor)
        binding.readerChapter.setTextColor(textColor)
        binding.readerEnd.setTextColor(mutedColor)
        binding.readerPercent.setTextColor(mutedColor)
        binding.readerToolbar.setTitleTextColor(textColor)
        binding.readerToolbar.setNavigationIconTint(textColor)
        binding.readerToolbar.menu.findItem(R.id.readerSettings)?.let {
            MenuItemCompat.setIconTintList(it, ColorStateList.valueOf(textColor))
        }
        binding.readerProgress.setIndicatorColor(textColor)
        binding.readerProgress.trackColor = ColorUtils.setAlphaComponent(textColor, 0x33)

        val lightPage = ColorUtils.calculateLuminance(backgroundColor) > 0.5
        insetsController.isAppearanceLightStatusBars = lightPage
        insetsController.isAppearanceLightNavigationBars = lightPage
    }

    private fun showSettings() {
        val dialog = BottomSheetDialog(this)
        val sheet = SheetReaderSettingsBinding.inflate(layoutInflater)
        dialog.setContentView(sheet.root)

        sheet.fontSizeSlider.value = SettingsStore.readerFontSize.coerceIn(
            SettingsStore.MIN_FONT_SIZE, SettingsStore.MAX_FONT_SIZE
        )
        fun setFontSize(size: Float) {
            val progress = currentProgress()
            SettingsStore.readerFontSize = size
            sheet.fontSizeSlider.value = SettingsStore.readerFontSize
            applyFontSize()
            // Keep the reader on the same passage after the text reflows.
            binding.readerScroll.post { binding.readerScroll.scrollTo(0, (scrollRange() * progress).roundToInt()) }
        }
        sheet.fontSizeSlider.addOnChangeListener { _, value, fromUser -> if (fromUser) setFontSize(value) }
        sheet.fontSmallerButton.setOnClickListener { setFontSize(SettingsStore.readerFontSize - 1) }
        sheet.fontLargerButton.setOnClickListener { setFontSize(SettingsStore.readerFontSize + 1) }

        val buttonForTheme = mapOf(
            ReaderTheme.PAPER to R.id.pagePaperButton,
            ReaderTheme.SEPIA to R.id.pageSepiaButton,
            ReaderTheme.NIGHT to R.id.pageNightButton,
        )
        sheet.pageThemeToggle.check(buttonForTheme.getValue(SettingsStore.readerTheme))
        sheet.pageThemeToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            SettingsStore.readerTheme = buttonForTheme.entries.first { it.value == checkedId }.key
            applyReaderTheme()
        }
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        if (contentLoaded) LibraryStore.saveProgress(book.id, currentProgress())
    }

    companion object {
        private const val EXTRA_BOOK_ID = "bookId"

        fun start(context: Context, bookId: String) {
            context.startActivity(Intent(context, ReaderActivity::class.java).putExtra(EXTRA_BOOK_ID, bookId))
        }
    }
}

package com.sopnolikhi.booksmyfriend.design.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.models.books.Book
import kotlin.math.min

/**
 * Draws a simple, typographic book cover from a book's colours, title and author,
 * so the shelf looks complete even offline. Height always follows a 2:3 book ratio.
 */
class BookCoverView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var book: Book? = null

    private val density = resources.displayMetrics.density
    private val coverRect = RectF()
    private val coverPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val spinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(46, 0, 0, 0) }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(140, 255, 255, 255) }
    private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = ResourcesCompat.getFont(context, R.font.anek_bangla_bold)
    }
    private val authorPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(220, 255, 255, 255)
        typeface = ResourcesCompat.getFont(context, R.font.hind_regular)
    }

    fun setBook(book: Book) {
        this.book = book
        contentDescription = context.getString(R.string.bookCoverDescription, book.title, book.author)
        updateShader()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, (width * 1.5f).toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        coverRect.set(0f, 0f, w.toFloat(), h.toFloat())
        updateShader()
    }

    private fun updateShader() {
        val b = book ?: return
        coverPaint.shader = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(), b.coverStart, b.coverEnd, Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        val b = book ?: return
        val radius = min(width, height) * 0.06f
        canvas.drawRoundRect(coverRect, radius, radius, coverPaint)

        // Spine shading on the left edge makes the rectangle read as a book.
        val spineWidth = width * 0.06f
        canvas.drawRect(radius / 2, 0f, spineWidth, height.toFloat(), spinePaint)

        val padding = width * 0.12f
        val textWidth = (width - padding - spineWidth).toInt().coerceAtLeast(1)
        val left = spineWidth + padding / 2

        titlePaint.textSize = (width * 0.13f).coerceAtLeast(9 * density)
        authorPaint.textSize = (width * 0.075f).coerceAtLeast(7 * density)

        val titleLayout = StaticLayout.Builder
            .obtain(b.title, 0, b.title.length, titlePaint, textWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 0.88f)
            .setMaxLines(4)
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()

        canvas.save()
        canvas.translate(left, height * 0.14f)
        titleLayout.draw(canvas)
        canvas.restore()

        val lineY = height * 0.14f + titleLayout.height + height * 0.05f
        canvas.drawRect(left, lineY, left + width * 0.22f, lineY + 2 * density, linePaint)

        val author = TextUtils.ellipsize(b.author, authorPaint, textWidth.toFloat(), TextUtils.TruncateAt.END)
        canvas.drawText(author, 0, author.length, left, height - padding, authorPaint)
    }
}

package com.sopnolikhi.booksmyfriend.design.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.sopnolikhi.booksmyfriend.R

class AnimationButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val loadingAnimationView: LottieAnimationView
    private val customLoadingButton: MaterialButton
    private var onClickListener: OnClickListener? = null

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.single_common_button_layout, this, true)

        loadingAnimationView = findViewById(R.id.loadingAnimationView)
        customLoadingButton = findViewById(R.id.customLoadingButton)

        customLoadingButton.setOnClickListener {
            if (customLoadingButton.isEnabled) {
                setLoading(true)
                onClickListener?.onClick(it)
            }
        }
    }

    fun setLoading(loading: Boolean) {
        customLoadingButton.isEnabled = !loading

        if (loading) {
            loadingAnimationView.visibility = VISIBLE
            loadingAnimationView.playAnimation()
        } else {
            loadingAnimationView.visibility = GONE
            loadingAnimationView.pauseAnimation()
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        onClickListener = listener
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        customLoadingButton.isEnabled = enabled
    }

    fun getText(): CharSequence {
        return customLoadingButton.text
    }

    fun setText(text: String) {
        customLoadingButton.text = text
    }
}

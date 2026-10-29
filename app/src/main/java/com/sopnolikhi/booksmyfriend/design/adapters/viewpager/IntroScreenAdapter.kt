package com.sopnolikhi.booksmyfriend.design.adapters.viewpager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sopnolikhi.booksmyfriend.databinding.SingleIntroScreenLayoutBinding
import com.sopnolikhi.booksmyfriend.models.viewpager.IntroScreenModel

class IntroScreenAdapter(
    private val introScreenModelList: List<IntroScreenModel>,
) : RecyclerView.Adapter<IntroScreenAdapter.IntroViewHolder>() {

    class IntroViewHolder(val binding: SingleIntroScreenLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder =
        IntroViewHolder(SingleIntroScreenLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
        val item = introScreenModelList[position]
        with(holder.binding) {
            introImage.text = item.emoji
            introTitleText.setText(item.title)
            introDescription.setText(item.description)
        }
    }

    override fun getItemCount(): Int = introScreenModelList.size
}

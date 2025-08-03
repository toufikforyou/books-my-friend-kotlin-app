package com.sopnolikhi.booksmyfriend.design.adapters.viewpager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.models.viewpager.IntroScreenModel

class IntroScreenAdapter(
    private val context: Context, private val introScreenModelList: List<IntroScreenModel>,
) : RecyclerView.Adapter<IntroScreenAdapter.IntroViewHolder>() {

    inner class IntroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sliderImage: ImageView = itemView.findViewById(R.id.introImage)
        val title: TextView = itemView.findViewById(R.id.introTitleText)
        val description: TextView = itemView.findViewById(R.id.introDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.single_intro_screen_layout, parent, false)
        return IntroViewHolder(view)
    }

    override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
        with(holder) {
            Glide.with(context).load(introScreenModelList[position].imageUrl).into(sliderImage)
            title.text = introScreenModelList[position].title
            description.text = introScreenModelList[position].description
        }
    }

    override fun getItemCount(): Int = introScreenModelList.size
}
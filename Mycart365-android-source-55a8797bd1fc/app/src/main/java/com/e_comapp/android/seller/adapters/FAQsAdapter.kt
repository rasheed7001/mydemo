package com.e_comapp.android.seller.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.seller.models.FAQ
import com.e_comapp.android.views.CustomTextView
import java.util.*

class FAQsAdapter(var context: Context?, var list: ArrayList<FAQ>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.faq_item, parent, false)
        return FAQViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FAQViewHolder) {
            holder.bind(list[position], position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    internal inner class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textQuestion: CustomTextView
        var textDescription: CustomTextView
        var layoutDescription: LinearLayout
        var imgExpand: ImageView
        fun bind(faq: FAQ, position: Int) {
            textQuestion.text = faq.question
            if (faq.expanded) {
                imgExpand.setImageResource(R.drawable.up_arrow_show)
                layoutDescription.visibility = View.VISIBLE
                textDescription.text = faq.answer
            } else {
                layoutDescription.visibility = View.GONE
            }
            imgExpand.setOnClickListener {
                faq.expanded = !faq.expanded
                notifyItemChanged(position)
            }
        }

        init {
            textQuestion = itemView.findViewById(R.id.textQuestion)
            textDescription = itemView.findViewById(R.id.textDescription)
            layoutDescription = itemView.findViewById(R.id.layoutDescription)
            imgExpand = itemView.findViewById(R.id.imageExpand)
        }
    }

}
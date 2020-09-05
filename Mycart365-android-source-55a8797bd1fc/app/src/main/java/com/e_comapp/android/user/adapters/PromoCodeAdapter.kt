package com.e_comapp.android.user.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.user.models.PromoCodeListParser.PromoCodeList
import com.e_comapp.android.views.CustomTextView
import java.util.*

class PromoCodeAdapter(var promoCodeList: ArrayList<PromoCodeList?>?, var context: Context, var listener: OnPromoCodeClickedListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_promo_code_item, parent, false)
        return PromoCodeHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PromoCodeHolder) {
            holder.bind(promoCodeList!![position])
        }
    }

    override fun getItemCount(): Int {
        return promoCodeList!!.size
    }

    fun updateList(promoCodeList: ArrayList<PromoCodeList?>?) {
        this.promoCodeList = promoCodeList
        notifyDataSetChanged()
    }

    internal inner class PromoCodeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textTitle: CustomTextView
        var textDesc: CustomTextView
        var textValidityType: CustomTextView
        fun bind(promoCode: PromoCodeList?) {
            textTitle.text = ""
            itemView.setOnClickListener { listener.onPromoCodeClicked(promoCode) }
        }

        init {
            textTitle = itemView.findViewById(R.id.textTitle)
            textDesc = itemView.findViewById(R.id.textDescription)
            textValidityType = itemView.findViewById(R.id.textValidityType)
        }
    }

    interface OnPromoCodeClickedListener {
        fun onPromoCodeClicked(promoCode: PromoCodeList?)
    }

}
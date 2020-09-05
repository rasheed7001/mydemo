package com.e_comapp.android.user.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.seller.models.Industry
import com.e_comapp.android.util.component.MyCartImageView

class IndustryListAdapter(private var listener: OnIndustryListClickedListener) : RecyclerView.Adapter<IndustryListAdapter.MyHolder>() {

    private var industryList = emptyList<Industry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val itemVeiw = LayoutInflater.from(parent.context).inflate(R.layout.view_industry_item, parent, false)
        return MyHolder(itemVeiw)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.txtIndustry.text = industryList[position].industry
        holder.imgIndustry.loadImageWithUrl(industryList[position].imgUrl)
        holder.rlView.setBackgroundColor(Color.parseColor(industryList[position].colorCode))
        holder.itemView.setOnClickListener { listener.onIndustryClicked(industryList[position]) }
    }

    override fun getItemCount(): Int {
        return industryList.size
    }

    fun setData(list: List<Industry>) {
        industryList = list
        notifyDataSetChanged()
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgIndustry: MyCartImageView
        var txtIndustry: TextView
        var rlView: RelativeLayout

        init {
            rlView = itemView.findViewById(R.id.rlIndustryView)
            txtIndustry = itemView.findViewById(R.id.txtIndustry)
            imgIndustry = itemView.findViewById(R.id.imgIndustry)
        }
    }

    interface OnIndustryListClickedListener {
        fun onIndustryClicked(industry: Industry?)
    }

}
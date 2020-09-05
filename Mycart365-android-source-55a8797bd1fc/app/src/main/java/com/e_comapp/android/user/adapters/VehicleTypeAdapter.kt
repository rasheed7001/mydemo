package com.e_comapp.android.user.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.views.CustomTextView
import com.facebook.drawee.view.SimpleDraweeView


class VehicleTypeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_recycler_item, parent, false)
        return VehicleHodler(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VehicleHodler) {
            holder.bindView()
        }
    }

    override fun getItemCount(): Int {
        return 12
    }

    inner class VehicleHodler(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textType: CustomTextView
        var imageView: SimpleDraweeView
        fun bindView() {}

        init {
            textType = itemView.findViewById(R.id.textType)
            imageView = itemView.findViewById(R.id.ivType)
        }
    }
}
package com.e_comapp.android.seller.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R

class AddedItemsAdapters(var bg: Int, var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemVeiw = LayoutInflater.from(parent.context).inflate(R.layout.added_recycler_item, parent, false)
        return AddedItemViewHolder(itemVeiw, bg, context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
    override fun getItemCount(): Int {
        return 4
    }

    inner class AddedItemViewHolder(itemView: View, bg: Int, context: Context?) : RecyclerView.ViewHolder(itemView) {
        var root: ConstraintLayout

        init {
            root = itemView.findViewById(R.id.rootView)
            root.setBackgroundColor(ContextCompat.getColor(context!!, R.color.app_bg_blue))
        }
    }

}
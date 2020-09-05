package com.e_comapp.android.seller.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.views.CustomTextView

class NotificationRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemVeiw = LayoutInflater.from(parent.context).inflate(R.layout.notificaiton_recycler_item, parent, false)
        return NotificationHolder(itemVeiw)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NotificationHolder) {
        }
    }

    override fun getItemCount(): Int {
        return 8
    }

    inner class NotificationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageIcon: ImageView
        var textMessage: CustomTextView

        init {
            imageIcon = itemView.findViewById(R.id.imageIcon)
            textMessage = itemView.findViewById(R.id.textMessage)
        }
    }
}
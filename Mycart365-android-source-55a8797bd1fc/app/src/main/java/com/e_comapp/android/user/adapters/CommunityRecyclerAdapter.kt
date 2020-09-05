package com.e_comapp.android.user.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R


class CommunityRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemVeiw: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_recycelr_item, parent, false)
        return CommunityViewHolder(itemVeiw)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
    override fun getItemCount(): Int {
        return 3
    }

    inner class CommunityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
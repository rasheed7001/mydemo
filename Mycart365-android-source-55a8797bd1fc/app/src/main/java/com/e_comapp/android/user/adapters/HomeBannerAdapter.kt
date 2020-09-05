package com.e_comapp.android.user.adapters

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.e_comapp.android.databinding.BannerViewBinding

class HomeBannerAdapter(context: Context?) : RecyclerView.Adapter<HomeBannerAdapter.ViewHolder?>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    private val colorArray = intArrayOf(R.color.black, R.color.holo_blue_dark, R.color.holo_green_dark, R.color.holo_red_dark)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = BannerViewBinding.inflate(mInflater, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: HomeBannerAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class ViewHolder(binding: BannerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        fun bind(position: Int) {
//            binding.clParent.setBackgroundResource(colorArray[position])
        }
    }

    override fun getItemCount(): Int = colorArray.size


}
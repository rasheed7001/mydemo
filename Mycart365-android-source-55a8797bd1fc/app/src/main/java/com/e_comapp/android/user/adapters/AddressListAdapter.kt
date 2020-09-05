package com.e_comapp.android.user.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.user.adapters.AddressListAdapter.MyViewHolder
import com.e_comapp.android.user.models.AddressListParser.DeliveryAddressList
import com.e_comapp.android.views.CustomTextView

class AddressListAdapter(context: Context?, addressList: List<DeliveryAddressList?>?) : RecyclerView.Adapter<MyViewHolder>() {
    private val context: Context?
    private var addressList: MutableList<DeliveryAddressList?>?

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textAddress: CustomTextView
        var textType: CustomTextView
        var imgType: ImageView
        var viewBackground: RelativeLayout
        var viewForeground: RelativeLayout

        init {
            textType = view.findViewById(R.id.textType)
            textAddress = view.findViewById(R.id.textAddress)
            imgType = view.findViewById(R.id.imgIcon)
            viewBackground = view.findViewById(R.id.view_background)
            viewForeground = view.findViewById(R.id.view_foreground)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.address_recycler_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = addressList!![position]
        holder.textType.text = item?.addressType
        holder.textAddress.text = item?.fullAddress
        holder.imgType.setImageResource(R.drawable.home_colored)
    }

    override fun getItemCount(): Int {
        return addressList!!.size
    }

    fun updateList(list: ArrayList<DeliveryAddressList?>?) {
        addressList = list
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        addressList?.removeAt(position)
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position)
    }

    fun restoreItem(item: DeliveryAddressList?, position: Int) {
        addressList?.add(position, item)
        // notify item added by position
        notifyItemInserted(position)
    }

    init {
        this.context = context
        this.addressList = addressList as MutableList<DeliveryAddressList?>?
    }
}
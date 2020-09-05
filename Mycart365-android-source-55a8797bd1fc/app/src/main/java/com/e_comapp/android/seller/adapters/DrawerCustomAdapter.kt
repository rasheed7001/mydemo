package com.e_comapp.android.seller.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.e_comapp.android.R
import com.e_comapp.android.seller.models.LeftMenuModel
import java.util.*

class DrawerCustomAdapter(var mContext: Context, var layoutResourceId: Int, data: ArrayList<LeftMenuModel?>?) : ArrayAdapter<LeftMenuModel?>(mContext, layoutResourceId, data) {
    var data: ArrayList<LeftMenuModel?>? = null
    var selected_menu = 0
    override fun getItem(position: Int): LeftMenuModel? {
        return super.getItem(position)
    }

    fun updateSelectedMenu(value: Int) {
        selected_menu = value
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        val inflater = (mContext as Activity).layoutInflater
        listItem = inflater.inflate(layoutResourceId, parent, false)
        val innerRootlayout = listItem.findViewById<View>(R.id.inner_layout) as RelativeLayout
        val imageViewIcon = listItem.findViewById<View>(R.id.imageViewIcon) as ImageView
        val textViewName = listItem.findViewById<View>(R.id.txt_item) as TextView
        val ivArrrow = listItem.findViewById<View>(R.id.iv_right_arrow) as ImageView
        val folder = data!![position]
        if (folder!!.icon == 0) {
            imageViewIcon.visibility = View.GONE
        } else {
            imageViewIcon.visibility = View.VISIBLE
            imageViewIcon.setImageResource(folder.icon)
        }
        textViewName.text = folder.name

        if (folder.name.equals("Faqs", true) || folder.name.equals("Customer Support", true) || folder.name.equals("logout", true)) {
            ivArrrow.visibility = View.GONE
        } else {
            ivArrrow.visibility = View.VISIBLE
        }

        return listItem
    }

    init {
        selected_menu = 0
        this.data = data
    }
}
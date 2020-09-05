package com.e_comapp.android.user.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.e_comapp.android.R
import com.e_comapp.android.user.models.DropDown
import java.util.*

class AddViewMoreAdapter // TODO Auto-generated constructor stub
(context: Context?, textViewResourceId: Int, protected var spinnerList: MutableList<DropDown?>?) : ArrayAdapter<DropDown?>(context, textViewResourceId, spinnerList) {
    override fun getDropDownView(position: Int, convertView: View,
                                 parent: ViewGroup): View {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent)
    }

    fun updateList(list: ArrayList<DropDown>?) {
        spinnerList = list?.toMutableList()
        notifyDataSetChanged()
    }

    fun getCustomView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);
        val inflater = LayoutInflater.from(context)
        val row = inflater.inflate(R.layout.layout_add_viewmore_details_spinner, parent, false)
        val txtName = row.findViewById<View>(R.id.txt_name) as TextView
        txtName.text = (spinnerList?.get(position)?.text ?: if (spinnerList?.get(position)?.isSelected!!) {
            txtName.isSelected = true
        } else {
            txtName.isSelected = false
        }) as CharSequence?
        return row
    }

}
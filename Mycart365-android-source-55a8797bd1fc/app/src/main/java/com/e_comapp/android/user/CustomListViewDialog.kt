package com.e_comapp.android.user

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.views.CustomTextView

class CustomListViewDialog : Dialog {
    constructor(context: Context?, themeResId: Int) : super(context, themeResId) {}
    constructor(context: Context?, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener) {}

    var activity: Activity? = null
    var dialog: Dialog? = null
    var yes: Button? = null
    var no: Button? = null
    var textProductName: CustomTextView? = null
    var recyclerView: RecyclerView? = null
    var productName: String? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    var adapter: RecyclerView.Adapter<*>? = null

    constructor(a: Activity?, adapter: RecyclerView.Adapter<*>?, productName: String?) : super(a) {
        activity = a
        this.adapter = adapter
        this.productName = productName
        setupLayout()
    }

    private fun setupLayout() {}
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_unit_list)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        textProductName = findViewById(R.id.textProductName)
        textProductName?.text = productName
        recyclerView = findViewById(R.id.rv_unit_type)
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = mLayoutManager
        recyclerView?.adapter = adapter
    }
}
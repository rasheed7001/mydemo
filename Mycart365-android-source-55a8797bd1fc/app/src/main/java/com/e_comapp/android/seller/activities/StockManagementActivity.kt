package com.e_comapp.android.seller.activities

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.adapters.StockListingRecyclerAdapter
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.seller.listeners.OnStockClickedListener
import com.e_comapp.android.seller.models.ProductList
import com.e_comapp.android.seller.models.StockListParser
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.DeviceUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*

class StockManagementActivity : BaseActivity(), OnStockClickedListener {
    val TAG = javaClass.name
    var searchView: SearchView? = null
    var recyclerView: RecyclerView? = null
    var rootView: ConstraintLayout? = null
    var adapter: StockListingRecyclerAdapter? = null
    var productList: ArrayList<ProductList?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_management)
        init()
        setupDefaults()
        setupEvents()
    }

    private fun init() {
        productList = ArrayList()
        rootView = findViewById(R.id.cl_root)
        searchView = rootView?.findViewById(R.id.searchView)
        recyclerView = rootView?.findViewById(R.id.rv_stock)
        adapter = StockListingRecyclerAdapter(this, productList)
    }

    private fun setupDefaults() {
        setupCustToolbar()
        setMenuIcon(R.drawable.back)
        setCustTitle(getString(R.string.str_stock_management))
        recyclerView!!.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView!!.adapter = adapter
        callGetProductList(0)
    }

    override fun menuClicked() {
        super.menuClicked()
        onBackPressed()
    }

    private fun setupEvents() {}
    private fun constructJson(start: Int): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("start", start)
        return jsonObject
    }

    private fun callGetProductList(start: Int) {
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        app.retrofitInterface.getSellerProductList("S", constructJson(start)).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.hide()
                Log.e(TAG, content)
                val parser = Gson().fromJson(content, StockListParser::class.java)
                if (!parser.error!!) {
                    if (!parser.productList?.isEmpty()!!) {
                        adapter!!.updateList(parser.productList as ArrayList<ProductList?>)
                    }
                }
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(this@StockManagementActivity, message)
            }
        })
    }

    override fun onUpdateClicked() {}
    override fun onDeleteClicked(productId: Int) {}
}
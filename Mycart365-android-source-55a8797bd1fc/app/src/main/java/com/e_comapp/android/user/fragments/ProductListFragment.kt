package com.e_comapp.android.user.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseFragment
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.CustConstants
import com.e_comapp.android.user.activities.ProductListUserActivity
import com.e_comapp.android.user.adapters.ProductListRecyclerAdapter
import com.e_comapp.android.user.models.ProductListParser
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.utils.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [ProductListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductListFragment : BaseFragment(), ClickInterface {
    val TAG = javaClass.name
    var recyclerView: RecyclerView? = null
    var adapter: ProductListRecyclerAdapter? = null
    var productList: ArrayList<ProductListParser.ProductList?>? = ArrayList()
    var deliveryCharge = "30"
    var productListUserActivity: ProductListUserActivity? = null

    // TODO: Rename and change types of parameters
    private var sellerId: String? = null
    private var category: String? = null
    private var isFromConfirmOrder = false
    fun setActivity(activity: ProductListUserActivity?) {
        productListUserActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            sellerId = arguments?.getString(SELLER_ID)
            category = arguments?.getString(CATEGORY)
            isFromConfirmOrder = arguments?.getBoolean(IS_FROM_CONFIRM_ORDER) ?: false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setupDefaults()
    }

    private fun init(view: View) {
        recyclerView = view.findViewById(R.id.rv_product_list)
        adapter = ProductListRecyclerAdapter(context, productList, this)
    }

    private fun setupDefaults() {
        productListUserActivity!!.setInterface(this)
        recyclerView!!.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView!!.adapter = adapter
        callGetCustProductList()
    }

    private fun constructJson(map: HashMap<String, String?>): JsonObject {
        val jsonObject = JsonObject()
        try {
            for ((key, value) in map) {
                jsonObject.addProperty(key, value)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    fun callGetCustProductList() {
        DeviceUtils.hideSoftKeyboard(activity)
        if (!DeviceUtils.isInternetConnected(context)) {
            AlertUtils.showNoInternetConnection(context)
            return
        }
        dialog!!.show()
        val map = HashMap<String, String?>()
        map["start"] = "0"
        map["sellerId"] = sellerId
        map["cate"] = category
        app.retrofitInterface.postGetProdListSeller("C", constructJson(map)).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                Log.e(TAG, content)
                dialog!!.hide()
                val parser = Gson().fromJson(content, ProductListParser::class.java)
                if (!parser.error!! && !parser.productList?.isEmpty()!!) {
                    productList = parser.productList as ArrayList<ProductListParser.ProductList?>
                    if (!CustConstants.selectedProdList.isEmpty()) {
                        val newList = ArrayList<ProductListParser.ProductList>()
                        for (i in productList!!.indices) {
                            for (selectedProduct in CustConstants.selectedProdList) {
                                if (productList!![i]?.category == selectedProduct?.category && productList!![i]?.id == selectedProduct?.id) {
                                    productList!![i]!!.qty = selectedProduct!!.qty
                                }
                            }
                        }
                        adapter!!.updateList(productList)
                    } else {
                        adapter!!.updateList(productList)
                    }
                }
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(context, message)
            }
        })
    }

    fun calculateAmt() {
        var totalAmt = 0.0
        for (item in CustConstants.selectedProdList) {
            var qtyAmt = 0.0
            for (unitDetail in item?.unitDetails!!) {
                if (unitDetail?.id.equals(item.seletecdUnitId, ignoreCase = true)) {
                    qtyAmt = if (TextUtils.isNullOrEmpty(unitDetail?.offerPrice) ||
                            unitDetail?.offerPrice.equals("0.00", ignoreCase = true)) {
                        unitDetail?.unitPrice?.toDouble()!! * item.qty
                    } else {
                        unitDetail?.offerPrice?.toDouble()!! * item.qty
                    }
                }
                break
            }
            totalAmt = totalAmt + qtyAmt
        }
        productListUserActivity!!.textTotal!!.text = totalAmt.toString() + ""
        CustConstants.subTotal = totalAmt
        CustConstants.deliveryCharge = deliveryCharge.toDouble()
        CustConstants.totalAmt = totalAmt + deliveryCharge.toDouble()
    }

    override fun buttonClicked() {}

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val SELLER_ID = "seller_id"
        private const val CATEGORY = "category"
        private const val IS_FROM_CONFIRM_ORDER = "is_from_confirm_order"

        // TODO: Rename and change types and number of parameters
        fun newInstance(sellerId: String?, category: String?, isFromConfirmOrder: Boolean): ProductListFragment {
            val fragment = ProductListFragment()
            val args = Bundle()
            args.putString(SELLER_ID, sellerId)
            args.putString(CATEGORY, category)
            args.putBoolean(IS_FROM_CONFIRM_ORDER, isFromConfirmOrder)
            fragment.arguments = args
            return fragment
        }
    }
}
package com.e_comapp.android.user.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.adapters.ViewPagerAdapter
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.CustConstants
import com.e_comapp.android.user.activities.ProductListUserActivity
import com.e_comapp.android.user.fragments.ClickInterface
import com.e_comapp.android.user.fragments.ProductListFragment
import com.e_comapp.android.user.models.CategoryListParser
import com.e_comapp.android.user.models.CategoryListParser.CategoryList
import com.e_comapp.android.user.models.OrderedProdModel
import com.e_comapp.android.user.models.SellerListParser.SellerList
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.AppConstants
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.utils.TextUtils
import com.e_comapp.android.viewmodel.ProductListViewModel
import com.e_comapp.android.viewmodel.SellerListViewModel
import com.e_comapp.android.views.CustomBtn
import com.e_comapp.android.views.CustomTextView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import cz.msebera.android.httpclient.entity.StringEntity
import okhttp3.ResponseBody
import org.koin.android.viewmodel.scope.getViewModel
import retrofit2.Call
import java.util.*

class ProductListUserActivity : BaseActivity() {
    val TAG = javaClass.simpleName
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var sellerId: String? = null
    private var sellerList: SellerList? = null
    private var btnCheckOut: CustomBtn? = null
    var textTotal: CustomTextView? = null
    private var categoryList: ArrayList<CategoryList?>? = null
    private var clickInterface: ClickInterface? = null
    private var isFromConfirmOrder = false
    private val viewModel by lazy { viewModelScope?.getViewModel<ProductListViewModel>(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list_user)
        init()
        setupDefaults()
        setupEvents()
        listener()
    }

    fun setInterface(clickInterface: ClickInterface?) {
        this.clickInterface = clickInterface
    }

    override fun menuClicked() {
        super.menuClicked()
        onBackPressed()
    }

    private fun init() {
        categoryList = ArrayList()
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        textTotal = findViewById(R.id.txtTotal)
        btnCheckOut = findViewById(R.id.btnCheckout)
    }

    private fun setupDefaults() {
        setupCustToolbar()
        setMenuIcon(R.drawable.back)
        if (intent != null) {
            if (intent.hasExtra("seller")) {
                sellerList = intent.getSerializableExtra("seller") as SellerList
                sellerId = sellerList?.id
                app.appPreference?.setSellerId(sellerId)
                CustConstants.sellerId = sellerList?.id.toString()
            } else if (intent.hasExtra(AppConstants.Companion.FROM_CONFIRM_ORDER)) {
                isFromConfirmOrder = true
            }
        }
        if (CustConstants.selectedProdList.isNotEmpty()) {
            textTotal!!.text = CustConstants.subTotal.toString() + ""
        }

        DeviceUtils.hideSoftKeyboard(this)
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        } else {
            sellerId?.let { viewModel?.callGetCustCategoryList(it) }
        }
    }

    private fun listener() {
        viewModel?.isLoading?.observe(this, androidx.lifecycle.Observer {
            if (it) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        })

        viewModel?.apiError?.observe(this, androidx.lifecycle.Observer {
            AlertUtils.showAlert(this, it)
        })

        viewModel?.listItem?.observe(this, androidx.lifecycle.Observer {
            setupViewPager(viewPager, it, isFromConfirmOrder)
            tabLayout!!.setupWithViewPager(viewPager)
        })

        viewModel?.checkQtySuccessResponse?.observe(this, androidx.lifecycle.Observer {
            if (isFromConfirmOrder) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                val intent = Intent(this@ProductListUserActivity, SetDeliveryLocationActivity::class.java)
                startActivity(intent)
            }
        })
    }

    private fun setupEvents() {
        btnCheckOut!!.setOnClickListener {
            DeviceUtils.hideSoftKeyboard(this)
            if (!DeviceUtils.isInternetConnected(this)) {
                AlertUtils.showNoInternetConnection(this)
            } else {
                viewModel?.callCheckQtyApi()
            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager?, categoryList: List<CategoryList>, isFromConfirmOrder: Boolean) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        for (category in categoryList) {
            val fragment: ProductListFragment = ProductListFragment.Companion.newInstance(app.appPreference?.getSellerId(), category.category, isFromConfirmOrder)
            fragment.setActivity(this)
            adapter.addFragment(fragment, category.category)
        }
        viewPager!!.offscreenPageLimit = categoryList.size
        viewPager.adapter = adapter
    }

    /*private fun constructJson(map: HashMap<String, String?>): JsonObject {
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

    fun callGetCustCategoryList() {
        DeviceUtils.hideSoftKeyboard(this)
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        val map = HashMap<String, String?>()
        map["start"] = "0"
        map["sellerId"] = app.appPreference?.getSellerId()
        app.retrofitInterface.getCategoryListSeller("C", constructJson(map)).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                Log.e(TAG, content)
                dialog!!.hide()
                val parser = Gson().fromJson(content, CategoryListParser::class.java)
                categoryList = parser.categoryList as ArrayList<CategoryList?>
                setupViewPager(viewPager, categoryList, isFromConfirmOrder)
                tabLayout!!.setupWithViewPager(viewPager)
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(this@ProductListUserActivity, message)
            }
        })
    }*/

}
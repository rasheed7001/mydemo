package com.e_comapp.android.user.activities

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.databinding.ActivitySellerListBinding
import com.e_comapp.android.user.adapters.SearchRecyclerAdapter
import com.e_comapp.android.user.listeners.OnSellerItemClickedListener
import com.e_comapp.android.user.models.SellerListParser.SellerList
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.utils.ParamConstant
import com.e_comapp.android.utils.afterTextChanged
import com.e_comapp.android.viewmodel.SellerListViewModel
import com.google.gson.JsonObject
import org.koin.android.viewmodel.scope.getViewModel
import java.util.*

class SellerListActivity : BaseActivity(), OnSellerItemClickedListener {
    val TAG = javaClass.simpleName
    var industryId: String? = null
    var industryName: String? = null

    val adapter = SearchRecyclerAdapter( this)
    private val viewModel by lazy { viewModelScope?.getViewModel<SellerListViewModel>(this) }


    private lateinit var binding: ActivitySellerListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
                        this,
                        R.layout.activity_seller_list
                )
        setupDefaults()
        setupEvents()
        listener()
    }

    override fun menuClicked() {
        super.menuClicked()
        onBackPressed()
    }

    private fun setupDefaults() {
        setupCustToolbar()
        setMenuIcon(R.drawable.back)

        val bundle = intent.extras
        industryId = bundle?.getString(ParamConstant.INDUSTRY_ID)
        industryName = bundle?.getString(ParamConstant.INDUSTRY_NAME)
        setCustTitle(industryName)

        binding.rvSellerList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvSellerList.addItemDecoration(DividerItemDecoration(binding.rvSellerList.context, DividerItemDecoration.VERTICAL))
        binding.rvSellerList.adapter = adapter

        DeviceUtils.hideSoftKeyboard(this)
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        } else {
            industryId?.let { viewModel?.callGetCustSellers(it) }
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
            AlertUtils.showAlert(this@SellerListActivity, it)
        })

        viewModel?.listItem?.observe(this, androidx.lifecycle.Observer {
            adapter.updateList(it ?: emptyList())
        })
    }

    private fun setupEvents() {
        binding.searchView.afterTextChanged {
            viewModel?.searchListItem(it)
        }
    }

    override fun onSellerItemClicked(sellerList: SellerList?) {
        val intent = Intent(this, ProductListUserActivity::class.java)
        intent.putExtra("seller", sellerList)
        startActivity(intent)
    }
}
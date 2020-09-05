package com.e_comapp.android.user.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseFragment
import com.e_comapp.android.databinding.FragmentSearchBinding
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.activities.ProductListUserActivity
import com.e_comapp.android.user.adapters.SearchRecyclerAdapter
import com.e_comapp.android.user.listeners.OnSellerItemClickedListener
import com.e_comapp.android.user.models.SellerListParser
import com.e_comapp.android.user.models.SellerListParser.SellerList
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.viewmodel.SellerListViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.koin.android.viewmodel.scope.getViewModel
import retrofit2.Call
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : BaseFragment(), OnSellerItemClickedListener {
    val TAG = javaClass.name

    //    var adapter: SearchRecyclerAdapter? = null
    var recyclerView: RecyclerView? = null
//    var sellerList: ArrayList<SellerList?>? = null


    val adapter = SearchRecyclerAdapter( this)
    private val viewModel by lazy { viewModelScope?.getViewModel<SellerListViewModel>(this) }

    private lateinit var binding: FragmentSearchBinding

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_search,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDefaults()

        viewModel?.isLoading?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                dialog?.show()
            } else {
                dialog?.dismiss()
            }
        })

        viewModel?.apiError?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            AlertUtils.showAlert(requireContext(), it)
        })

        viewModel?.sellerListResponse?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.updateList(it ?: emptyList())
        })
    }

    private fun setupDefaults() {
        binding.rvSearchSeller.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvSearchSeller.adapter = adapter
        DeviceUtils.hideSoftKeyboard(activity)
        if (!DeviceUtils.isInternetConnected(context)) {
            AlertUtils.showNoInternetConnection(context)
            return
        } else {
            viewModel?.callGetCustSellers(industry = "2")
        }
    }

    /*fun callGetCustSellers() {
        DeviceUtils.hideSoftKeyboard(activity)
        if (!DeviceUtils.isInternetConnected(context)) {
            AlertUtils.showNoInternetConnection(context)
            return
        }
        dialog!!.show()
        val map = HashMap<String, String>()
        map["start"] = "0"
        map["industry"] = "2"
        app.retrofitInterface.postGetCustSellers("C", constructJson(map)).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                Log.e(TAG, content)
                dialog!!.hide()
                val parser = Gson().fromJson(content, SellerListParser::class.java)
                if (!parser.error!!) {
                    if (!parser.sellerList?.isEmpty()!!) {
                        adapter!!.updateList(parser.sellerList as ArrayList<SellerList?>)
                    }
                }
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(context, message)
            }
        })
    }*/

    override fun onSellerItemClicked(sellerList: SellerList?) {
        val intent = Intent(context, ProductListUserActivity::class.java)
        intent.putExtra("seller", sellerList)
        startActivity(intent)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): SearchFragment {
            val fragment = SearchFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
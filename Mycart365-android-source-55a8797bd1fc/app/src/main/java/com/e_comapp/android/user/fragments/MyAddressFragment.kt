package com.e_comapp.android.user.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseFragment
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.activities.MainPageActivity
import com.e_comapp.android.user.adapters.AddressListAdapter
import com.e_comapp.android.user.adapters.AddressListAdapter.MyViewHolder
import com.e_comapp.android.user.adapters.RecyclerItemTouchHelper
import com.e_comapp.android.user.adapters.RecyclerItemTouchHelper.RecyclerItemTouchHelperListener
import com.e_comapp.android.user.models.AddressListParser
import com.e_comapp.android.user.models.AddressListParser.DeliveryAddressList
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.views.CustomTextView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [MyAddressFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyAddressFragment : BaseFragment(), RecyclerItemTouchHelperListener {
    var addressList: ArrayList<DeliveryAddressList?>? = null
    var recyclerView: RecyclerView? = null
    var adapter: AddressListAdapter? = null
    var textNoAddress: CustomTextView? = null

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setLeftMenuIcon(R.drawable.ic_left_menu)
        setTitle(getString(R.string.str_my_address))
    }

    override fun menuClicks() {
        super.menuClicks()
        (activity as MainPageActivity?)!!.drawer!!.openDrawer(GravityCompat.START)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setupDefaults()
        setupEvents()
    }

    private fun init(view: View) {
        textNoAddress = view.findViewById(R.id.textNoAddress)
        addressList = ArrayList()
        recyclerView = view.findViewById(R.id.rv_address)
        adapter = AddressListAdapter(context, addressList)
    }

    private fun setupDefaults() {
        recyclerView!!.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView!!.adapter = adapter
        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
        callAddressListApi()
    }

    private fun setupEvents() {}
    private fun constructJson(start: Int): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("start", start)
        return jsonObject
    }

    private fun constructJson2(id: String?): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Id", id)
        return jsonObject
    }

    private fun callAddressListApi() {
        if (!DeviceUtils.isInternetConnected(context)) {
            AlertUtils.showNoInternetConnection(context)
            return
        }
        dialog!!.show()
        app.retrofitInterface.getDeliveryAddress("C", constructJson(0)).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.dismiss()
                val parser = Gson().fromJson(content, AddressListParser::class.java)
                if (!parser.error!!) {
                    addressList = parser.deliveryAddressList as ArrayList<DeliveryAddressList?>
                    if (!addressList!!.isEmpty()) {
                        adapter!!.updateList(addressList)
                        textNoAddress!!.visibility = View.GONE
                    } else {
                        textNoAddress!!.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(activity, message)
            }
        })
    }

    private fun callDeleteAddressApi(id: String?) {
        if (!DeviceUtils.isInternetConnected(context)) {
            AlertUtils.showNoInternetConnection(context)
            return
        }
        dialog!!.show()
        app.retrofitInterface.deleteDeliveryAddress("C", constructJson2(id)).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.hide()
                Log.e("MyaddressFragment", content)
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(activity, message)
            }
        })
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is MyViewHolder) {
            // get the removed item name to display it in snack bar

            // backup of removed item for undo purpose
            val deletedItem = addressList!![viewHolder.getAdapterPosition()]
            val deletedIndex = viewHolder.getAdapterPosition()

            // remove the item from recycler view
            adapter!!.removeItem(viewHolder.getAdapterPosition())
            callDeleteAddressApi(deletedItem?.id)

            // showing snack bar with Undo option
            val snackbar = Snackbar
                    .make(recyclerView!!, "address removed from cart!", Snackbar.LENGTH_LONG)
            snackbar.setAction("UNDO") { // undo is selected, restore the deleted item
                adapter!!.restoreItem(deletedItem, deletedIndex)
            }
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()
        }
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
         * @return A new instance of fragment MyAddressFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): MyAddressFragment {
            val fragment = MyAddressFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
package com.e_comapp.android.user.activities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.DeliveryAddressDialog
import com.e_comapp.android.user.adapters.AddViewMoreAdapter
import com.e_comapp.android.user.adapters.DeliveryAddressAdapter
import com.e_comapp.android.user.adapters.DeliveryAddressAdapter.DeliveryAddressClickListener
import com.e_comapp.android.user.adapters.VehicleTypeAdapter
import com.e_comapp.android.user.models.AddressListParser
import com.e_comapp.android.user.models.AddressListParser.DeliveryAddressList
import com.e_comapp.android.user.models.DropDown
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.views.CustomEditText
import com.e_comapp.android.views.CustomTextView
import com.e_comapp.android.views.DashedLineView
import com.e_comapp.android.views.FlowLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*

class PickupAndDropActivity : BaseActivity(), DeliveryAddressClickListener {
    var flParcelType: FlowLayout? = null
    var vehicleTypeAdapter: VehicleTypeAdapter? = null
    var rvVehicleType: RecyclerView? = null
    var clDropC: ConstraintLayout? = null
    var clDropD: ConstraintLayout? = null
    var clDropE: ConstraintLayout? = null
    var clDropF: ConstraintLayout? = null
    var etPickup: CustomEditText? = null
    var etDropB: CustomEditText? = null
    var etDropC: CustomEditText? = null
    var etDropD: CustomEditText? = null
    var etDropE: CustomEditText? = null
    var etDropF: CustomEditText? = null
    var textAlphaA: CustomTextView? = null
    var expandA: ImageView? = null
    var llAdd: LinearLayout? = null
    var customDialog: Dialog? = null
    var addressList: ArrayList<DeliveryAddressList?>? = null
    var dynamicViewCount = 0
    var lnrDynamicViewContainer: LinearLayout? = null
    var spinnerAdapter: AddViewMoreAdapter? = null
    var customSpinnerList: ArrayList<DropDown>? = null
    var popupWindow: PopupWindow? = null
    var mDrawable: Drawable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pickup_and_drop)
        init()
        setupDefaults()
        setupEvents()
    }

    override fun menuClicked() {
        super.menuClicked()
        onBackPressed()
    }

    private fun init() {
        flParcelType = findViewById(R.id.flParcelType)
        rvVehicleType = findViewById(R.id.rv_vehicle_type)
        vehicleTypeAdapter = VehicleTypeAdapter()
        lnrDynamicViewContainer = findViewById(R.id.ll_location)
        llAdd = findViewById(R.id.llAdd)
        clDropC = findViewById(R.id.cl_c)
        clDropD = findViewById(R.id.cl_d)
        clDropE = findViewById(R.id.cl_e)
        clDropF = findViewById(R.id.cl_f)
        etPickup = findViewById(R.id.et_location1)
        etDropB = findViewById(R.id.et_location2)
        etDropC = findViewById(R.id.et_location3)
        etDropD = findViewById(R.id.et_location4)
        etDropE = findViewById(R.id.et_location5)
        etDropF = findViewById(R.id.et_location6)
        expandA = findViewById(R.id.ic_down_arrow1)
        textAlphaA = findViewById(R.id.textA)
    }

    private fun setupDefaults() {
        setupCustToolbar()
        setMenuIcon(R.drawable.back)
        setCustTitle(getString(R.string.str_promocode))
        setFl()
        rvVehicleType!!.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvVehicleType!!.adapter = vehicleTypeAdapter
        callAddressListApi()
        addDynamicLocation()
    }

    private fun setupEvents() {
        llAdd!!.setOnClickListener { addDynamicLocation() }
        expandA!!.setOnClickListener {
            val dataAdapter = DeliveryAddressAdapter(this@PickupAndDropActivity, addressList, this@PickupAndDropActivity)

            val bottomSheetFragment = DeliveryAddressDialog(dataAdapter, "address");
            bottomSheetFragment.show(supportFragmentManager, "dialog")

/*            customDialog = DeliveryAddressDialog(this@PickupAndDropActivity, dataAdapter, "address")
            customDialog?.show()
            customDialog?.setCanceledOnTouchOutside(false)*/
        }
    }

    private fun setFl() {
        flParcelType!!.removeAllViews()
        for (i in 0..17) {
            val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.fl_parcel_item, flParcelType, false)
            flParcelType!!.addView(view)
        }
    }

    /*
    private void addDropLocation(){
       switch (lastLocation){
           case "B":
               clDropC.setVisibility(View.VISIBLE);
               lastLocation = "C";
               break;
           case "C":
               clDropD.setVisibility(View.VISIBLE);
               lastLocation = "D";
               break;
           case "D":
               clDropE.setVisibility(View.VISIBLE);
               lastLocation = "E";
               break;
           case "E":
               clDropF.setVisibility(View.VISIBLE);
               lastLocation = "F";
               break;
       }
    }

    private void removeView(){
        switch (lastLocation){
            case "C":
                clDropC.setVisibility(View.GONE);
                lastLocation = "B";
                break;
            case "D":
                clDropD.setVisibility(View.GONE);
                lastLocation = "C";
                break;
            case "E":
                clDropE.setVisibility(View.GONE);
                lastLocation = "D";
                break;
            case "F":
                clDropF.setVisibility(View.GONE);
                lastLocation = "E";
                break;
        }
    }*/
    private fun addDynamicLocation() {
        dynamicViewCount++
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.drop_address, null)
        view.tag = dynamicViewCount
        val etLocation: CustomEditText = view.findViewById(R.id.et_location)
        val ivDown = view.findViewById<ImageView>(R.id.iv_down_arrow)
        val textAlpha: CustomTextView = view.findViewById(R.id.textAlphaBet)
        val dashLine: DashedLineView = view.findViewById(R.id.dashLine1)
        val textLocationType: CustomTextView = view.findViewById(R.id.textPorD)
        when (dynamicViewCount) {
            1 -> textAlpha.text = "B"
            2 -> textAlpha.text = "C"
            3 -> textAlpha.text = "D"
            4 -> textAlpha.text = "E"
            5 -> textAlpha.text = "F"
        }
        ivDown.setOnClickListener { view ->
            val dataAdapter = DeliveryAddressAdapter(this@PickupAndDropActivity, addressList, this@PickupAndDropActivity, (view.parent as View).tag)

            val bottomSheetFragment = DeliveryAddressDialog(dataAdapter, "address");
            bottomSheetFragment.show(supportFragmentManager, "dialog")

/*            customDialog = DeliveryAddressDialog(this@PickupAndDropActivity, dataAdapter, "address")
            customDialog?.show()
            customDialog?.setCanceledOnTouchOutside(false)*/
        }
        textLocationType.setOnClickListener {
            customSpinnerList = ArrayList()
            customSpinnerList!!.add(DropDown("Pickup", false))
            customSpinnerList!!.add(DropDown("Drop", false))
            customSpinnerList!!.add(DropDown("Drop & Pickup", false))
            listPopUpMenu(customSpinnerList!!, textLocationType, textLocationType)
        }
        lnrDynamicViewContainer!!.addView(view)
    }

    private fun constructJson(start: Int): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("start", start)
        return jsonObject
    }

    private fun callAddressListApi() {
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
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
                    addressList?.get(0)?.isSelected = true                }
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(this@PickupAndDropActivity, message)
            }
        })
    }

    override fun clickOnAddressItem(data: DeliveryAddressList?, position: Int, id: Any?) {
        if (customDialog != null) {
            customDialog!!.dismiss()
            for (model in addressList!!) {
                if (model?.id == data?.id) {
                    model?.isSelected = true
                } else {
                    model?.isSelected = false
                }
            }
            val editText: CustomEditText = lnrDynamicViewContainer!!.findViewWithTag<View>(id).findViewById(R.id.et_location)
            val textAlpha: CustomTextView = lnrDynamicViewContainer!!.findViewWithTag<View>(id).findViewById(R.id.textAlphaBet)
            val dashedLineView: DashedLineView = lnrDynamicViewContainer!!.findViewWithTag<View>(id).findViewById(R.id.dashLine1)
            dashedLineView.setColor(resources.getColor(R.color.dark_green))
            editText.setText(data?.fullAddress)
            if (!textAlpha.isSelected) {
                textAlpha.isSelected = true
            }
        }
    }

    override fun clickOnAddressItem(data: DeliveryAddressList?, position: Int) {
        if (customDialog != null) {
            customDialog!!.dismiss()
            for (model in addressList!!) {
                if (model?.id == data?.id) {
                    model?.isSelected = true
                } else {
                    model?.isSelected = false
                }
            }
            etPickup?.setText(data?.fullAddress)
            if (!textAlphaA!!.isSelected) {
                textAlphaA!!.isSelected = true
            }
        }
    }

    private fun listPopUpMenu(list: ArrayList<DropDown>, view: View, textView: CustomTextView) {
        spinnerAdapter = AddViewMoreAdapter(this, R.layout.layout_add_viewmore_details_spinner, list.toMutableList())
        spinnerAdapter!!.notifyDataSetChanged()
        popupWindow = popupWindowsort(spinnerAdapter!!, textView)
        if (popupWindow != null) {
            popupWindow!!.width = DeviceUtils.getDisplayMetrics(this).widthPixels * 35 / 100
            popupWindow!!.showAsDropDown(view, 0, 0)
        }
    }

    private fun popupWindowsort(spinnerAdapter: AddViewMoreAdapter, textView: CustomTextView): PopupWindow {
        popupWindow = PopupWindow(this)
        val listViewSort = ListView(this)
        listViewSort.divider = null
        listViewSort.adapter = spinnerAdapter
        listViewSort.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            textView.text = customSpinnerList!![i].text
            popupWindow!!.dismiss()
        }
        popupWindow!!.isFocusable = true
        popupWindow!!.elevation = 6f
        popupWindow!!.height = ViewGroup.LayoutParams.WRAP_CONTENT
        Log.e("Width", " " + DeviceUtils.getDisplayMetrics(this).widthPixels * 30 / 100)
        popupWindow!!.contentView = listViewSort
        mDrawable = ColorDrawable(Color.WHITE)
        popupWindow!!.setBackgroundDrawable(mDrawable)
        return popupWindow as PopupWindow
    }
}
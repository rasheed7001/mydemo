package com.e_comapp.android.user.activities

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.activities.SetDeliveryLocationActivity
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.AppConstants
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.views.CustomBtn
import com.e_comapp.android.views.CustomEditText
import com.e_comapp.android.views.CustomTextView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.io.IOException
import java.util.*

class SetDeliveryLocationActivity : BaseActivity(), OnMapReadyCallback {
    var btnNext: CustomBtn? = null
    var etLocation: CustomEditText? = null
    var etHouseNo: CustomEditText? = null
    var etApartment: CustomEditText? = null
    var etLandMark: CustomEditText? = null
    var etInstruction: CustomEditText? = null
    var etContactPerson: CustomEditText? = null
    var etSaveLocationAs: CustomEditText? = null
    var textHome: CustomTextView? = null
    var textOffice: CustomTextView? = null
    var textOther: CustomTextView? = null
    private var errorFlag = false
    private var googleMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mapView: View? = null
    private var mGeocoder: Geocoder? = null
    private var isLocationPermissionGranted = false
    private var googleApiClient: GoogleApiClient? = null
    private var lastKnownLocation: Location? = null
    private var selectedLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_delivery_location)
        init()
        setupDefaults()
        setupEvents()
    }

    override fun menuClicked() {
        super.menuClicked()
        onBackPressed()
    }

    override fun rightMenuClicked() {
        super.rightMenuClicked()
        val intent = Intent(this@SetDeliveryLocationActivity, MyCartActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (isLocationPermissionGranted) {
            isLocationPermissionGranted = false
            locationPermission
        }
        FusedLocationSingleton.Companion.getInstance(this)!!.startLocationUpdates()
        // register observer for location updates
        LocalBroadcastManager.getInstance(this@SetDeliveryLocationActivity).registerReceiver(mLocationUpdated,
                IntentFilter(AppConstants.Companion.INTENT_FILTER_LOCATION_UPDATE))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.Companion.RC_MARSH_MALLOW_LOCATION_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isLocationPermissionGranted = true
                    setupLocationEvent()
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this@SetDeliveryLocationActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        showContactPermission()
                    } else {
                        requestPermission()
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState)
    }

    private fun init() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mGeocoder = Geocoder(this, Locale.getDefault())
        mapView = mapFragment!!.view
        btnNext = findViewById(R.id.btnNext)
        etLocation = findViewById(R.id.et_location)
        etHouseNo = findViewById(R.id.et_house_no)
        etApartment = findViewById(R.id.et_building_name)
        etLandMark = findViewById(R.id.et_landmark)
        etInstruction = findViewById(R.id.et_instruction)
        etContactPerson = findViewById(R.id.et_contact)
        textHome = findViewById(R.id.textHome)
        textOffice = findViewById(R.id.textOffice)
        textOther = findViewById(R.id.textOther)
    }

    private fun setupDefaults() {
        setupCustToolbar()
        setMenuIcon(R.drawable.back)
        setCustTitle(getString(R.string.str_set_delivery_location))
        setRightMenuIcon(R.drawable.my_cart_white)
        mapFragment!!.getMapAsync(this)
        locationPermission
    }

    private fun setupEvents() {
        textOffice!!.setOnClickListener {
            textOffice!!.isSelected = true
            textHome!!.isSelected = false
            textOther!!.isSelected = false
        }
        textHome!!.setOnClickListener {
            textOffice!!.isSelected = false
            textHome!!.isSelected = true
            textOther!!.isSelected = false
        }
        textOther!!.setOnClickListener {
            textOffice!!.isSelected = false
            textHome!!.isSelected = false
            textOther!!.isSelected = true
        }
        btnNext!!.setOnClickListener {
            errorFlag = false
            if (etLocation!!.text.toString().isEmpty()) {
                etLocation!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etHouseNo!!.text.toString().isEmpty()) {
                etHouseNo!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etLocation!!.text.toString().isEmpty()) {
                etLocation!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etApartment!!.text.toString().isEmpty()) {
                etApartment!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etLandMark!!.text.toString().isEmpty()) {
                etLandMark!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etInstruction!!.text.toString().isEmpty()) {
                etInstruction!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (errorFlag == false) {
                callSaveDeliveryAddressApi()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        addOnMapClickListener(this.googleMap)
        //        getLocationPermission();
        updateLocationUI()
        if (selectedLatLng != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 16.0f))
            addMarker(selectedLatLng)
        }
        if (mapView != null &&
                mapView!!.findViewById<View?>("1".toInt()) != null) {
            val locationButton = (mapView!!.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
            // and next place it, on bottom right (as Google Maps app)
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 30, 30)
        }
        googleMap.setOnMyLocationButtonClickListener {
            deviceLocation2
            true
        }
    }

    private val locationPermission: Unit
        private get() {
            Log.e("tag", "Called --> getLocationPermission()")
            if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this@SetDeliveryLocationActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                        showContactPermission()
                    } else {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), AppConstants.Companion.RC_MARSH_MALLOW_LOCATION_PERMISSION)
                    }
                } else {
                    setupLocationEvent()
                }
            } else {
                setupLocationEvent()
            }
        }

    private fun requestPermission() {
        val builder = AlertDialog.Builder(this@SetDeliveryLocationActivity)
        builder.setTitle("Location access needed")
        builder.setPositiveButton(android.R.string.ok, null)
        builder.setMessage("Need Location permission to access your location. Please enable location permission.")
        builder.setOnDismissListener { dialog ->
            dialog.dismiss()
            ActivityCompat.requestPermissions(this@SetDeliveryLocationActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), AppConstants.Companion.RC_MARSH_MALLOW_LOCATION_PERMISSION)
        }
        builder.show()
    }

    private fun showContactPermission() {
        val builder = AlertDialog.Builder(this@SetDeliveryLocationActivity)
        builder.setTitle("Location access needed")
        builder.setPositiveButton(R.string.go_to_appsettings, null)
        builder.setMessage("Need location permission to access your location. Please enable location permission in the settings.")
        builder.setOnDismissListener { dialog ->
            isLocationPermissionGranted = true
            dialog.dismiss()
            goToSettings()
        }
        builder.show()
    }

    fun goToSettings() {
        val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + application.packageName))
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(myAppSettings)
    }

    private fun setupLocationEvent() {
        val manager = this@SetDeliveryLocationActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this@SetDeliveryLocationActivity)) {
//            Toast.makeText(ChooseLocationActivity.this, "Gps already enabled", Toast.LENGTH_SHORT).show();
//            finish();
        }
        // Todo Location Already on  ... end
        if (!hasGPSDevice(this@SetDeliveryLocationActivity)) {
            Toast.makeText(this@SetDeliveryLocationActivity, "Gps not Supported", Toast.LENGTH_SHORT).show()
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this@SetDeliveryLocationActivity)) {
            Log.e("keshav", "Gps already enabled")
            Toast.makeText(this@SetDeliveryLocationActivity, "Gps not enabled", Toast.LENGTH_SHORT).show()
            enableLoc()
        } else {
            Log.e("keshav", "Gps already enabled")
            //            Toast.makeText(ChooseLocationActivity.this, "Gps already enabled", Toast.LENGTH_SHORT).show();
        }
    }

    private fun hasGPSDevice(context: Context): Boolean {
        val mgr = context
                .getSystemService(Context.LOCATION_SERVICE) as LocationManager
                ?: return false
        val providers = mgr.allProviders ?: return false
        return providers.contains(LocationManager.GPS_PROVIDER)
    }

    private fun enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this@SetDeliveryLocationActivity)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(object : ConnectionCallbacks {
                        override fun onConnected(bundle: Bundle?) {}
                        override fun onConnectionSuspended(i: Int) {
                            googleApiClient!!.connect()
                        }
                    })
                    .addOnConnectionFailedListener { connectionResult -> Log.d("Location error", "Location error " + connectionResult.errorCode) }.build()
            googleApiClient?.connect()
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 30 * 1000.toLong()
            locationRequest.fastestInterval = 5 * 1000.toLong()
            val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
            builder.setAlwaysShow(true)
            val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
            result.setResultCallback { result ->
                val status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(this@SetDeliveryLocationActivity, REQUEST_LOCATION)

//                                finish();
                    } catch (e: SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }
    }

    private fun updateLocationUI() {
        if (googleMap == null) {
            return
        }
        try {
            if (isLocationPermissionGranted) {
                googleMap!!.isMyLocationEnabled = true
                googleMap!!.uiSettings.isMyLocationButtonEnabled = true
            } else {
                googleMap!!.isMyLocationEnabled = false
                googleMap!!.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                //                getLocationPermission();
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }

    //        atv_address.dismissDropDown();
    private val deviceLocation2: Unit
        private get() {
            selectedLatLng = currentLatLng
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f))
            addMarker(currentLatLng)
            etLocation!!.setText(getAddress(currentLatLng))
            //        atv_address.dismissDropDown();
        }

    private fun addMarker(latLng: LatLng?) {
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng!!)
        googleMap!!.clear()
        googleMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap!!.addMarker(markerOptions)
    }

    private fun addOnMapClickListener(googleMap: GoogleMap?) {
        googleMap!!.setOnMapLongClickListener(object : OnMapLongClickListener {
            var marker = MarkerOptions()
            var latLngOld: LatLng? = null
            override fun onMapLongClick(latLng: LatLng) {
                googleMap.clear()
                if (latLngOld == null) {
                    latLngOld = latLng
                    etLocation!!.setText(getAddress(latLngOld))
                    //                    atv_address.dismissDropDown();
                    marker.position(latLngOld!!)
                    selectedLatLng = latLngOld
                    googleMap.addMarker(marker)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLngOld))
                } else {
                    marker.position(latLng)
                    selectedLatLng = latLng
                    etLocation!!.setText(getAddress(latLng))
                    //                    atv_address.dismissDropDown();
                    googleMap.addMarker(marker)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                }
            }
        })
    }

    fun getAddress(latLng: LatLng?): String {
        var address = ""
        try {
            val addresses = mGeocoder!!.getFromLocation(latLng!!.latitude, latLng.longitude, 1)
            address = addresses[0].getAddressLine(0)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }

    private val mLocationUpdated: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                val location = intent.getParcelableExtra<Parcelable>(AppConstants.Companion.LBM_EVENT_LOCATION_UPDATE) as Location
                Log.d("Sabari", location.latitude.toString())
                currentLatLng = LatLng(location.latitude, location.longitude)
                if (dialog!!.isShowing) {
                    dialog!!.dismiss()
                }
                if (selectedLatLng == null) {
                    deviceLocation2
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun constructJson(): JsonObject {
        var addressType = ""
        if (textHome!!.isSelected) {
            addressType = "Home"
        } else if (textOffice!!.isSelected) {
            addressType = "Office"
        } else if (textOther!!.isSelected) {
            addressType = "Other"
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("fullAddress", etLocation!!.text.toString())
        jsonObject.addProperty("address1", etHouseNo!!.text.toString())
        jsonObject.addProperty("address2", etApartment!!.text.toString())
        jsonObject.addProperty("landMark", etLandMark!!.text.toString())
        jsonObject.addProperty("others", etInstruction!!.text.toString())
        jsonObject.addProperty("contactPerson", etContactPerson!!.text.toString())
        jsonObject.addProperty("addressType", addressType)
        return jsonObject
    }

    private fun callSaveDeliveryAddressApi() {
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        app.retrofitInterface.postSaveDeliveryAddress("C", constructJson()).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.hide()
                var error = false
                var message = ""
                var addressId = 0
                try {
                    val response = JSONObject(content)
                    error = response.getBoolean("error")
                    message = response.getString("message")
                    addressId = response.getInt("addressId")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                if (!error) {
                    val intent = Intent(this@SetDeliveryLocationActivity, ConfirmOrderUserActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(this@SetDeliveryLocationActivity, message)
            }
        })
    }

    companion object {
        private val TAG = SetDeliveryLocationActivity::class.java.simpleName
        const val REQUEST_LOCATION = 199
    }
}
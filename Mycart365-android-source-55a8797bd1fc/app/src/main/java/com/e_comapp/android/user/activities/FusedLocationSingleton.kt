package com.e_comapp.android.user.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.e_comapp.android.utils.AppConstants
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

/**
 * Created by admin on 05/01/2019.
 */
class FusedLocationSingleton(private val context: Context?) : ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    protected var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null

    /**
     * destructor
     * @throws Throwable
     */
    @Throws(Throwable::class)
    protected fun finalize() {
        stopLocationUpdates()
    }
    ///////////// 1
    /**
     * builds a GoogleApiClient
     * @param context
     */
    @Synchronized
    private fun buildGoogleApiClient(context: Context?) {
        // setup googleapi client
        mGoogleApiClient = GoogleApiClient.Builder(context!!)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        // setup location updates
        configRequestLocationUpdate()
    }
    ///////////// 2
    /**
     * config request location update
     */
    private fun configRequestLocationUpdate() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_FREQUENCY.toLong())
                .setFastestInterval(FAST_LOCATION_FREQUENCY.toLong())
    }
    ///////////// 3
    /**
     * request location updates
     */
    private fun requestLocationUpdates() {
/*        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }*/
        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        )
    }

    /**
     * start location updates
     */
    fun startLocationUpdates() {
        // connect and force the updates
        mGoogleApiClient!!.connect()
        if (mGoogleApiClient!!.isConnected) {
            requestLocationUpdates()
        }
    }

    /**
     * removes location updates from the FusedLocationApi
     */
    fun stopLocationUpdates() {
        // stop updates, disconnect from google api
        if (null != mGoogleApiClient && mGoogleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            mGoogleApiClient!!.disconnect()
        }
    }// start the updates// return last location

    /**
     * get last available location
     * @return last known location
     */
    val lastLocation: Location?
        get() = if (null != mGoogleApiClient && mGoogleApiClient!!.isConnected) {
            // return last location
            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                null
            } else LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        } else {
            startLocationUpdates() // start the updates
            null
        }

    /***********************************************************************************************
     * GoogleApiClient Callbacks
     */
    override fun onConnected(bundle: Bundle?) {
        Log.e("onConnect", " onConnected")
        // do location updates
        requestLocationUpdates()
    }

    override fun onConnectionSuspended(i: Int) {
        Log.e("onConnect", " onConnectionSuspended")
        // connection to Google Play services was lost for some reason
        if (null != mGoogleApiClient) {
            mGoogleApiClient!!.connect() // attempt to establish a new connection
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e("onConnect", " onConnectionFailed")
    }

    /***********************************************************************************************
     * Location Listener Callback
     */
    override fun onLocationChanged(location: Location) {
        Log.e("onConnect", " onLocationChanged")
        if (location != null) {
            // send location in broadcast
            val intent = Intent(AppConstants.Companion.INTENT_FILTER_LOCATION_UPDATE)
            intent.putExtra(AppConstants.Companion.LBM_EVENT_LOCATION_UPDATE, location)
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
        }
    }

    companion object {
        /***********************************************************************************************
         * properties
         */
        private var mInstance: FusedLocationSingleton? = null
        const val FAST_LOCATION_FREQUENCY = 2 * 1000
        const val LOCATION_FREQUENCY = 2 * 1000
        fun getInstance(context: Context?): FusedLocationSingleton? {
            if (null == mInstance) {
                mInstance = FusedLocationSingleton(context)
            }
            return mInstance
        }
    }
    /***********************************************************************************************
     * methods
     */
    /**
     * constructor
     */
    init {
        buildGoogleApiClient(context)
    }
}
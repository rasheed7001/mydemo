package com.e_comapp.android.user.activities

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.user.activities.MapActivity
import com.e_comapp.android.utils.AnimationUtils.carAnimator
import com.e_comapp.android.utils.AnimationUtils.polylineAnimator
import com.e_comapp.android.utils.MapUtils.getCarBitmap
import com.e_comapp.android.utils.MapUtils.getListOfLocations
import com.e_comapp.android.utils.MapUtils.getOriginDestinationMarkerBitmap
import com.e_comapp.android.utils.MapUtils.getRotation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

class MapActivity : BaseActivity(), OnMapReadyCallback {
    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    private var greyPolyLine: Polyline? = null
    private var blackPolyLine: Polyline? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        init()
        setupDefaults()
        setupEvents()
    }

    override fun menuClicked() {
        super.menuClicked()
        onBackPressed()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        val defaultLocation = LatLng(28.435350000000003, 77.11368)
        showDefuatLocationOnMap(defaultLocation)
        Handler().postDelayed({
            showPath(getListOfLocations())
            showMovingCab(getListOfLocations())
        }, 3000)
    }

    private fun init() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    private fun setupDefaults() {
        setupCustToolbar()
        setMenuIcon(R.drawable.back)
        setCustTitle(getString(R.string.str_payment))
    }

    private fun setupEvents() {}
    private fun moveCamera(latLng: LatLng) {
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng?) {
        val cameraPosition = CameraPosition.builder().target(latLng).zoom(15.5f).build()
        googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun showDefuatLocationOnMap(latLng: LatLng) {
        moveCamera(latLng)
        animateCamera(latLng)
    }

    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private fun showPath(latLngList: ArrayList<LatLng>) {
        val builder = LatLngBounds.Builder()
        for (latLng in latLngList) {
            builder.include(latLng)
        }
        // this is used to set the bound of the Map
        val bounds = builder.build()
        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))
        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.GRAY)
        polylineOptions.width(5f)
        polylineOptions.addAll(latLngList)
        greyPolyLine = googleMap!!.addPolyline(polylineOptions)
        val blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.color(Color.BLACK)
        blackPolylineOptions.width(5f)
        blackPolyLine = googleMap!!.addPolyline(blackPolylineOptions)
        originMarker = addOriginDestinationMarkerAndGet(latLngList[0])
        originMarker!!.setAnchor(0.5f, 0.5f)
        destinationMarker = addOriginDestinationMarkerAndGet(latLngList[latLngList.size - 1])
        destinationMarker!!.setAnchor(0.5f, 0.5f)
        val polylineAnimator = polylineAnimator()
        polylineAnimator.addUpdateListener { valueAnimator ->
            val percentValue = valueAnimator.animatedValue as Int
            val index = greyPolyLine?.getPoints()?.size!! * (percentValue / 100.0f).toInt()
            blackPolyLine?.setPoints(greyPolyLine?.getPoints()?.subList(0, index))
        }
        polylineAnimator.start()
    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getOriginDestinationMarkerBitmap())
        return googleMap!!.addMarker(
                MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun addMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getCarBitmap(this))
        return googleMap!!.addMarker(
                MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private var movingCabMarker: Marker? = null
    private var previousLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null
    private fun updateCarLocation(latLng: LatLng) {
        if (movingCabMarker == null) {
            movingCabMarker = addMarkerAndGet(latLng)
        }
        if (previousLatLng == null) {
            currentLatLng = latLng
            previousLatLng = currentLatLng
            movingCabMarker!!.position = currentLatLng!!
            movingCabMarker!!.setAnchor(0.5f, 0.5f)
            animateCamera(currentLatLng)
        } else {
            previousLatLng = currentLatLng
            currentLatLng = latLng
            val valueAnimator = carAnimator()
            valueAnimator.addUpdateListener { valueAnimator ->
                if (currentLatLng != null && previousLatLng != null) {
                    val multiplier = valueAnimator.animatedFraction
                    val nextLocation = LatLng(
                            multiplier * currentLatLng!!.latitude + (1 - multiplier) * previousLatLng!!.latitude,
                            multiplier * currentLatLng!!.longitude + (1 - multiplier) * previousLatLng!!.longitude
                    )
                    movingCabMarker!!.position = nextLocation
                    val rotation = getRotation(previousLatLng!!, nextLocation)
                    if (!rotation.isNaN()) {
                        movingCabMarker!!.rotation = rotation
                    }
                    movingCabMarker!!.setAnchor(0.5f, 0.5f)
                    animateCamera(nextLocation)
                }
            }
            valueAnimator.start()
        }
    }

    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private fun showMovingCab(cabLatLngList: ArrayList<LatLng>) {
        handler = Handler()
        val index = intArrayOf(0)
        runnable = Runnable {
            if (index[0] < 10) {
                updateCarLocation(cabLatLngList[index[0]])
                handler!!.postDelayed(runnable, 3000)
                ++index[0]
            } else {
                handler!!.removeCallbacks(runnable)
                Toast.makeText(this@MapActivity, "Trip Ends", Toast.LENGTH_LONG).show()
            }
            //            updateCarLocation(cabLatLngList.get(0));
//            handler.postDelayed(runnable,3000);
        }
        handler!!.postDelayed(runnable, 5000)
    }
}
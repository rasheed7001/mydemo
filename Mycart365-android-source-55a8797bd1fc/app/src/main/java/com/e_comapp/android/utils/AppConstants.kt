package com.e_comapp.android.utils

import android.Manifest

interface AppConstants {
    companion object {
        const val REQUEST_PICTURE_FROM_GALLERY = 100
        const val REQUEST_PICTURE_FROM_CAMERA = 101
        const val REQUEST_PERMISSION_READ_STORAGE = 102
        const val REQUEST_CODE_IMAGE_CAPTURE = 100
        const val REQUEST_CODE_GALLERY_IMAGE = 101
        const val REQUEST_WRITE_EXTERNAL_STORAGE = 102
        val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        const val RC_MARSH_MALLOW_LOCATION_PERMISSION = 1001
        const val RC_PRODUCT_LIST_USER_ACTIVITY = 1002
        const val RC_PROMO_CODE_ACTIVITY = 1003
        const val LBM_EVENT_LOCATION_UPDATE = "lbmLocationUpdate"
        const val INTENT_FILTER_LOCATION_UPDATE = "intentFilterLocationUpdate"
        const val FROM_CONFIRM_ORDER = "from_confirm_order"
    }
}
package com.e_comapp.android.preference

import android.content.Context
import android.content.SharedPreferences

class AppPreference(//    private static final String NOTIFICATION_COUNT = "notification_count";
        private val mContext: Context) {
    private val mPreference: SharedPreferences
    private val mEditor: SharedPreferences.Editor
    fun getAppDefaults(): String {
        return mPreference.getString(APP_DEFAULTS, null)
    }

    fun setAppDefaults(value: String?) {
        mEditor.putString(APP_DEFAULTS, value)
        mEditor.commit()
    }

    fun setAppUserType(userType: String?) {
        mEditor.putString(APP_USER_TYPE, userType)
        mEditor.commit()
    }

    fun setAppUserRes(res: String?) {
        mEditor.putString(APP_USER_RES, res)
        mEditor.commit()
    }

    fun getAppUserRes(): String {
        return mPreference.getString(APP_USER_RES, null)
    }

    fun getAppUserType(): String {
        return mPreference.getString(APP_USER_TYPE, "C")
    }

    fun setAppBaseUrl(baseUrl: String?) {
        mEditor.putString(APP_BASE_URL, baseUrl)
        mEditor.commit()
    }

    fun getAppBaseUrl(): String {
        return mPreference.getString(APP_BASE_URL, "")
    }

    fun setAppUserId(id: String?) {
        mEditor.putString(APP_USER_ID, id)
        mEditor.commit()
    }

    fun getAppUserId(): String {
        return mPreference.getString(APP_USER_ID, null)
    }

    fun setAccessToken(accessToken: String?) {
        mEditor.putString(APP_ACCESS_TOKEN, accessToken)
        mEditor.commit()
    }

    fun getAppAccessToken(): String {
        return mPreference.getString(APP_ACCESS_TOKEN, "")
    }

    fun setOtp(otp: Int) {
        mEditor.putInt(OTP, otp)
        mEditor.commit()
    }

    fun getOtp(): Int {
        return mPreference.getInt(OTP, 0)
    }

    fun setStatus(status: String?) {
        mEditor.putString(STATUS, status)
        mEditor.commit()
    }

    fun getStatus(): String {
        return mPreference.getString(STATUS, "")
    }

    fun setFirstName(status: String?) {
        mEditor.putString(FIRST_NAME, status)
        mEditor.commit()
    }

    fun getFirstName(): String {
        return mPreference.getString(FIRST_NAME, "")
    }

    fun setLastName(status: String?) {
        mEditor.putString(LAST_NAME, status)
        mEditor.commit()
    }

    fun getLastName(): String {
        return mPreference.getString(LAST_NAME, "")
    }

    fun setEmail(status: String?) {
        mEditor.putString(EMAIL, status)
        mEditor.commit()
    }

    fun getEmail(): String {
        return mPreference.getString(EMAIL, "")
    }

    fun setUserImage(status: String?) {
        mEditor.putString(USER_IMAGE, status)
        mEditor.commit()
    }

    fun getUserImage(): String {
        return mPreference.getString(USER_IMAGE, "")
    }

    fun setOtpStaus(status: String?) {
        mEditor.putString(OTP_STATUS, status)
        mEditor.commit()
    }

    fun getOtpStatus(): String {
        return mPreference.getString(OTP_STATUS, "")
    }

    fun setMobileNumber(mobileNumber: String?) {
        mEditor.putString(MOBILE_NUMBER, mobileNumber)
        mEditor.commit()
    }

    fun getMobileNumber(): String {
        return mPreference.getString(MOBILE_NUMBER, "")
    }

    fun setImageUri(uri: String?) {
        mEditor.putString(IMAGE_URI, uri)
        mEditor.commit()
    }

    fun getImageUri(): String {
        return mPreference.getString(IMAGE_URI, "")
    }

    fun setImageName(uri: String?) {
        mEditor.putString(IMAGE_NAME, uri)
        mEditor.commit()
    }

    fun getImageName(): String {
        return mPreference.getString(IMAGE_NAME, "")
    }

    fun setSellerId(id: String?) {
        mEditor.putString(SELLER_ID, id)
        mEditor.commit()
    }

    fun getSellerId(): String {
        return mPreference.getString(SELLER_ID, "")
    }

    companion object {
        private const val APP_PREF = "app_news_pref"
        private const val APP_DEFAULTS = "app_defaults"
        private const val APP_BASE_URL = "base_url"
        private const val APP_ACCESS_TOKEN = "access_token"
        private const val APP_USER_TYPE = "user_type"
        private const val APP_USER_RES = "user_res"
        private const val APP_USER_ID = "user_id"
        private const val OTP = "otp"
        private const val STATUS = "status"
        private const val OTP_STATUS = "otp_status"
        private const val FIRST_NAME = "first_name"
        private const val LAST_NAME = "last_name"
        private const val EMAIL = "email"
        private const val USER_IMAGE = "user_image"
        private const val MOBILE_NUMBER = "mobile_number"
        private const val IMAGE_URI = "image_uri"
        private const val IMAGE_NAME = "image_name"
        private const val SELLER_ID = "seller_id"
    }

    init {
        mPreference = mContext.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE)
        mEditor = mPreference.edit()
    }
}
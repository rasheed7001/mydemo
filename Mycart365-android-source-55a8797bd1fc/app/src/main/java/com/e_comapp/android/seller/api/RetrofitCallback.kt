package com.e_comapp.android.seller.api

import android.util.Log
import com.e_comapp.android.utils.TextUtils
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

abstract class RetrofitCallback<T> : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        val statusCode = response.code()
        if (response.body() != null || response.isSuccessful) {
            var responseString = ""
            try {
                val body = response.body() as ResponseBody?
                responseString = body!!.string()
                Log.e("Retrofit callback", "onSuccess Retrofit callback response... " + responseString + " isSuccess ---> " + response.isSuccessful + " status code--->" + statusCode)
                val jsonObject = JSONObject(responseString)
                val metaObject = jsonObject.getJSONObject("meta")
                val code = metaObject.optInt("code")
                var errorMessage = jsonObject.optString("notifications", "")
                if (TextUtils.isNullOrEmpty(errorMessage)) {
                    errorMessage = "Couldn`t connect to server"
                }
                if (code == 9005) {
                    onFailureCallback(call, errorMessage, code)
                } else if (code == 7005) {
                    onFailureCallback(call, errorMessage, code)
                } else if (code == 4015) {
                    onFailureCallback(call, errorMessage, code)
                } else if (code == 30016) {
                } else {
                    onSuccessCallback(call, responseString)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onSuccessCallback(call, responseString)
            }
        } else {
            // failure case
            var message = ""
            var errorMessage = ""
            var code = 0
            try {
                message = response.errorBody()!!.string()
                val jsonObject = JSONObject(message)
                val metaObject = jsonObject.getJSONObject("meta")
                errorMessage = metaObject.getJSONArray("error").getString(0)
                code = metaObject.optInt("code")
                Log.e("code---> ", "---$code")
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            onFailureCallback(call, errorMessage, statusCode)
        }
    }

    /**/
    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.e("onFailure", "failure")
        onFailureCallback(call, "Couldn`t connect to server", 0)
    }

    open fun onSuccessCallback(call: Call<T>?, content: String?) {}
    open fun onFailureCallback(call: Call<T>?, message: String?, code: Int) {}
}
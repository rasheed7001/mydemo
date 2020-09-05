package com.e_comapp.android.network

import android.net.sip.SipErrorCode.SERVER_ERROR
import androidx.lifecycle.MutableLiveData
import com.e_comapp.android.exception.ApiFailureException
import com.e_comapp.android.exception.NoNetworkException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException

const val DATA_NOT_FOUND_ERROR_CODE = 404
const val STARTING_PAGE_INDEX = 0
const val NETWORK_PAGE_SIZE = 10
const val VISIBLE_THRESHOLD = 2
const val NO_INTERNET_ERROR_CODE = 502



/**
 * Common network call class to handle all the network calls
 * */
open class NetworkCall<T> {
    lateinit var call: Call<T>

    /**
     * makeCall Function helps to make network call
     * */
    fun makeCall(call: Call<T>): MutableLiveData<Resource<T>> {
        this.call = call
        val callBackKt = CallBackKt<T>()
        callBackKt.result.value = Resource.loading(null)
        this.call.clone().enqueue(callBackKt)
        return callBackKt.result
    }

    /**
     * [retry] method is retry last call
     * */
    fun retry(): MutableLiveData<Resource<T>> = makeCall(call)

    fun requestMore(call: Call<T>): MutableLiveData<Resource<T>> {
        this.call = call
        val callBackKt = CallBackKt<T>()
        this.call.clone().enqueue(callBackKt)
        return callBackKt.result
    }

    /**
     * This method is cancel the call
     * */
    fun cancel() {
        if (::call.isInitialized) {
            call.cancel()
        }
    }
}

/**
 * Common network callback method to handle the response
 * */
class CallBackKt<T> : Callback<T>, NetworkCall<T>() {
    var result: MutableLiveData<Resource<T>> = MutableLiveData()

    /**
     * This method handle network call failure
     * */
    override fun onFailure(call: Call<T>, t: Throwable) {
        when (t) {
            is NoNetworkException -> {
                result.value = Resource.error(
                        ApiFailureException(
                                "No internet connection, Please check you mobile data or Wi-fi",
                                null,
                                NO_INTERNET_ERROR_CODE
                        )
                )
            }
            is ConnectException -> {
                result.value = Resource.error(
                        ApiFailureException(
                                "Failed to connect to the server, Please try after some time",
                                null,
                                SERVER_ERROR
                        )
                )
            }

            else -> {
                result.value = Resource.error(ApiFailureException(t.localizedMessage, t, null))
            }
        }

        t.printStackTrace()
    }

    /**
     * This method handle network call success
     **/
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            result.value = Resource.success(response.body())

            val res = response.body() as T

            if (res != null) {
                result.value = Resource.success(response.body())
            } else {
                result.value = Resource.error(
                        ApiFailureException(
                                "Data Not found",
                                null,
                                DATA_NOT_FOUND_ERROR_CODE
                        )
                )
            }
        } else {
            result.value = Resource.error(ApiFailureException(response.errorBody().toString(), null, response.code()))
        }
    }
}
package com.e_comapp.android.network

import com.e_comapp.android.exception.ApiFailureException

/**
 * Class to handle Network response.
 * It either can be Success with the required data or Error with an exception.
 * @param [status] status of the network call which become success, failed or in-progress
 * @param [data] data holds the result of the network call
 * @param [apiError] holds the content if the network call get failed
 * */

class Resource<T> private constructor(
    val status: Status,
    val data: T?,
    val apiError: ApiFailureException?
) {

    companion object {
        /**
         *successful network call return success method
         * */
        fun <T> success(data: T?): Resource<T> =
            Resource(Status.SUCCESS, data, null)

        /**
         * when network call ended with any error return error method
         * */
        fun <T> error(resourceError: ApiFailureException?): Resource<T> =
            Resource(Status.ERROR, null, resourceError)

        /**
         *when the network call is still in progress by various reason like
         *slow internet or retriving and huge data from server returns loading
         * */
        fun <T> loading(data: T?): Resource<T> = Resource(Status.LOADING, data, null)
    }
}
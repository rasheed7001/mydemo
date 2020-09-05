package com.e_comapp.android.liadatautil

import androidx.lifecycle.LiveData

/**
 * Extension function for the live data to make it listen only once
 * */
fun <T> LiveData<T>.toSingleEvent(): LiveData<T> {
    val result = LiveEvent<T>()
    result.addSource(this) {
        result.value = it
    }
    return result
}
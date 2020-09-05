package com.e_comapp.android.utils

import com.google.gson.JsonObject
import java.util.HashMap

fun constructJson(map: HashMap<String, String>): JsonObject {
    val jsonObject = JsonObject()
    try {
        for ((key, value) in map) {
            jsonObject.addProperty(key, value)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return jsonObject
}
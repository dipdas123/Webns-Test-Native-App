package com.dip_int.test_webns.api

import android.annotation.SuppressLint
import android.content.Context
import com.dip_int.test_webns.common.ToastHelper
import com.dip_int.test_webns.common.getAppVersion
import com.dip_int.test_webns.common.getBatteryLevel
import com.dip_int.test_webns.common.getDeviceIMEI
import com.dip_int.test_webns.common.getDeviceName
import com.dip_int.test_webns.common.getDeviceType
import com.dip_int.test_webns.common.getNetworkTypeName
import com.dip_int.test_webns.common.getOsVersion
import com.dip_int.test_webns.model.AccessUser
import com.dip_int.test_webns.model.SendLocationBody
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val apiService = RetrofitClient.createService(ApiService::class.java)
val gson : Gson = Gson()

fun callSendUserLocation(
    locationName: String,
    lat: Double,
    long: Double,
    applicationContext: Context
) {
        val call = apiService.sendUserLocation(sendLocationBodyData(locationName, lat, long, applicationContext))
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    ToastHelper.showSuccessToast(applicationContext, "Location Sent Successfully")

                    println("SuccessResponse :: $response")
                } else {
                    ToastHelper.showSuccessToast(applicationContext, response.message())
                    println("ErrorResponse :: $response")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("API call failed: ${t.message}")
            }
        })
}

@SuppressLint("MissingPermission")
fun sendLocationBodyData(
    locationName: String,
    lat: Double,
    long: Double,
    applicationContext: Context
): SendLocationBody {
    val accessUser = AccessUser(
        token = "5FE791398D1F5A01FCFFC3635767927DCF1203513FFAFBDB1B33466FA4B049A3",
        userName = "DIPE K DAS"
    )

    val body = SendLocationBody(
        accessUser = accessUser,
        deviceBattery = "${getBatteryLevel(applicationContext)}",
        deviceName = getDeviceName(),
        imeiNo = getDeviceIMEI(),
        locationLat = "$lat",
        locationLng = "$long",
        locationName = locationName,
        loginAgent = getAppVersion(context = applicationContext),
        loginOs = "${getDeviceType()}, ${getOsVersion()}",
        mobileNetworkName = getNetworkTypeName(context = applicationContext),
        networkType = getNetworkTypeName(context = applicationContext)
    )

    println("SendLocationBody ::: ${gson.toJson(body)}")
    return body
}
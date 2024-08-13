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
import com.dip_int.test_webns.model.SyncImageResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/*** Created By Dipe K Das on: 13th Aug 2024*/

val gson : Gson = Gson()

fun callSendUserLocation(locationName: String, lat: Double, long: Double, applicationContext: Context) {
    RetrofitClient.createService(baseUrl1, ApiService::class.java)
        .sendUserLocation(sendLocationBodyData(locationName, lat, long, applicationContext))
        .enqueue(object : Callback<SyncImageResponse> {
            override fun onResponse(call: Call<SyncImageResponse>, response: Response<SyncImageResponse>) {
                if (response.isSuccessful) {
                    ToastHelper.showSuccessToast(applicationContext, "${response.body()?.msg}")
                    println("SuccessResponse :: $response")
                } else {
                    ToastHelper.showErrorToast(applicationContext, response.message())
                    println("ErrorResponse :: $response")
                }
            }

            override fun onFailure(call: Call<SyncImageResponse>, t: Throwable) {
                ToastHelper.showErrorToast(applicationContext, "API call failed: ${t.message}")
                println("API call failed: ${t.message}")
            }
        })
}

@SuppressLint("MissingPermission")
fun sendLocationBodyData(locationName: String, lat: Double, long: Double, applicationContext: Context): SendLocationBody {
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




fun callSyncImages(file: File, fileRefCode: String, fileRefName: String, applicationContext: Context) {
    // Create RequestBody for fileRefCode and fileRefName
    val fileRefCodeRequestBody = fileRefCode.toRequestBody(MultipartBody.FORM)
    val fileRefNameRequestBody = fileRefName.toRequestBody(MultipartBody.FORM)

    // Create MultipartBody.Part for the image file
    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

    RetrofitClient.createService(baseUrl2, ApiService::class.java)
        .callSyncImage(fileRefCodeRequestBody, fileRefNameRequestBody, imagePart)
        .enqueue(object : Callback<SyncImageResponse> {
        override fun onResponse(call: Call<SyncImageResponse>, response: Response<SyncImageResponse>) {
            if (response.isSuccessful) {
                ToastHelper.showSuccessToast(applicationContext, "${response.body()?.msg}")
                println("SuccessResponse :: $response")
            } else {
                ToastHelper.showErrorToast(applicationContext, response.message())
                println("ErrorResponse :: $response")
            }
        }

        override fun onFailure(call: Call<SyncImageResponse>, t: Throwable) {
            // Handle network errors
            ToastHelper.showErrorToast(applicationContext, "Api Call Failed: ${t.message}")
            println("Upload error: ${t.message}")
        }
    })
}
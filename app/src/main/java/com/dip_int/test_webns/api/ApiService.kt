package com.dip_int.test_webns.api

import com.dip_int.test_webns.model.SendLocationBody
import com.dip_int.test_webns.model.SyncImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/*** Created By Dipe K Das on: 13th Aug 2024*/

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST(sendLocation)
    fun sendUserLocation(@Body body: SendLocationBody): Call<SyncImageResponse>

    @Multipart
    @POST(syncImage)
    fun callSyncImage(
        @Part("fileRefCode") fileRefCode: RequestBody,
        @Part("fileRefName") fileRefName: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<SyncImageResponse>
}
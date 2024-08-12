package com.dip_int.test_webns.api

import com.dip_int.test_webns.model.SendLocationBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST(sendLocation)
    fun sendUserLocation(@Body body: SendLocationBody): Call<Void>
}
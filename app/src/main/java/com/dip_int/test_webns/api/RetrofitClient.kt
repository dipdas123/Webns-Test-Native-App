package com.dip_int.test_webns.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Lazy initialization of Retrofit instance
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Generic function to create services
    fun <T> createService(serviceClass: Class<T>): T {
        return instance.create(serviceClass)
    }
}

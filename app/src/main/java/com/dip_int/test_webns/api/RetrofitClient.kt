package com.dip_int.test_webns.api

import android.util.Log.VERBOSE
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*** Created By Dipe K Das on: 13th Aug 2024*/

object RetrofitClient {

    private var retrofit: Retrofit? = null
    private var currentBaseUrl: String? = null

    private fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null || currentBaseUrl != baseUrl) {

            val client = OkHttpClient.Builder().addInterceptor(
                LoggingInterceptor.Builder()
                .setLevel(Level.BASIC)
                .log(VERBOSE)
                .build())
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            currentBaseUrl = baseUrl
        }
        return retrofit!!
    }

    fun <T> createService(baseUrl: String, serviceClass: Class<T>): T {
        return getClient(baseUrl).create(serviceClass)
    }
}

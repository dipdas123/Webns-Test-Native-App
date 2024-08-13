package com.dip_int.test_webns.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

/*** Created By Dipe K Das on: 13th Aug 2024*/

interface LocationClient {
    fun getLocationUpdate(interval: Long) : Flow<Location>

    class  LocationException(message: String) : Exception()
}
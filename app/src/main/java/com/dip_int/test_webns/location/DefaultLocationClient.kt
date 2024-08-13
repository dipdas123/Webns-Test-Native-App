package com.dip_int.test_webns.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/*** Created By Dipe K Das on: 13th Aug 2024*/

class DefaultLocationClient(private val  context: Context, private  val client: FusedLocationProviderClient) : LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdate(interval: Long): Flow<Location> {
        return callbackFlow {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled  = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnabled && !isNetworkEnabled) {
                Toast.makeText(context, "GPS is Disabled", Toast.LENGTH_LONG).show()
                throw LocationClient.LocationException("GPS is Disabled")
            }

            val requset = LocationRequest.create().setInterval(interval).setFastestInterval(interval)
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch { send(location) }
                    }
                }
            }

            client.requestLocationUpdates(requset, locationCallback, Looper.getMainLooper())

            awaitClose{
                client.removeLocationUpdates(locationCallback)
            }

        }
    }


}
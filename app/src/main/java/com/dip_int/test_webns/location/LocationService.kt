package com.dip_int.test_webns.location

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import android.app.NotificationManager
import android.content.Context
import android.location.Geocoder
import androidx.core.app.NotificationCompat
import com.dip_int.test_webns.R
import com.dip_int.test_webns.api.callSendUserLocation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Locale

class LocationService : Service() {

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }


    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient


    override fun onBind(intent: Intent?): IBinder? {
        return  null
    }





    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
//            .getLocationUpdate(10000L)
            .getLocationUpdate(300000L)  // 5 minutes = 300,000 milliseconds
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude
                val long = location.longitude
                val locationName = getLocationName(lat, long)
                val updatedNotification = notification.setContentText("Location: (${"Lat : $lat"}, ${"Long : $long, Address: $locationName"})")
                notificationManager.notify(1, updatedNotification.build())

                callSendUserLocation(locationName, lat, long, applicationContext)
            }.launchIn(serviceScope)
        startForeground(1, notification.build())
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    private fun getLocationName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                // You can customize this based on what you want to display
                "${address.locality}, ${address.countryName}"
            } else {
                "Unknown Location"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown Location"
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

}
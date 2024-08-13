package com.dip_int.test_webns.screens.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dip_int.test_webns.MainActivity
import com.dip_int.test_webns.R
import com.dip_int.test_webns.common.BackgroundLocationGranted
import com.dip_int.test_webns.common.SharedPreferencesHelper
import com.dip_int.test_webns.common.backgroundLocationRunning
import com.dip_int.test_webns.location.LocationService

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE = 1002
        const val REQUEST_PERMISSION_CODE = 1003
    }
    var backgroundLocationPermissionGranted = false

    override fun onResume() {
        super.onResume()
        println("onResume >> SplashActivity")

//        if (SharedPreferencesHelper.getBoolean(this, BackgroundLocationGranted, )) {
//            gotoMainActivity()
//        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        backgroundLocationRunning = false

        /// Permission Check
        if (isNetworkAvailable(this)) {
            if (isLocationEnabled()) {
                checkAndRequestPermissions()
            } else {
                //checkAndPromptEnableLocation()
            }
        } else {
            enableInternetConnectionDialog()
        }

        if (SharedPreferencesHelper.getBoolean(this, BackgroundLocationGranted, )) {
            gotoMainActivity()
        } else {
            /// Start Background Location Service
            if (isNetworkAvailable(this)) {
                if (isLocationEnabled()) {
                    backgroundLocationRunning = true
                    Toast.makeText(this, "Background Location Service Running...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, LocationService::class.java)
                    intent.action = LocationService.ACTION_START
                    startService(intent)
                } else {
                    checkAndPromptEnableLocation()
                }
            } else {
                enableInternetConnectionDialog()
            }
        }
    }






    /// Check Internet
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }


    /// Enabling My Location
    private fun checkAndPromptEnableLocation() {
        if (!isLocationEnabled()) {
            enableMyLocationDialog()
        } else {
            // Location services are already enabled
            println("Location services are already enabled")
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun promptEnableLocation() {
        val locationRequestIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(locationRequestIntent)
    }


    // Permission
    private fun checkAndRequestPermissions(): Boolean {
        val permissionsToCheck = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }

        val listPermissionsNeeded = permissionsToCheck.filter { permission ->
            when {
                permission == android.Manifest.permission.READ_EXTERNAL_STORAGE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> false
                permission == android.Manifest.permission.WRITE_EXTERNAL_STORAGE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> false
                permission == android.Manifest.permission.POST_NOTIFICATIONS && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> false
                else -> ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
            }
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            if (listPermissionsNeeded.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }) {
                // Show an explanation to the user
                showPermissionExplanationDialog(listPermissionsNeeded)
            } else {
                // Request the permissions
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_PERMISSION_CODE)
            }
            return false
        }

        return true
    }

    private fun checkAndRequestBackgroundPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE)
        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for Background Location Permission
            backgroundLocationDialog()
        } else {
            // Permissions are granted, continue with location-related work

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                println("REQUEST_PERMISSION_CODE ::: $REQUEST_PERMISSION_CODE")
                val permissionsDenied = permissions.zip(grantResults.toList())
                    .filter { it.second != PackageManager.PERMISSION_GRANTED }
                if (permissionsDenied.isNotEmpty()) {
                    // Re-request the permissions that were not granted
                    checkAndRequestPermissions()
                } else {
                    checkAndRequestBackgroundPermissions()
                }
            }

            REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE -> {
                println("BACKGROUND_LOCATION ::: $REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE")
                backgroundLocationPermissionGranted = grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED
                if (backgroundLocationPermissionGranted) {
                    println("Background location permission granted")
                    SharedPreferencesHelper.putBoolean(this, BackgroundLocationGranted, true)
                    gotoMainActivity()
                } else {
                    checkAndRequestBackgroundPermissions()
                    println("Background location permission denied")
                }
            }

            else -> {
                // Handle other request codes if needed
            }
        }
    }

    private fun gotoMainActivity() {
        Looper.myLooper()?.let { it1 ->
            Handler(it1).postDelayed(Runnable {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000)
        }
    }

    private fun showPermissionExplanationDialog(permissionsNeededList: List<String>) {
        val message = buildPermissionExplanationMessage(permissionsNeededList)
        AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage(message)
            .setPositiveButton("Grant") {dialog, _ ->
                // Re-request the permissions
                dialog.dismiss()
                ActivityCompat.requestPermissions(this, permissionsNeededList.toTypedArray(), REQUEST_PERMISSION_CODE)
            }
            .setNegativeButton("Cancel") {dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Settings") {dialog, _ ->
                // Open app settings
                dialog.dismiss()
                openApplicationMainSettingsDialog()
            }
            .create()
            .show()
    }

    private fun buildPermissionExplanationMessage(permissionsNeeded: List<String>): String {
        val permissionReasons = permissionsNeeded.map {
            when (it) {
                android.Manifest.permission.CAMERA -> "Access to the camera is needed to take pictures or videos."
                android.Manifest.permission.READ_EXTERNAL_STORAGE -> "Reading external storage is needed to access photos, videos, or files."
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE -> "Writing to external storage is needed to save photos, videos, or files."
                android.Manifest.permission.ACCESS_FINE_LOCATION -> "Fine location access is needed to provide accurate location-based services."
                android.Manifest.permission.ACCESS_COARSE_LOCATION -> "Coarse location access is needed to provide approximate location-based services."
                android.Manifest.permission.POST_NOTIFICATIONS -> "Notification permission is needed to send you alerts and updates."
                else -> "This permission is needed for the app to function properly."
            }
        }

        return "The app requires the following permissions:\n\n" + permissionReasons.joinToString("\n\n") +
                "\n\nPlease grant them for a better experience."
    }


    // Dialogs
    private fun enableMyLocationDialog() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Location Services Required")
            .setMessage("To provide location-based features, we need access to your location. Please enable location services (GPS) in your device settings.")
            .setPositiveButton("Go to Settings") { dialog, _ ->
                dialog.dismiss()
                promptEnableLocation()
            }.show()
    }

    private fun enableInternetConnectionDialog() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Internet Connection Required")
            .setMessage("To provide online features, we need an active internet connection. Please enable Wi-Fi in your device settings.")
            .setPositiveButton("Turn On WIFI") { dialog, _ ->
                dialog.dismiss()
                promptEnableInternet()
            }
            .show()
    }

    private fun promptEnableInternet() {
        // val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        //startActivity(intent)
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        startActivity(intent)
    }

    // Permission Dialogs
    private fun openApplicationMainSettingsDialog() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("All the permissions must approve")
            .setMessage("All the permissions must approve for apps smooth functionality")
            .setPositiveButton("Go to Settings") { dialog, _ ->
                // Open the app's settings
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.setData(uri)
                startActivity(intent)
            }.show()
    }

    private fun backgroundLocationDialog() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Background Location Access Required 'Allow all the time' ")
            .setMessage("To provide location-based features even when the app is in the background, we need permission to access your location all the time. Please enable 'Allow all the time' in your device settings.")
            .setPositiveButton("Go to Settings") { dialog, _ ->
                // Open ACCESS_BACKGROUND_LOCATION
                dialog.dismiss()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION), REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE)
                }
            }.show()
    }
    // Permission Dialogs

}
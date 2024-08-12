package com.dip_int.test_webns.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.telephony.TelephonyManager
import java.util.UUID

fun getDeviceIMEI(): String {
    return UUID.randomUUID().toString()
}

fun getDeviceType(): String {
    return "Android"
}

fun getDeviceName(): String {
    return Build.MODEL
}

fun getOsVersion(): String {
    val fields = Build.VERSION_CODES::class.java.fields
    return fields[Build.VERSION.SDK_INT].name
}

fun getAppVersion(context: Context): String {
    try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val version = pInfo.versionName
        val number = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pInfo.longVersionCode
        } else {
            pInfo.versionCode
        }
        return "$version $number"
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return ""
}

@SuppressLint("MissingPermission")
fun getNetworkTypeName(context: Context): String {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return "No Network"
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return "Unknown"

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                getCellularNetworkTypeName(telephonyManager.networkType)
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Unknown"
        }
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return "No Network"
        return if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
            "WIFI"
        } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            getCellularNetworkTypeName(telephonyManager.networkType)
        } else {
            "Unknown"
        }
    }
}

fun getCellularNetworkTypeName(networkType: Int): String {
    return when (networkType) {
        TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
        TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
        TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
        TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA"
        TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVDO 0"
        TelephonyManager.NETWORK_TYPE_EVDO_A -> "EVDO A"
        TelephonyManager.NETWORK_TYPE_1xRTT -> "1xRTT"
        TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA"
        TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA"
        TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
        TelephonyManager.NETWORK_TYPE_IDEN -> "iDen"
        TelephonyManager.NETWORK_TYPE_EVDO_B -> "EVDO B"
        TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
        TelephonyManager.NETWORK_TYPE_EHRPD -> "eHRPD"
        TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPA+"
        TelephonyManager.NETWORK_TYPE_GSM -> "GSM"
        TelephonyManager.NETWORK_TYPE_TD_SCDMA -> "TD-SCDMA"
        TelephonyManager.NETWORK_TYPE_IWLAN -> "IWLAN"
        TelephonyManager.NETWORK_TYPE_NR -> "5G"
        else -> "Unknown"
    }
}

fun getBatteryLevel(context: Context): Int {
    val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    return if (batteryIntent != null) {
        val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        (level * 100) / scale
    } else {
        -1 // Indicates an error in retrieving the battery level
    }
}
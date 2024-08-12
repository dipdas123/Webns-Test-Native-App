package com.dip_int.test_webns.model

data class AccessUser(
    val token: String = "5FE791398D1F5A01FCFFC3635767927DCF1203513FFAFBDB1B33466FA4B049A3",
    val userName: String = "DIPE K DAS"
)

data class SendLocationBody(
    val accessUser: AccessUser,
    val deviceBattery: String,
    val deviceName: String,
    val imeiNo: String,
    val locationLat: String,
    val locationLng: String,
    val locationName: String,
    val loginAgent: String,
    val loginOs: String,
    val mobileNetworkName: String,
    val networkType: String
)
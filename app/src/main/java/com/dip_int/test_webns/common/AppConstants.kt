package com.dip_int.test_webns.common

import android.app.NotificationManager

/*** Created By Dipe K Das on: 13th Aug 2024*/

var firstTimeOpeningApp = true
var backgroundLocationRunning = false
var BackgroundLocationGranted = "BackgroundLocationGranted"

//var  notificationImportance =  NotificationManager.IMPORTANCE_LOW
var  notificationImportance =  NotificationManager.IMPORTANCE_HIGH

//var  notificationInterval =  10000L // 10000L // 10sec
var  notificationInterval =  300000L // 300000L // 5min

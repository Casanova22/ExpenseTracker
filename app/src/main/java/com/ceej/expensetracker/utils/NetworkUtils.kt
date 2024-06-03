package com.ceej.expensetracker.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

fun Context.isConnectionAvailable(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(network)
        capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    } else {
        @Suppress("DEPRECATION")
        val networkInfo = cm.activeNetworkInfo
        networkInfo != null && networkInfo.isConnectedOrConnecting
    }
}
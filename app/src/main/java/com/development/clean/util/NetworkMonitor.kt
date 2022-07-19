package com.development.clean.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.development.clean.util.extension.isInternetAvailable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@Suppress("MemberVisibilityCanBePrivate")
@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    val isConnected: Boolean
        get() {
            return applicationContext.isInternetAvailable
        }

    val connectStateFlow = callbackFlow {

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
                super.onLost(network)
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val connectivityManager =
            applicationContext.getSystemService(ConnectivityManager::class.java)

        connectivityManager.registerNetworkCallback(networkRequest, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.cancellable()
        .onStart { emit(isConnected) }
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), isConnected)
}
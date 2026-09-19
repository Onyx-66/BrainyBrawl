package com.brainybrawl.app.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class ConnectivityMonitor(context:Context,scope:CoroutineScope){
    private val manager=context.applicationContext.getSystemService(ConnectivityManager::class.java)
    private fun connected()=manager.getNetworkCapabilities(manager.activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)==true
    val online:StateFlow<Boolean> = callbackFlow {
        val callback=object:ConnectivityManager.NetworkCallback(){
            override fun onCapabilitiesChanged(network:Network,capabilities:NetworkCapabilities){trySend(connected())}
            override fun onLost(network:Network){trySend(connected())}
        }
        manager.registerDefaultNetworkCallback(callback)
        trySend(connected())
        awaitClose{manager.unregisterNetworkCallback(callback)}
    }.distinctUntilChanged().stateIn(scope,SharingStarted.Eagerly,connected())
}

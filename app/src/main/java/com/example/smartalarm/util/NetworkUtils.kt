package com.example.smartalarm.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for monitoring network connectivity.
 */
@Singleton
class NetworkUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!
    
    /**
     * Checks if the device is currently connected to the internet.
     */
    val isConnected: Boolean
        get() = checkNetworkConnection()
    
    /**
     * A flow that emits the current network connectivity status.
     * Emits `true` when connected to the internet, `false` otherwise.
     */
    val connectionState: Flow<Boolean> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(checkNetworkConnection(network))
            }
            
            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(false)
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                trySend(checkNetworkConnection(network))
            }
        }
        
        // Register the network callback
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(request, networkCallback)
        
        // Set initial state
        trySend(isConnected)
        
        // Unregister the callback when the flow is cancelled
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged()
    
    /**
     * A flow that emits the current network type (e.g., "WiFi", "Cellular", "Offline").
     */
    val networkType: Flow<String> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(getNetworkType(network))
            }
            
            override fun onLost(network: Network) {
                super.onLost(network)
                trySend("Offline")
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                trySend(getNetworkType(network))
            }
        }
        
        // Register the network callback
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(request, networkCallback)
        
        // Set initial state
        trySend(if (isConnected) getNetworkType() else "Offline")
        
        // Unregister the callback when the flow is cancelled
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged()
    
    /**
     * Checks if the device is connected to a metered network.
     */
    val isMeteredNetwork: Flow<Boolean> = connectionState.map { isConnected ->
        if (!isConnected) {
            false
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.isActiveNetworkMetered
        } else {
            // For older versions, we'll assume WiFi is unmetered and mobile data is metered
            val activeNetwork = connectivityManager.activeNetworkInfo
            activeNetwork?.type == ConnectivityManager.TYPE_MOBILE
        }
    }.distinctUntilChanged()
    
    /**
     * Checks if the device is connected to a VPN.
     */
    val isVpnConnected: Boolean
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networks = connectivityManager.allNetworks
                for (network in networks) {
                    val caps = connectivityManager.getNetworkCapabilities(network)
                    if (caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true) {
                        return true
                    }
                }
                return false
            } else {
                // For older versions, we can't reliably detect VPN
                return false
            }
        }
    
    /**
     * Gets the type of the current network connection.
     * @param network The network to check, or null to check the active network.
     * @return The network type as a string (e.g., "WiFi", "Cellular", "Ethernet", "VPN", "Offline")
     */
    private fun getNetworkType(network: Network? = null): String {
        val capabilities = if (network != null) {
            connectivityManager.getNetworkCapabilities(network)
        } else {
            val activeNetwork = connectivityManager.activeNetwork
            activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
        } ?: return "Offline"
        
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "Bluetooth"
            else -> "Unknown"
        }
    }
    
    /**
     * Checks if the device is currently connected to the internet.
     * @param network The network to check, or null to check the active network.
     * @return true if the device is connected to the internet, false otherwise.
     */
    private fun checkNetworkConnection(network: Network? = null): Boolean {
        val capabilities = if (network != null) {
            connectivityManager.getNetworkCapabilities(network)
        } else {
            val activeNetwork = connectivityManager.activeNetwork
            activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
        } ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
    }
}

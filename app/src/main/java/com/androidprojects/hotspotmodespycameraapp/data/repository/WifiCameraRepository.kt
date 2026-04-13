package com.androidprojects.hotspotmodespycameraapp.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.annotation.RequiresApi
import com.androidprojects.hotspotmodespycameraapp.domain.model.ConnectionState
import com.androidprojects.hotspotmodespycameraapp.domain.repository.CameraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@RequiresApi(Build.VERSION_CODES.Q)
class WifiCameraRepository(
    private val context: Context
) : CameraRepository {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    override val connectionState: StateFlow<ConnectionState> = _connectionState

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var connectedNetwork: Network? = null

    override fun connectToCameraHotspot() {
        _connectionState.value = ConnectionState.Connecting

        val specifier = WifiNetworkSpecifier.Builder()
            .setSsid("XIAO_CAM_AUDIO")
            .setWpa2Passphrase("12345678")
            .build()

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .setNetworkSpecifier(specifier)
            .build()

        networkCallback?.let {
            try {
                connectivityManager.unregisterNetworkCallback(it)
            } catch (_: Exception) {
            }
        }

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                connectedNetwork = network
                connectivityManager.bindProcessToNetwork(network)
                _connectionState.value = ConnectionState.Connected("XIAO_CAM_AUDIO")
            }

            override fun onUnavailable() {
                _connectionState.value = ConnectionState.Error("Unable to connect to camera hotspot")
            }

            override fun onLost(network: Network) {
                if (connectedNetwork == network) {
                    connectedNetwork = null
                    connectivityManager.bindProcessToNetwork(null)
                    _connectionState.value = ConnectionState.Error("Camera Wi-Fi connection lost")
                }
            }
        }

        connectivityManager.requestNetwork(request, networkCallback!!)
    }

    override fun disconnect() {
        try {
            networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        } catch (_: Exception) {
        }
        connectivityManager.bindProcessToNetwork(null)
        connectedNetwork = null
        _connectionState.value = ConnectionState.Idle
    }
}
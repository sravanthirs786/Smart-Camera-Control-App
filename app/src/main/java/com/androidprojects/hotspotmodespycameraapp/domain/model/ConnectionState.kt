package com.androidprojects.hotspotmodespycameraapp.domain.model
sealed class ConnectionState {
    data object Idle : ConnectionState()
    data object Connecting : ConnectionState()
    data class Connected(val ssid: String) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}
package com.androidprojects.hotspotmodespycameraapp.domain.repository

import com.androidprojects.hotspotmodespycameraapp.domain.model.ConnectionState
import kotlinx.coroutines.flow.StateFlow

interface CameraRepository {
    val connectionState: StateFlow<ConnectionState>
    fun connectToCameraHotspot()
    fun disconnect()
}
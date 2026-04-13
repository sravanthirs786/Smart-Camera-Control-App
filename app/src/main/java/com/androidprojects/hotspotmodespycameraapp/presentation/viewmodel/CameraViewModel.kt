package com.androidprojects.hotspotmodespycameraapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.androidprojects.hotspotmodespycameraapp.domain.repository.CameraRepository

class CameraViewModel(
    private val repository: CameraRepository
) : ViewModel() {

    val connectionState = repository.connectionState

    fun connectToCamera() {
        repository.connectToCameraHotspot()
    }

    fun disconnect() {
        repository.disconnect()
    }

    class Factory(
        private val repository: CameraRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CameraViewModel(repository) as T
        }
    }
}
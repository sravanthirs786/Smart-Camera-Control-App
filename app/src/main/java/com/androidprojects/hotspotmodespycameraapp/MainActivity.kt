package com.androidprojects.hotspotmodespycameraapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidprojects.hotspotmodespycameraapp.data.repository.WifiCameraRepository
import com.androidprojects.hotspotmodespycameraapp.presentation.ui.CameraScreen
import com.androidprojects.hotspotmodespycameraapp.presentation.viewmodel.CameraViewModel
import com.androidprojects.hotspotmodespycameraapp.ui.theme.SpyCameraTheme
import android.content.pm.ActivityInfo
import android.content.res.Configuration

@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : ComponentActivity() {

    private val viewModel: CameraViewModel by viewModels {
        CameraViewModel.Factory(
            WifiCameraRepository(this)
        )
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setContent {
            SpyCameraTheme {
                val state = viewModel.connectionState.collectAsStateWithLifecycle()

                CameraScreen(
                    connectionState = state.value,
                    onConnectClick = { viewModel.connectToCamera() },
                    onDisconnectClick = { viewModel.disconnect() }
                )
            }
        }
    }

    private fun updateOrientationBehavior(isConnected: Boolean) {
        requestedOrientation = if (isConnected) {
            ActivityInfo.SCREEN_ORIENTATION_FULL_USER
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}
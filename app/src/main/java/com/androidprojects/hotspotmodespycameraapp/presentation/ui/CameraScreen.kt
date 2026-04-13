package com.androidprojects.hotspotmodespycameraapp.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidprojects.hotspotmodespycameraapp.domain.model.ConnectionState
import com.androidprojects.hotspotmodespycameraapp.ui.theme.CardDark
import com.androidprojects.hotspotmodespycameraapp.ui.theme.TextPrimary
import com.androidprojects.hotspotmodespycameraapp.ui.theme.TextSecondary

@Composable
fun CameraScreen(
    connectionState: ConnectionState,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        if (connectionState is ConnectionState.Connected && isLandscape) {
            FullScreenCameraView(
                onDisconnectClick = onDisconnectClick
            )
        } else {
            PortraitCameraView(
                connectionState = connectionState,
                onConnectClick = onConnectClick,
                onDisconnectClick = onDisconnectClick
            )
        }
    }
}

@Composable
private fun PortraitCameraView(
    connectionState: ConnectionState,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "ESP32 Spy Camera",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Connect to the ESP32 hotspot and watch the live feed.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatusCard(connectionState)

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onConnectClick,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Connect")
            }

            OutlinedButton(
                onClick = onDisconnectClick,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Disconnect")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (connectionState is ConnectionState.Connected) {
                    CameraWebView(url = "http://192.168.4.1/")
                } else {
                    Text(
                        text = "Live camera preview will appear here",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun FullScreenCameraView(
    onDisconnectClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CameraWebView(url = "http://192.168.4.1/")

        OutlinedButton(
            onClick = onDisconnectClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Disconnect")
        }
    }
}

@Composable
private fun StatusCard(connectionState: ConnectionState) {
    val text = when (connectionState) {
        ConnectionState.Idle -> "Not connected"
        ConnectionState.Connecting -> "Connecting to XIAO_CAM_AUDIO..."
        is ConnectionState.Connected -> "Connected to ${connectionState.ssid}"
        is ConnectionState.Error -> "Error: ${connectionState.message}"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TextPrimary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
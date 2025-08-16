package com.rinne.libraries.map.core.deprecated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rinne.libraries.map.core.model.RinneMapCameraPosition
import com.rinne.libraries.map.core.model.RinneLatLng

@Composable
fun rememberRinneGoogleMapState() = remember { RinneGoogleMapState() }

class RinneGoogleMapState {
    var markers = mutableStateListOf<RinneLatLng>()
    var cameraPosition by mutableStateOf<RinneMapCameraPosition?>(null)
    var selectedPosition by mutableStateOf<RinneLatLng?>(null)
    var showUserLocationOnMap by mutableStateOf(false)

    fun setMarkers(newMarkers: List<RinneLatLng>) {
        markers.clear()
        markers.addAll(newMarkers)
    }

    fun setCameraPosition(latLng: RinneLatLng, zoom: Float) {
        cameraPosition = RinneMapCameraPosition(latLng, zoom)
    }
}
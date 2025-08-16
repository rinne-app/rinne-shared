package com.rinne.libraries.map.mapbox

import com.rinne.libraries.map.core.RinneMapActionsManager
import com.rinne.libraries.map.core.RinneMapManager
import com.rinne.libraries.map.core.model.RinneLatLng
import com.rinne.libraries.map.core.model.RinneMapCameraPosition
import com.rinne.libraries.map.core.model.RinneMapState
import kotlinx.coroutines.flow.MutableStateFlow

class RinneMapBoxMapManager : RinneMapManager {
    //TODO change default state
    override val mapStateFlow = MutableStateFlow(
        RinneMapState(
            RinneMapCameraPosition(RinneLatLng(0.0, 0.0), 0f),
            false,
            emptyList(),
        )
    )
    override val actionsManager: RinneMapActionsManager
        get() = TODO("Not yet implemented")

    override fun moveCameraPosition(cameraPosition: RinneMapCameraPosition) {

    }
}
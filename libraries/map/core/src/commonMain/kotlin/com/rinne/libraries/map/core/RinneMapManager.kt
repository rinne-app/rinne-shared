package com.rinne.libraries.map.core

import com.rinne.libraries.map.core.model.RinneMapCameraPosition
import com.rinne.libraries.map.core.model.RinneMapState
import kotlinx.coroutines.flow.StateFlow

interface RinneMapManager {
    val mapStateFlow: StateFlow<RinneMapState>
    val actionsManager: RinneMapActionsManager

    fun moveCameraPosition(newCameraPosition: RinneMapCameraPosition)
}
package com.rinne.libraries.map.core.model

data class RinneMapState(
    val cameraPosition: RinneMapCameraPosition,
    val userLocationVisible: Boolean,
    val actions: List<RinneMapAction>
)

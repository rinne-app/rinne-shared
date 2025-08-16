package com.rinne.libraries.map.core

import androidx.compose.ui.graphics.ImageBitmap
import com.rinne.libraries.map.core.model.RinneMapCameraPosition

interface RinneMapSnapshotManager {
    fun createSnapshot(cameraPosition: RinneMapCameraPosition): ImageBitmap
}
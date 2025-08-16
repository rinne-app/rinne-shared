package com.rinne.libraries.map.core.model

sealed interface RinneMapAction {
    data object Marker : RinneMapAction
}

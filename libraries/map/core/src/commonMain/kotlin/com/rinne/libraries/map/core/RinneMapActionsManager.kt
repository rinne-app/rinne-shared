package com.rinne.libraries.map.core

import com.rinne.libraries.map.core.model.RinneMapAction
import kotlinx.coroutines.flow.StateFlow

interface RinneMapActionsManager {
    val actionsStateFlow: StateFlow<List<RinneMapAction>>

    fun addActions(actions: List<RinneMapAction>)
    fun removeActions(actions: List<RinneMapAction>)
    fun clearAllActions()
}
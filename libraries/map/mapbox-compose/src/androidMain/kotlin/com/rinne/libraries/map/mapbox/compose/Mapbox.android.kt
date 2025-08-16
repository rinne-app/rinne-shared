package com.rinne.libraries.map.mapbox.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.GenericStyle
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.compose.style.StyleColorTheme
import com.mapbox.maps.extension.compose.style.rememberStyleState
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyleExperimental
import com.mapbox.maps.extension.compose.style.standard.rememberExperimentalStandardStyleState

@Composable
actual fun RinneMapbox(modifier: Modifier) {
    var initialized by remember { mutableStateOf(false) }
    val darkTheme = isSystemInDarkTheme()
    val mapStyle by remember(darkTheme) {
        mutableStateOf(
            when (darkTheme) {
                true -> "mapbox://styles/tomrinne/cm85wkpfe006n01sbesil50og"
                false -> "mapbox://styles/tomrinne/cm85wjqmw006e01qrask45i4g"
            }
        )
    }

    LaunchedEffect(Unit) {
        //TODO
        MapboxOptions.accessToken =
            "pk.eyJ1IjoidG9tcmlubmUiLCJhIjoiY202bTJ2dWY4MGQwejJrcjVmNHFvODNmciJ9.9RaNl_gZTdr7BAyrC_q8uA"
        initialized = true
    }

    if (initialized) MapboxMap(
        modifier = modifier.fillMaxSize(),
        style = { MapStyle(style = mapStyle) },
//        style = {
//            MapboxStandardStyleExperimental(
//                experimentalStandardStyleState = rememberExperimentalStandardStyleState {
//
//                    interactionsState.onBuildingsClicked { building, _ ->
//                        building.setStandardBuildingsState {
//                            highlight(true)
//                        }
//                        true
//                    }
//
////                interactionsState { poi, context ->
////                    Log.d("Tap at poi:", poi.name!!)
////                    true
//                },
////            styleImportsContent = { StyleImport("1", mapStyle, null) }
//            )
//
////            MapStyle(style = mapStyle)
//        },
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(12.0)
                center(Point.fromLngLat(30.741259237924442, 46.485378465832774))
                pitch(0.0)
                bearing(0.0)
            }
        },
        scaleBar = {},
        logo = {},
        attribution = {},
    )
}
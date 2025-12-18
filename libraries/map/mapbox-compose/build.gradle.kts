import com.rinne.extensions.rinneAndroid

plugins {
    alias(libs.plugins.rinne.multiplatform.library)
    alias(libs.plugins.rinne.multiplatform.compose)
}


rinneAndroid("com.rinne.libraries.map.mapbox.compose")

kotlin {
    sourceSets.commonMain.dependencies {
        api(projects.rinneShared.libraries.map.core)
        api(projects.rinneShared.libraries.map.mapbox)
    }

    sourceSets.androidMain.dependencies {
        implementation(libs.mapbox.android)
        implementation(libs.mapbox.android.compose)
    }
}

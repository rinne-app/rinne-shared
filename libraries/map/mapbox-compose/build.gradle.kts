import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.compose)
}

kotlin {
    rinneAndroid("com.rinne.libraries.map.mapbox.compose")
    sourceSets.commonMain.dependencies {
        api(projects.rinneShared.libraries.map.core)
        api(projects.rinneShared.libraries.map.mapbox)
    }

    sourceSets.androidMain.dependencies {
        implementation(libs.mapbox.android)
        implementation(libs.mapbox.android.compose)
    }
}

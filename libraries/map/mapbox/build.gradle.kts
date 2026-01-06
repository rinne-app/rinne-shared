import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.compose)
}

kotlin {
    rinneAndroid("com.rinne.libraries.map.mapbox")
    sourceSets.commonMain.dependencies {
        api(projects.rinneShared.libraries.map.core)

    }
    sourceSets.androidMain.dependencies {
        implementation(libs.mapbox.android)
        implementation(libs.mapbox.android.compose)
    }
}

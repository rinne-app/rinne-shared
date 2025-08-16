plugins {
    alias(libs.plugins.rinne.multiplatform.library)
    alias(libs.plugins.rinne.multiplatform.compose)
}


android {
    namespace = "com.rinne.libraries.map.mapbox"
}

kotlin {
    sourceSets.commonMain.dependencies {
        api(projects.libraries.map.core)

    }
    sourceSets.androidMain.dependencies {
        implementation(libs.mapbox.android)
        implementation(libs.mapbox.android.compose)
    }
}

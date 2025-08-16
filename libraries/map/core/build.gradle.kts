plugins {
    alias(libs.plugins.rinne.multiplatform.library)
    alias(libs.plugins.rinne.multiplatform.compose)
}


android {
    namespace = "com.rinne.libraries.map.core"
}

kotlin {
    sourceSets.commonMain.dependencies {}
}

plugins {
    alias(libs.plugins.rinne.multiplatform.library)
    alias(libs.plugins.rinne.multiplatform.koin)
}


android {
    namespace = "com.rinne.libraries.di.core"
}

kotlin {
    sourceSets.commonMain.dependencies {}
}

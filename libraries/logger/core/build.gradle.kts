plugins {
    alias(libs.plugins.rinne.multiplatform.library)
    alias(libs.plugins.rinne.multiplatform.koin)
    alias(libs.plugins.rinne.multiplatform.ktor)
}


android {
    namespace = "com.rinne.libraries.logger.core"
}

kotlin {
    sourceSets.commonMain.dependencies {}
}

plugins {
    alias(libs.plugins.rinne.multiplatform.library)
}


android {
    namespace = "com.rinne.libraries.buildConfig.core"
}

kotlin {
    sourceSets.commonMain.dependencies {}
}

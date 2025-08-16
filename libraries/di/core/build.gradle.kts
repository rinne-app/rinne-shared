plugins {
    alias(libs.plugins.rinne.multiplatform.library)
}


android {
    namespace = "com.rinne.libraries.di.core"
}

kotlin {
    sourceSets.commonMain.dependencies {}
}

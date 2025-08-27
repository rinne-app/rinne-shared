plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
//    alias(sharedLibs.plugins.rinne.multiplatform.koin)
//    alias(sharedLibs.plugins.rinne.multiplatform.ktor)
}


android {
    namespace = "com.rinne.libraries.logger.core"
}

kotlin {
    sourceSets.commonMain.dependencies {}
}

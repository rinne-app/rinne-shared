import com.rinne.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
//    alias(sharedLibs.plugins.rinne.multiplatform.koin)
//    alias(sharedLibs.plugins.rinne.multiplatform.ktor)
}

rinneAndroid("com.rinne.libraries.logger.core")

kotlin {
    sourceSets.commonMain.dependencies {}
}

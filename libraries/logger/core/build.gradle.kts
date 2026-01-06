import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
//    alias(sharedLibs.plugins.rinne.multiplatform.koin)
//    alias(sharedLibs.plugins.rinne.multiplatform.ktor)
}

kotlin {
    rinneAndroid("com.rinne.libraries.logger.core")
    sourceSets.commonMain.dependencies {}
}

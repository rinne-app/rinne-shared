import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    //TODO
//    alias(libs.plugins.rinne.multiplatform.koin)
}

kotlin {
    rinneAndroid("com.rinne.libraries.di.core")
    sourceSets.commonMain.dependencies {}
}

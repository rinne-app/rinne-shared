import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
}

kotlin {
    rinneAndroid("com.rinne.libraries.serialization.core")
    sourceSets.commonMain.dependencies {}
}

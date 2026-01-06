import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.compose)
}

kotlin {
    rinneAndroid("com.rinne.libraries.text.core")
    sourceSets.commonMain.dependencies {}
}

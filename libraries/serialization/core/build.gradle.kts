import com.rinne.extensions.rinneAndroid

plugins {
    alias(libs.plugins.rinne.multiplatform.library)
}


rinneAndroid("com.rinne.libraries.serialization.core")

kotlin {
    sourceSets.commonMain.dependencies {}
}

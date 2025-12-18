import com.rinne.extensions.rinneAndroid

plugins {
    alias(libs.plugins.rinne.multiplatform.library)
}


rinneAndroid("com.rinne.libraries.buildConfig.core")

kotlin {
    sourceSets.commonMain.dependencies {}
}

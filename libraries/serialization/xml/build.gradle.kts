import com.rinne.extensions.rinneAndroid

plugins {
    alias(libs.plugins.rinne.multiplatform.library)
}


rinneAndroid("com.rinne.libraries.serialization.xml")

kotlin {
    sourceSets.commonMain.dependencies {}
}

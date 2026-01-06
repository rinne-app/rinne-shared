import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
}


kotlin {
    rinneAndroid("com.rinne.libraries.date.time")
    sourceSets.commonMain.dependencies {
        implementation(sharedLibs.kotlinx.datetime)
    }
}

import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.kotlinx.serialization)
}


kotlin {
    rinneAndroid("com.rinne.libraries.date.time")
    sourceSets.commonMain.dependencies {
        implementation(sharedLibs.kotlinx.datetime)
        api(sharedLibs.kotlinx.serialization.core)
    }
    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
        implementation(sharedLibs.kotlinx.serialization.json)
    }
}

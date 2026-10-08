import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.kotlin.serialization)
}

kotlin {
    rinneAndroid("com.rinne.shared.network.model")
    sourceSets.commonMain.dependencies {
        // DTOs expose RinneTimestamp, so consumers need it on their compile classpath.
        api(projects.rinneShared.libraries.dateTime.core)
    }
}

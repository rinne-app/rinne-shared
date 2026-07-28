import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
}


kotlin {
    rinneAndroid("com.rinne.libraries.network.client.core")
    sourceSets.commonMain.dependencies {
        implementation(projects.rinneShared.libraries.logger.core)
        implementation(sharedLibs.kotlinx.serialization.json)
    }
}

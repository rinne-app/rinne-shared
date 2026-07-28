import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.ktor.client)
}

kotlin {
    rinneAndroid("com.rinne.libraries.network.client.ktor")
    sourceSets.commonMain.dependencies {
        implementation(projects.rinneShared.libraries.network.client.core)
    }
}

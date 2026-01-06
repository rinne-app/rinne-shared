import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.koin)
    alias(sharedLibs.plugins.rinne.multiplatform.ktor.client)
}


kotlin {
    rinneAndroid("com.rinne.libraries.network.client.core")
    sourceSets.commonMain.dependencies {}
}

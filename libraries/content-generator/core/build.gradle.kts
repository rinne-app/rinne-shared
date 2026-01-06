import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.ktor.client)
}

kotlin {
    rinneAndroid("com.rinne.libraries.content.generator.core")
    sourceSets.commonMain.dependencies {}
}

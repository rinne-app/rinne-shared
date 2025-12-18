import com.rinne.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.ktor.client)
}

rinneAndroid("com.rinne.libraries.content.generator.core")

kotlin {
    sourceSets.commonMain.dependencies {}
}

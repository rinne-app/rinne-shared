import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.kotlin.serialization)
}

kotlin {
    rinneAndroid("com.rinne.shared.network.model")
}
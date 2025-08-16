plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.kotlin.serialization)
}

android {
    namespace = "com.rinne.shared.network.model"
}
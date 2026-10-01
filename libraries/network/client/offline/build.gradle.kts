import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.kotlin.serialization)
}

kotlin {
    rinneAndroid("com.rinne.libraries.network.client.offline")

    sourceSets {
        commonMain.dependencies {
            api(projects.rinneShared.libraries.network.client.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(sharedLibs.kotlinx.coroutines.test)
        }
    }
}

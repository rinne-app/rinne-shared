import com.rinne.extensions.rinneAndroid

// A Compose library: it uses the client's Compose convention (no macOS native target, where the UI is
// SwiftUI). Only the client includes this module.
plugins {
    id("rinne.multiplatform.compose.core")
}

kotlin {
    rinneAndroid("com.rinne.libraries.error.compose")
    sourceSets.commonMain.dependencies {
        api(projects.rinneShared.libraries.error.core)
        implementation(projects.rinneShared.libraries.logger.core)
    }
}

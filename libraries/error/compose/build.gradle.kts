import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.compose)
}

kotlin {
    rinneAndroid("com.rinne.libraries.error.compose")
    sourceSets.commonMain.dependencies {
        api(projects.rinneShared.libraries.error.core)
        implementation(projects.rinneShared.libraries.logger.core)
    }
}
